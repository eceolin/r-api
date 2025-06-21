# Recipe Management API

A RESTful API for managing cooking recipes built with Spring Boot 3.5.0 and Java 17.

## Features

- Complete CRUD operations for recipes
- Advanced search functionality with multiple filters
- Input validation
- PostgreSQL database integration
- Comprehensive error handling
- Unit and integration tests

## Technical Stack

- **Java** 17
- **Spring Boot** 3.5.0
- **Spring Data JPA** with Hibernate
- **PostgreSQL** 14.17
- **Project Lombok**
- **Maven**
- **JUnit 5** for testing
- **H2** for test database

## Prerequisites

### For Docker Compose:
- Docker
- Docker Compose

### For Manual Setup:
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 14.17
- Git

## Setup Instructions

### Option 1: Docker Compose (Recommended)

The easiest way to run the application is using Docker Compose:

```bash
# Clone the repository
git clone <repository-url>
cd recime

# Start the application and database
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down

# Stop and remove volumes (careful - this deletes data)
docker-compose down -v
```

The API will be available at `http://localhost:8080`

### Option 2: Manual Setup

#### 1. Clone the Repository

```bash
git clone <repository-url>
cd recime
```

#### 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE recipedb;
```

Update database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/recipedb
spring.datasource.username=postgres
spring.datasource.password=postgres
```

#### 3. Build the Project

```bash
mvn clean install
```

#### 4. Run the Application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Documentation

Once the application is running, you can access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## API Endpoints

### Create Recipe
```http
POST /api/recipes
Content-Type: application/json

{
  "title": "Pasta Carbonara",
  "description": "Classic Italian pasta dish",
  "ingredients": ["Pasta", "Eggs", "Bacon", "Parmesan"],
  "instructions": "Cook pasta, fry bacon, mix with eggs and cheese",
  "vegetarian": false,
  "servings": 4
}
```

### Get Recipe by ID
```http
GET /api/recipes/{id}
```

### Get All Recipes
```http
GET /api/recipes
```

### Update Recipe
```http
PUT /api/recipes/{id}
Content-Type: application/json

{
  "title": "Updated Recipe",
  "description": "Updated description",
  "ingredients": ["New ingredients"],
  "instructions": "Updated instructions",
  "vegetarian": true,
  "servings": 2
}
```

### Delete Recipe
```http
DELETE /api/recipes/{id}
```

### Search Recipes
```http
GET /api/recipes?vegetarian=true&servings=4&includeIngredients=pasta&excludeIngredients=meat&contentInstructions=boil
```

Query Parameters:
- `vegetarian` (Boolean): Filter by vegetarian recipes
- `servings` (Integer): Filter by number of servings
- `includeIngredients` (List<String>): Include recipes containing these ingredients (can be specified multiple times)
- `excludeIngredients` (List<String>): Exclude recipes containing these ingredients (can be specified multiple times)
- `contentInstructions` (String): Search in recipe instructions

## Project Structure

```
src/
├── main/
│   ├── java/com/recime/api/
│   │   ├── controller/       # REST controllers
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # JPA entities
│   │   ├── exception/        # Custom exceptions
│   │   ├── repository/       # Data repositories
│   │   └── service/          # Business logic
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/recime/api/
        ├── controller/       # Controller tests
        └── service/          # Service tests
```

## Design Decisions

### 1. Architecture
- **Layered Architecture**: Clear separation between Controller, Service, and Repository layers
- **DTO Pattern**: Using DTOs for request/response to decouple API from domain model
- **Repository Pattern**: Abstracting data access logic

### 2. Database Design
- `Recipe` entity with separate `Ingredient` entities for normalized ingredient storage
- Automatic timestamp management with JPA lifecycle callbacks
- Custom JPQL query with LEFT JOIN for optimized ingredient filtering
- Database-level ingredient filtering for better performance

### 3. Error Handling
- Global exception handler for consistent error responses
- Custom `ResourceNotFoundException` for 404 scenarios
- Validation error handling with detailed field-level messages

### 4. Testing Strategy
- Unit tests for Service layer with Mockito
- Integration tests for REST endpoints using MockMvc
- H2 in-memory database for test isolation

### 5. API Design
- RESTful conventions for all endpoints
- Unified search functionality integrated into the main GET /api/recipes endpoint
- Support for multiple include/exclude ingredients as query parameters
- Request validation using Bean Validation annotations
- Case-insensitive ingredient filtering

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=RecipeServiceTest

# Run with coverage
mvn clean test jacoco:report
```