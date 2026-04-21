package com.example.messenger.service;

import com.example.messenger.dto.*;
import com.example.messenger.model.User;
import com.example.messenger.model.UserSettings;
import com.example.messenger.model.UserStatus;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.security.JwtService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username уже занят");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже зарегистрирован");
        }

        User user = User.builder()
                .username(request.getUsername())
                .displayName(request.getDisplayName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ONLINE)
                .settings(new UserSettings())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserDTO.fromEntity(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        user.setStatus(UserStatus.ONLINE);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserDTO.fromEntity(user));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    public UserDTO getProfile(User user) {
        return UserDTO.fromEntity(user);
    }

    @Transactional
    public UserDTO updateProfile(User user, UpdateProfileRequest request) {
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username уже занят");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getSettings() != null) {
            user.setSettings(request.getSettings());
        }

        userRepository.save(user);
        return UserDTO.fromEntity(user);
    }

    @Transactional
    public void updateAvatar(User user, String avatarUrl) {
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }

    @Transactional
    public void updateStatus(User user, UserStatus status) {
        user.setStatus(status);
        if (status == UserStatus.OFFLINE) {
            user.setLastSeen(LocalDateTime.now());
        }
        userRepository.save(user);
    }

    public List<UserDTO> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(UserDTO::fromEntityPublic)
                .collect(Collectors.toList());
    }

    public boolean checkUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
}
