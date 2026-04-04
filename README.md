# 📬 Mailbox Messenger

A lightweight web messenger built with **Java 21**, **Spring Boot**, and **HTML**.

## 🚀 About

**Mailbox Messenger** is a simple web-based messaging application, and this project is meant to be just a hobby.

This is just the first version, and it's still very rough at this point.

## 🧱 Architecture

The application follows a standard layered architecture:

```
Controller → Service → Repository → Database
                ↓
             HTML Views
```

* **Controller** — handles HTTP requests
* **Service** — business logic
* **Repository** — database access
* **Frontend** — server-side rendered HTML

## ✨ Features

* 💬 Send and receive messages
* 👤 User authentication (login/register)
* 📜 Message history
* 🌐 Web interface (HTML templates)
* ⚡ Fast backend powered by Spring Boot

## 🛠️ Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA

### Frontend

* HTML
* CSS *(optional)*
* Thymeleaf / JSP *(depending on your implementation)*

## ⚙️ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/vt6719/mailbox_messenger.git
cd mailbox_messenger
```

### 2. Run the application

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

### 3. Open in browser

```
http://localhost:8080
```

## 🧪 Testing

```bash
mvn test
```

## 🔮 Future Improvements

* 📱 Responsive UI
* 🧾 Message attachments
* 🔒 Advanced security (JWT, OAuth2)
* 👥 Group chats

## 🤝 Contributing

Contributions are welcome!

1. Fork the project
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Authors

GitHub: https://github.com/vt6719
GitHub: https://github.com/vexolous

---

⭐ If you like this project, give it a star!
