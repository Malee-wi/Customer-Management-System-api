# Customer Management System

This is a **Customer Management System** developed using **Jakarta EE**, **Spring Boot**, **Spring Data JPA** and **Spring MVC**. The project uses **Java SDK 22** and **Lombok** to simplify code.

## Project Structure

The project follows a standard structure, separating different layers and concerns:


## Features

1. **Entity Management:**
    - `Customer`, `MobileNumber`, and `Address` entities are defined with proper JPA annotations.
    - Relationships and ORM mappings are established to manage the database seamlessly.

2. **Spring Data JPA Repository:**
    - Repository layer provides CRUD functionality to persist entities to the underlying database.

3. **Restful API**
    - The application is expected to expose RESTful endpoints for external service communication.

4. **Unit Testing:**
    - Test cases are included for validating business logic and ensuring API correctness.

5. **Lombok Integration:**
    - Reduces boilerplate code with annotations like `@Getter`, `@Setter`, `@Builder` and more.

## Requirements

- **Java SDK 22**
- **Maven** (to manage dependencies and build the project)

## Setup Instructions

1. Clone the repository:

   ```bash
   git clone [REPO_URL]
   cd customer-management-system
   ```

2. Build the project:

   ```bash
   ./mvnw clean install
   ```

3. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Access the application at:

   ```
   http://localhost:8080
   ```

## Technology Stack

- **Language:** Java 
- **Frameworks:**
    - Spring Boot
    - Spring Data JPA
    - Spring MVC
- **Database:** Maria DB
- **Database Management:** JPA with Hibernate


## Developer Notes

- This project assumes an initial database setup; ensure to configure the database connection in `application.properties`.
- Lombok annotations are used for entities, so ensure your IDE supports Lombok (for IntelliJ IDEA, install the Lombok Plugin).
- Tests are located in `src/test/java` for validating all operations.
-  Load Database Scripts:
    - Apply the scripts located in the `database/schema.sql` folder to initialize database structure.
    - Run the scripts in the `database/data.sql` folder to populate initial data.




---

**Developed by Maleesha Wijekoon**
