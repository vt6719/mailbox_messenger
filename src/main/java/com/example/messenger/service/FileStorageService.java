package com.example.messenger.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private Path rootLocation;
    private Path avatarsLocation;
    private Path mediaLocation;
    private Path voiceLocation;

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(uploadDir);
        avatarsLocation = rootLocation.resolve("avatars");
        mediaLocation = rootLocation.resolve("media");
        voiceLocation = rootLocation.resolve("voice");

        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(avatarsLocation);
            Files.createDirectories(mediaLocation);
            Files.createDirectories(voiceLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директории для загрузки файлов", e);
        }
    }

    public String storeAvatar(MultipartFile file) {
        return storeFile(file, avatarsLocation, "avatars");
    }

    public String storeMedia(MultipartFile file) {
        return storeFile(file, mediaLocation, "media");
    }

    public String storeVoice(MultipartFile file) {
        return storeFile(file, voiceLocation, "voice");
    }

    private String storeFile(MultipartFile file, Path location, String folder) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + extension;

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Файл пуст");
            }
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Недопустимое имя файла: " + originalFilename);
            }

            Path targetLocation = location.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/api/media/files/" + folder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл: " + originalFilename, e);
        }
    }

    public Resource loadFile(String folder, String filename) {
        try {
            Path file = rootLocation.resolve(folder).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Файл не найден: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка чтения файла: " + filename, e);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            String relativePath = fileUrl.replace("/api/media/files/", "");
            Path file = rootLocation.resolve(relativePath);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // Игнорируем ошибки удаления
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex >= 0 ? filename.substring(dotIndex) : "";
    }

    public String getContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            case ".mp4" -> "video/mp4";
            case ".webm" -> "video/webm";
            case ".ogg" -> "audio/ogg";
            case ".mp3" -> "audio/mpeg";
            case ".wav" -> "audio/wav";
            case ".pdf" -> "application/pdf";
            default -> "application/octet-stream";
        };
    }
}
