
# Task Manager

This is a simple Task Manager Application built with Spring Boot.

## Features

- ✅ Create Users  
- 📋 List All Users  
- 📝 Create Tasks  
- 🔁 Assign/Reassign Tasks to Users  
- 📦 List All Tasks  
- ❌ Delete Tasks  
- 👤 Fetch All Tasks Assigned to a User  
- 📌 Fetch All Assigned Tasks  

## Requirements

- Java 8 or higher  
- Maven  
- PostgreSQL  

## Clone the Repository

```sh
git clone https://github.com/Mohammed-Imthihyaz/Task-Manager.git
cd Task-Manager
```

## Database Configuration

Ensure you have **PostgreSQL installed and running**.

- Create a database named: `Task_Manager`

Then, open `src/main/resources/application.properties` and configure the following:

```properties
spring.application.name=task-manager
spring.datasource.url=jdbc:postgresql://localhost:5432/Task_Manager
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Running the Application

```sh
mvn clean install
mvn spring-boot:run
```

## API Documentation

### 🔹 Create User

- **Method:** POST  
- **URL:** http://localhost:8080/users/create  
- **Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

### 🔹 List All Users

- **Method:** GET  
- **URL:** http://localhost:8080/users/list

### 🔹 Create Task

- **Method:** POST  
- **URL:** http://localhost:8080/tasks/create  
- **Body:**
```json
{
  "title": "Fix bug #404",
  "description": "Fix the 404 page crash issue"
}
```

### 🔹 List All Tasks

- **Method:** GET  
- **URL:** http://localhost:8080/tasks/list

### 🔹 Assign Task to User

- **Method:** POST  
- **URL:** http://localhost:8080/tasks/assign  
- **Params:**
  - taskId: Task ID (e.g., 1)
  - userId: User ID (e.g., 3)

### 🔹 Reassign Task

- **Method:** POST  
- **URL:** http://localhost:8080/tasks/reassign  
- **Params:**
  - taskId: Task ID
  - userId: New User ID

### 🔹 Delete Task

- **Method:** DELETE  
- **URL:** http://localhost:8080/tasks/delete  
- **Params:**
  - taskId: Task ID

### 🔹 Fetch Tasks for a User

- **Method:** GET  
- **URL:** http://localhost:8080/users/get-user-task  
- **Params:**
  - userId: User ID

### 🔹 Get All Assigned Tasks

- **Method:** GET  
- **URL:** http://localhost:8080/tasks/getallassigendtasks

## Tools to Test

- Postman  
- cURL  


