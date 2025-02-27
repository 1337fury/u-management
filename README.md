# User Management API

This project is a Spring Boot application that provides REST APIs for user management, including user generation, authentication, and profile management.

## Technologies Used

- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- H2 Database
- JWT for authentication
- Swagger/OpenAPI for documentation
- JavaFaker for generating realistic test data
- Lombok for reducing boilerplate code

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```
4. The application will start on port 9090

### API Documentation

Once the application is running, you can access:
- Swagger UI: http://localhost:9090/swagger-ui/index.html
- API Docs: http://localhost:9090/api-docs
- H2 Console: http://localhost:9090/h2-console

## API Endpoints

1. **Generate Users**
   - `GET /api/users/generate?count={number}`
   - Generates specified number of users with realistic data

2. **Batch Upload Users**
   - `POST /api/users/batch`
   - Upload JSON file containing user data

3. **User Authentication**
   - `POST /api/auth`
   - Authenticate user and receive JWT token

4. **View My Profile**
   - `GET /api/users/me`
   - View own profile (requires authentication)

5. **View User Profile**
   - `GET /api/users/{username}`
   - View other user's profile (requires admin role)

## Security

- Passwords are encoded before storage
- JWT authentication
- Role-based access control (admin/user)

## Testing

The project includes a comprehensive test suite covering both controller and service layers.

### Running Tests

To run all tests:
```bash
./gradlew test
```

To run a specific test class:
```bash
./gradlew test --tests "com.example.miniproject.controller.UserControllerTest"
```

### Test Structure

1. **Controller Tests**
   - `UserGenerationControllerTest`: Tests for the user generation endpoint
   - `UserBatchImportTest`: Tests for the batch import functionality
   - `UserControllerTest`: Tests for user profile endpoints
   - `AuthControllerTest`: Tests for authentication endpoints

2. **Service Tests**
   - `UserServiceTest`: Tests for user management service
   - `AuthenticationServiceTest`: Tests for authentication service

### Test Approach

- **Integration Tests**: Using `@SpringBootTest` to test the application as a whole
- **MockMvc**: For testing REST endpoints
- **Flexible Assertions**: Tests are designed to handle both client and server errors
- **Security Testing**: Includes tests for authenticated and unauthenticated access
- **Error Handling**: Tests various error scenarios to ensure robust error handling

### Test Coverage

The test suite covers:
- Successful scenarios
- Error scenarios (client and server errors)
- Authentication and authorization
- Input validation
- Duplicate handling for batch imports
- Security constraints
