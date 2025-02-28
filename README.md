# User Management System

This project is a full-stack application for user management, featuring a Spring Boot backend API and a Next.js frontend.

## Project Structure

- `backend/`: Spring Boot application providing REST APIs
- `frontend/`: Next.js application providing the user interface

## Backend

### Technologies Used

- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- H2 Database
- JWT for authentication
- Swagger/OpenAPI for documentation
- JavaFaker for generating realistic test data
- Lombok for reducing boilerplate code

### Getting Started

#### Prerequisites

- Java 17 or higher
- Gradle

#### Running the Backend

1. Navigate to the backend directory
2. Run the application:
   ```bash
   ./gradlew bootRun
   ```
3. The backend will start on port 9090

### API Documentation

Once the backend is running, you can access:
- Swagger UI: http://localhost:9090/swagger-ui/index.html
- API Docs: http://localhost:9090/api-docs
- H2 Console: http://localhost:9090/h2-console

### API Endpoints

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

### Security

- Passwords are encoded before storage
- JWT authentication
- Role-based access control (admin/user)

### Testing the Backend

The project includes a comprehensive test suite covering both controller and service layers.

#### Running Tests

To run all tests:
```bash
./gradlew test
```

To run a specific test class:
```bash
./gradlew test --tests "com.example.miniproject.controller.UserControllerTest"
```

#### Test Structure

1. **Controller Tests**
   - `UserGenerationControllerTest`: Tests for the user generation endpoint
   - `UserBatchImportTest`: Tests for the batch import functionality
   - `UserControllerTest`: Tests for user profile endpoints
   - `AuthControllerTest`: Tests for authentication endpoints

2. **Service Tests**
   - `UserServiceTest`: Tests for user management service
   - `AuthenticationServiceTest`: Tests for authentication service

#### Test Approach

- **Integration Tests**: Using `@SpringBootTest` to test the application as a whole
- **MockMvc**: For testing REST endpoints
- **Flexible Assertions**: Tests are designed to handle both client and server errors
- **Security Testing**: Includes tests for authenticated and unauthenticated access
- **Error Handling**: Tests various error scenarios to ensure robust error handling

#### Test Coverage

The test suite covers:
- Successful scenarios
- Error scenarios (client and server errors)
- Authentication and authorization
- Input validation
- Duplicate handling for batch imports
- Security constraints

## Frontend

### Technologies Used

- Next.js 14
- TypeScript
- Tailwind CSS
- Axios for API requests
- React Hot Toast for notifications

### Features

- **User Authentication**: Secure login with JWT
- **User Generation**: Generate realistic user data
- **Batch Import**: Upload JSON files with user data
- **Profile Management**: View your profile and other users' profiles (admin only)
- **Responsive Design**: Works on desktop and mobile devices

### Getting Started

#### Prerequisites

- Node.js 18.17 or later
- Backend running on port 9090

#### Running the Frontend

1. Navigate to the frontend directory
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. The frontend will start on port 3000
5. Open [http://localhost:3000](http://localhost:3000) in your browser

### Usage

#### Login

- Use the credentials of any user imported into the system
- The username field accepts either username or email

#### Dashboard

The dashboard provides access to the following features:

1. **Generate Users**:
   - Specify the number of users to generate
   - Download the generated JSON file

2. **Batch Import**:
   - Upload a JSON file containing user data
   - View import results (total records, success, failures)

3. **Profile View**:
   - View your own profile information

4. **User Search (Admin Only)**:
   - Search for users by username
   - View detailed user information


