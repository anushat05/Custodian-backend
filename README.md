# CustomerManagerService
CustomerManagerService is a Spring Boot-based RESTful API for managing customer records, designed for reliability, portability, and ease of integration. The project emphasizes testability, custom logic implementation (without built-in sort), and support for cloud and serverless deployment.

---
## 🔍 Overview

This RESTful service allows creation and retrieval of customer records. Key features include:

- Validation of request data
- Manual sorted insertion (without `.sort()`)
- File-based persistence to retain data across server restarts
- Modular, testable architecture (controller-service-persistence pattern)

---
## 💡 Architecture
<img width="468" alt="image" src="assets/flowDiagram.png">

---
## 🔧 Components

- `CustomerController` – handles API endpoints
- `CustomerService` – business logic and validation
- `CustomerPersistence` – handles file-based read/write
- `GlobalExceptionHandler` – centralized error handling
- `CustomerApiSimulator` – generates POST/GET requests for load testing

---
## 🛠 Key Features

- ✅ RESTful API with `POST` and `GET` endpoints
- ✅ Manual sorted insertion by `lastName`, then `firstName`
- ✅ Input validation using `@Valid` and custom logic
- ✅ Global exception handling for both schema and business rule violations
- ✅ File-based persistence (via Jackson) 
- ✅ JUnit + MockMvc test coverage
- ✅ API simulator to test performance under concurrency

---

## Phased Delivery Approach
### ⬆️ Phase 1: Core Functionality (MVP)
* Define model (Customer)
* Implement `/customers` POST and GET
* Manual insertion without `.sort()`
* File-based persistence with Jackson

### ⬆️ Phase 2: Enhancements
* Adding Unit Test and Integration Test
* Structured error handling
* Simulator
* Modular folder structure

---
## How to Run
### 🛠 Prerequisites
- Java 17+
- Maven 3.8+
- IntelliJ or any Java IDE
### 🏃 Running the App

#### Option 1: From IntelliJ
- Open `CustomerManagerServiceApplication.java`
- Right-click → **Run**

#### Option 2: Using Maven
```bash
./mvnw spring-boot:run
```
#### Option 3: Build & Run the JAR
```bash
./mvnw clean package
java -jar target/CustomerManagerService-0.0.1-SNAPSHOT.jar
```
---
## 📫 API Endpoints

### 🔹 POST /customers
Adds multiple customers at once.

**Requirements:**
* Must include at least 2 customers

* Each customer must have:

* `firstName`, `lastName` (non-empty)

* age between 10–90

* Sequentially increasing id
```JSON
[
  {
    "firstName": "Leia",
    "lastName": "Ray",
    "age": 25,
    "id": 1
  },
  {
    "firstName": "Frank",
    "lastName": "Anderson",
    "age": 30,
    "id": 2
  }
]
```

### 🔹 GET /customers

Returns the list of all customers, sorted by :
* `lastName`
*  then `firstName`

---
## 📝 Design Decisions & FAQs
### 1. Why did you choose Spring Boot?
Spring Boot offers:

* Fast setup and built-in support for REST APIs

* Strong testing ecosystem

* Auto-configuration and cloud-native features

* Easy extensibility for GCP, AWS, or Docker

* It allowed rapid delivery with a scalable, professional structure.

### 2. Why file-based persistence?
File persistence allows:

* Retaining state across server restarts

* Avoiding the complexity of database setup

* Easy cloning and testing by reviewers

---
## Future Improvements
* Replace file persistence with DynamoDB or GCS 
* Add Swagger/OpenAPI documentation 
* Add CI/CD pipeline (GitHub Actions)
* Implement request throttling/rate-limiting 
* Extend simulator with CLI flags and metrics