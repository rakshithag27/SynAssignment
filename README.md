# SynAssignment - User Image Upload REST API

A Spring Boot RESTful API that allows users to register, log in with JWT authentication, and securely upload, view, and delete images via Cloudinary.

---

## Features

- User registration and login with JWT-based security
- Upload, view, and delete images using Cloudinary
- Protected image endpoints with role-based access
- Exception handling and validation for safe operations

---

## Technologies Used

- Java 17  
- Spring Boot 3.x  
- Spring Security (JWT)  
- Cloudinary API  
- H2 / MySQL (JPA)  
- Maven

---

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/rakshithag27/SynAssignment.git
   cd SynAssignment
2. **Configure application properties**
- Set your environment variables or edit src/main/resources/application.properties:
jwt.secret=your_jwt_secret
cloudinary.cloud_name=your_cloud_name
cloudinary.api_key=your_api_key
cloudinary.api_secret=your_api_secret

3. **Build and run the app**
- ./mvnw spring-boot:run

Authentication
- Login with valid credentials at `/synassignment/users/login`
- Receive a **JWT token** in the response
- Use this token as a **Bearer token** in the `Authorization` header for all protected image routes

API Endpoints

### Authentication
- `POST /synassignment/users/register` – Register a new user
- `POST /synassignment/users/login` – Authenticate and get JWT token

### Image Management
- `POST /synassignment/images/upload` – Upload image (**requires JWT**)
- `GET /synassignment/images/all` – View all uploaded images of a user (**requires JWT**)
- `DELETE /synassignment/images/delete?publicId={id}` – Delete an image by its public ID (**requires JWT**)

### License
- This project is an assignment

---


