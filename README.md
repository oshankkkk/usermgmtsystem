# User Management System

A Spring Boot application for managing users, with a Thymeleaf web UI and a REST API, backed by Microsoft SQL Server.

## Prerequisites

- **JDK 21** (check with `java -version`, and make sure `JAVA_HOME` is set)
- **Microsoft SQL Server** 2019 or 2022 (Developer or Express), running locally or in Docker
- **SSMS** (optional, for inspecting the database)
- **Maven** (optional, the included `mvnw` wrapper works without it)
- An IDE such as IntelliJ IDEA

## 1. Set up the database

1. Make sure SQL Server is running and TCP/IP is enabled on port **1433** (SQL Server Configuration Manager → Network Configuration → TCP/IP).
2. Make sure SQL Server login with a username and password is allowed (Mixed Mode authentication).
3. Create an empty database named `usermgmt`:

   ```sql
   CREATE DATABASE usermgmt;
   ```

The `users` table is created automatically by Hibernate the first time the app starts, so no schema script is needed.

## 2. Configure the connection

Open `src/main/resources/application.properties` and set your own SQL Server username and password:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=usermgmt;encrypt=true;trustServerCertificate=true
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
```

`trustServerCertificate=true` lets the driver accept the self-signed certificate of a local SQL Server install.

## 3. Run the application

**From the IDE:** open `UserManagementSystem.java` and run its `main` method.

**From the command line:**

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

When the console shows `Tomcat started on port 8080`, the app is ready.

## 4. Use the application

| What | URL |
|---|---|
| Web UI (user list) | http://localhost:8080/users |
| REST API | http://localhost:8080/api/users |


## Project structure

```
com.example.demo
├── controller   MVC and REST controllers
├── service      business logic
├── repository   Spring Data JPA and JdbcTemplate repositories
├── model        JPA entity and Role enum
├── dto          request/response objects
└── exception    custom exceptions and exception handlers
```
