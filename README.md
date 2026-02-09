# Test Automation Platform - Backend

<div align="center">
  <img src="https://raw.githubusercontent.com/bugra-university/test_web_v2_backend/master/docs/images/vizja-logo.png" alt="Uniwersytet Vizja" width="300"/>
  
  ### Graduation Thesis Project
  **University of Economics and Human Sciences in Warsaw**
  
  **Student:** Buğra Han - 42078
  
  ---
  
  [![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
  [![TestNG](https://img.shields.io/badge/TestNG-7.7.0-red.svg)](https://testng.org/)
  [![Selenium](https://img.shields.io/badge/Selenium-4.8.3-green.svg)](https://www.selenium.dev/)
  
</div>

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

This is the **backend service** of the Test Automation Platform, a comprehensive web-based solution for managing and executing automated test cases. The platform enables teams to:

- 📊 Manage test suites and test cases
- 🚀 Execute automated tests with Selenium WebDriver
- 📈 Track test execution progress in real-time
- 📝 Generate detailed test reports
- ⏰ Schedule automated test runs
- 🔄 Monitor step-by-step test execution

This project serves as a **graduation thesis** demonstrating modern software engineering practices, including microservices architecture, real-time communication, and automated testing frameworks.

---

## ✨ Features

### Core Functionality
- ✅ **Project Management** - Create and manage multiple test automation projects
- ✅ **Test Suite Organization** - Organize tests by user stories and test cases
- ✅ **Excel Import** - Import test cases from Excel files (Product Backlog format)
- ✅ **Test Execution** - Execute tests asynchronously with Selenium WebDriver
- ✅ **Real-time Updates** - Server-Sent Events (SSE) for live test progress
- ✅ **Step Tracking** - Monitor individual test step execution
- ✅ **Report Generation** - Automated HTML reports with ExtentReports
- ✅ **Test Scheduling** - Schedule recurring test executions
- ✅ **Screenshot Capture** - Automatic screenshots on test start/end/failure

### Advanced Features
- 🔄 **Asynchronous Execution** - Non-blocking test execution using CompletableFuture
- 📡 **WebSocket Support** - Real-time bidirectional communication
- 🎯 **Multi-browser Support** - Chrome, Firefox, Edge compatibility
- 🖥️ **Headless Mode** - Run tests without GUI for CI/CD integration
- 📊 **Execution Analytics** - Track test run statistics and trends
- 🔐 **JWT Authentication** - Secure API endpoints with JSON Web Tokens

---

## 🏗️ Architecture

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   Frontend      │◄────►│   Backend API   │◄────►│   PostgreSQL    │
│   (React)       │ HTTP │   (Spring Boot) │ JDBC │   Database      │
└─────────────────┘      └─────────────────┘      └─────────────────┘
                                 │
                                 │ Controls
                                 ▼
                         ┌─────────────────┐
                         │  Selenium       │
                         │  WebDriver      │
                         └─────────────────┘
                                 │
                                 │ Automates
                                 ▼
                         ┌─────────────────┐
                         │  Test Site      │
                         │  (Dummy App)    │
                         └─────────────────┘
```

### Key Components

1. **REST API Layer** - Spring Boot controllers handling HTTP requests
2. **Service Layer** - Business logic for test execution and management
3. **Repository Layer** - JPA repositories for database operations
4. **Test Execution Engine** - TestNG + Selenium WebDriver integration
5. **Real-time Communication** - SSE for live updates to frontend
6. **Step Tracking System** - HTTP-based step progress reporting

---

## 🛠️ Tech Stack

### Core Technologies
- **Java 17** - Programming language
- **Spring Boot 2.7.5** - Application framework
- **Spring Data JPA** - Database ORM
- **PostgreSQL 15** - Relational database
- **Maven** - Build and dependency management

### Testing & Automation
- **TestNG 7.7.0** - Test execution framework
- **Selenium WebDriver 4.8.3** - Browser automation
- **WebDriverManager** - Automatic driver management
- **ExtentReports** - HTML report generation

### Additional Libraries
- **Apache POI** - Excel file parsing
- **Jackson** - JSON serialization
- **Hibernate Types** - JSON/Array type support
- **JWT (jjwt)** - Authentication tokens
- **Lombok** - Boilerplate code reduction

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+
- Chrome/Firefox browser (for test execution)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/bugra-university/test_web_v2_backend.git
   cd test_web_v2_backend
   ```

2. **Configure database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE test_automation;
   ```
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/test_automation
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Run database migrations**
   ```bash
   # The schema will be created automatically on first run
   # Or manually execute scripts in database/init/
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8081`

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | `8081` |
| `DB_URL` | Database URL | `jdbc:postgresql://localhost:5432/test_automation` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing key | `your-secret-key` |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` |

---

## 📚 API Documentation

### Base URL
```
http://localhost:8081/api
```

### Authentication

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### Projects

#### Get All Projects
```http
GET /projects
Authorization: Bearer {token}
```

#### Create Project
```http
POST /projects
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "My Test Project",
  "description": "Project description"
}
```

### Test Suites

#### Get Test Suites
```http
GET /projects/{projectId}/test-suites
Authorization: Bearer {token}
```

#### Run Test Case
```http
POST /projects/{projectId}/test-suites/test-cases/{testCaseId}/run
Authorization: Bearer {token}
Content-Type: application/json

{
  "isHeadless": false,
  "browser": "chrome"
}
```

### Real-time Events

#### Subscribe to Test Events
```http
GET /projects/{projectId}/test-suites/events
Accept: text/event-stream
Authorization: Bearer {token}
```

Events:
- `test_case_started` - Test execution started
- `test_case_completed` - Test execution finished
- `step_started` - Test step started
- `step_completed` - Test step completed
- `step_failed` - Test step failed

---

## 🗄️ Database Schema

### Core Tables

#### `projects`
- `id` (BIGSERIAL PRIMARY KEY)
- `name` (VARCHAR)
- `description` (TEXT)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

#### `product_backlog_items`
- `id` (BIGSERIAL PRIMARY KEY)
- `project_id` (BIGINT FK)
- `user_story_id` (VARCHAR)
- `user_story_title` (TEXT)
- `acceptance_criteria` (TEXT)

#### `test_cases`
- `id` (BIGSERIAL PRIMARY KEY)
- `project_id` (BIGINT FK)
- `user_story_id` (VARCHAR)
- `test_case_id` (VARCHAR)
- `test_case_title` (TEXT)
- `test_class_name` (VARCHAR)
- `test_method_name` (VARCHAR)

#### `test_steps`
- `id` (BIGSERIAL PRIMARY KEY)
- `test_case_id` (BIGINT FK)
- `step_number` (INTEGER)
- `step_description` (TEXT)
- `expected_result` (TEXT)
- `status` (VARCHAR)

#### `test_runs`
- `id` (BIGSERIAL PRIMARY KEY)
- `project_id` (BIGINT FK)
- `execution_id` (VARCHAR)
- `status` (VARCHAR)
- `start_time` (TIMESTAMP)
- `end_time` (TIMESTAMP)
- `browser` (VARCHAR)
- `is_headless` (BOOLEAN)

#### `test_results`
- `id` (BIGSERIAL PRIMARY KEY)
- `test_run_id` (BIGINT FK)
- `test_case_id` (BIGINT FK)
- `status` (VARCHAR)
- `duration_ms` (BIGINT)
- `error_message` (TEXT)
- `screenshot_path` (VARCHAR)

---

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Run Specific Test Class
```bash
mvn test -Dtest=TestExecutionServiceTest
```

### Test Coverage
```bash
mvn clean test jacoco:report
# Report available at: target/site/jacoco/index.html
```

---

## 📁 Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/vizja/testweb/
│   │   │   ├── controller/          # REST API controllers
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # JPA repositories
│   │   │   ├── model/               # Entity classes
│   │   │   ├── security/            # JWT & authentication
│   │   │   └── Application.java     # Main application
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   └── test/
│       └── java/com/vizja/testweb/
│           ├── tests/               # Selenium test classes
│           │   ├── us01/            # User Story 01 tests
│           │   ├── us02/            # User Story 02 tests
│           │   ├── us03/            # User Story 03 tests
│           │   └── us04/            # User Story 04 tests
│           ├── pages/               # Page Object Model
│           └── utilities/           # Test utilities
│               ├── Driver.java
│               ├── StepTracker.java
│               └── EnhancedTestListener.java
├── database/
│   └── init/                        # SQL initialization scripts
├── TestOutput/
│   ├── screenshots/                 # Test screenshots
│   └── reports/                     # HTML reports
├── pom.xml
└── README.md
```

---

## 🤝 Contributing

This is a graduation thesis project. Contributions are welcome for educational purposes.

### Development Workflow

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is part of a graduation thesis at the **University of Economics and Human Sciences in Warsaw**.

**Student:** Buğra Han (ID: 42078)  
**Academic Year:** 2025/2026

---

## 📞 Contact

**Buğra Han**  
Student ID: 42078  
University of Economics and Human Sciences in Warsaw

---

<div align="center">
  <p>Made with ❤️ for graduation thesis</p>
  <p>© 2026 Uniwersytet Vizja - All Rights Reserved</p>
</div>
