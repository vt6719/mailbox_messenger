# Mailbox Messenger

A full-featured web messenger built with **Java 21**, **Spring Boot 3.3**, **WebSocket**, **WebRTC**, and **PostgreSQL**.

## About

**Mailbox Messenger** is a modern web-based messaging application inspired by popular messengers like Telegram and WhatsApp. It provides real-time messaging, voice/video calls, media sharing, and user customization features.

## Features

### User System
- User registration and authentication with JWT tokens
- Customizable user profiles (display name, bio, avatar)
- Unique usernames (@username)
- Online/offline status tracking
- Privacy settings (show online status, read receipts, typing indicator)

### Messaging
- Real-time messaging via WebSocket (STOMP/SockJS)
- Private chats (1-on-1)
- Group chats with multiple participants
- Message types: text, voice, image, video, file
- Reply to messages
- Edit and delete messages
- Message search within chats
- Read receipts
- "Typing..." indicator

### Voice Messages
- Record voice messages directly in browser
- Audio playback with duration display
- MediaRecorder API integration

### Audio/Video Calls
- WebRTC-based peer-to-peer calls
- Audio calls
- Video calls
- Call controls (mute microphone, disable camera, end call)
- Incoming call notifications

### Media Sharing
- Upload and share images
- Upload and share videos
- File attachments (up to 50MB)
- Image/video preview in chat

### Customization
- Light and dark themes
- Customizable accent color
- Profile avatar upload

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         Frontend                            │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────────────┐ │
│  │  Auth   │  │  Chat   │  │ Profile │  │ Voice/Video Call│ │
│  │  Modal  │  │   UI    │  │  Modal  │  │      Modal      │ │
│  └─────────┘  └─────────┘  └─────────┘  └─────────────────┘ │
│         │          │            │               │           │
│         └──────────┴────────────┴───────────────┘           │
│                          │                                  │
│              ┌───────────┴───────────┐                      │
│              │  REST API  │ WebSocket │                     │
│              └───────────────────────┘                      │
└─────────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────────┐
│                         Backend                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    Controllers                       │    │
│  │  Auth │ User │ Chat │ Media │ Call (WebSocket)      │    │
│  └─────────────────────────────────────────────────────┘    │
│                          │                                  │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                     Services                         │    │
│  │  UserService │ ChatService │ FileStorage │ Online   │    │
│  └─────────────────────────────────────────────────────┘    │
│                          │                                  │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                   Repositories                       │    │
│  │      UserRepository │ ChatRepository │ Message      │    │
│  └─────────────────────────────────────────────────────┘    │
│                          │                                  │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    Security                          │    │
│  │         JWT Authentication │ Spring Security        │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────────┐
│                       PostgreSQL                            │
│     users │ chats │ chat_participants │ chat_messages       │
└─────────────────────────────────────────────────────────────┘
```

## Tech Stack

### Backend
- **Java 21**
- **Spring Boot 3.3.0**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access
- **Spring WebSocket** - Real-time messaging (STOMP/SockJS)
- **JWT (jjwt 0.12.5)** - Token-based authentication
- **PostgreSQL** - Primary database
- **Lombok** - Boilerplate reduction

### Frontend
- **HTML5 / CSS3 / JavaScript**
- **SockJS + STOMP.js** - WebSocket client
- **WebRTC** - Peer-to-peer audio/video calls
- **MediaRecorder API** - Voice message recording

## Project Structure

```
src/main/java/com/example/messenger/
├── config/
│   ├── SecurityConfig.java          # Spring Security configuration
│   └── WebSocketConfig.java         # WebSocket/STOMP configuration
├── controller/
│   ├── AuthController.java          # Registration, login endpoints
│   ├── UserController.java          # User profile endpoints
│   ├── ChatController.java          # Chat and message endpoints
│   ├── MediaController.java         # File upload/download endpoints
│   └── CallController.java          # WebRTC signaling (WebSocket)
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── UserDTO.java
│   ├── ChatDTO.java
│   ├── MessageDTO.java
│   ├── CreateChatRequest.java
│   ├── SendMessageRequest.java
│   └── UpdateProfileRequest.java
├── model/
│   ├── User.java                    # User entity
│   ├── UserSettings.java            # Embedded user settings
│   ├── UserStatus.java              # Online status enum
│   ├── Chat.java                    # Chat entity
│   ├── ChatType.java                # PRIVATE, GROUP enum
│   ├── ChatMessage.java             # Message entity
│   ├── MessageType.java             # TEXT, VOICE, IMAGE, etc.
│   └── CallSignal.java              # WebRTC signaling model
├── repository/
│   ├── UserRepository.java
│   ├── ChatRepository.java
│   └── MessageRepository.java
├── security/
│   ├── JwtService.java              # JWT token operations
│   └── JwtAuthenticationFilter.java # JWT filter
├── service/
│   ├── UserService.java             # User business logic
│   ├── ChatService.java             # Chat/message business logic
│   ├── FileStorageService.java      # File upload handling
│   └── OnlineStatusService.java     # Online/typing status
└── MessengerApplication.java

src/main/resources/
├── static/
│   └── index.html                   # Single-page application
└── application.properties           # Configuration
```

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- PostgreSQL 14+

### 1. Clone the repository

```bash
git clone https://github.com/vt6719/mailbox_messenger.git
cd mailbox_messenger
```

### 2. Setup PostgreSQL

Create a database:

```sql
CREATE DATABASE messenger;
```

Update credentials in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/messenger
spring.datasource.username=postgres
spring.datasource.password=password
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

### 4. Open in browser

```
http://localhost:8080
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get current user profile |
| PUT | `/api/users/me` | Update profile |
| POST | `/api/users/avatar` | Upload avatar |
| GET | `/api/users/search?query=` | Search users by username |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/{id}/status` | Get user online status |

### Chats
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/chats` | Get user's chats |
| POST | `/api/chats` | Create new chat |
| GET | `/api/chats/private/{userId}` | Get or create private chat |
| GET | `/api/chats/{chatId}/messages` | Get chat messages (paginated) |
| GET | `/api/chats/{chatId}/messages/all` | Get all chat messages |
| POST | `/api/chats/{chatId}/read` | Mark messages as read |
| GET | `/api/chats/{chatId}/search?query=` | Search messages in chat |
| POST | `/api/chats/{chatId}/participants` | Add participants to group |
| DELETE | `/api/chats/{chatId}/leave` | Leave chat |

### Messages
| Method | Endpoint | Description |
|--------|----------|-------------|
| DELETE | `/api/messages/{messageId}` | Delete message |
| PUT | `/api/messages/{messageId}` | Edit message |

### Media
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/media/upload` | Upload media file |
| POST | `/api/media/voice` | Upload voice message |
| GET | `/api/media/{folder}/{filename}` | Get media file |

## WebSocket Endpoints

Connect to: `ws://localhost:8080/ws-chat` (with SockJS)

### Chat Messages
| Destination | Description |
|-------------|-------------|
| `/app/chat.sendMessage` | Send message |
| `/app/chat.typing` | Send typing indicator |
| `/app/chat.read` | Mark messages as read |
| `/topic/chat/{chatId}` | Subscribe to chat messages |
| `/topic/chat/{chatId}/read` | Subscribe to read receipts |
| `/topic/chat/{chatId}/typing` | Subscribe to typing indicators |

### Calls (WebRTC Signaling)
| Destination | Description |
|-------------|-------------|
| `/app/call.initiate` | Initiate a call |
| `/app/call.accept` | Accept incoming call |
| `/app/call.reject` | Reject incoming call |
| `/app/call.end` | End call |
| `/app/call.offer` | Send WebRTC offer |
| `/app/call.answer` | Send WebRTC answer |
| `/app/call.ice-candidate` | Send ICE candidate |
| `/user/queue/call` | Receive call signals |

## Configuration

Key settings in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/messenger
spring.datasource.username=postgres
spring.datasource.password=password

# JWT
jwt.secret=your-256-bit-secret-key
jwt.expiration=86400000  # 24 hours

# File uploads
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
file.upload-dir=./uploads

# Server
server.port=8080
```

## Testing

```bash
mvn test
```

## Contributing

Contributions are welcome!

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Authors

- GitHub: https://github.com/vt6719
- GitHub: https://github.com/vexolous

---

If you like this project, give it a star!
