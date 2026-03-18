# ☕ Cafe Stock Management System

A production-ready **REST API** for managing cafe inventory, built with Java Spring Boot. Features JWT authentication, Google OAuth2, role-based access control, and full stock transaction tracking.

> 🎓 Built as a portfolio project to demonstrate real-world backend development skills.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [API Endpoints](#-api-endpoints)
- [Authentication](#-authentication)
- [Database Design](#-database-design)
- [Testing](#-testing)
- [Key Concepts Learned](#-key-concepts-learned)
- [Troubleshooting](#-troubleshooting)

---

## 🛠 Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security 7.x + JWT (jjwt 0.12.3) |
| OAuth2 | Google OAuth2 via Spring OAuth2 Client |
| Database | PostgreSQL (Supabase) |
| ORM | Spring Data JPA / Hibernate 7.x |
| Validation | Spring Validation (Jakarta) |
| Utilities | Lombok |
| Testing | JUnit 5 + Mockito + MockMvc |
| Build Tool | Maven |
| Server Port | 8089 |

---

## ✨ Features

- 🔐 **JWT Authentication** — Stateless token-based auth
- 🔑 **Google OAuth2** — Sign in with Google
- 👥 **Role-Based Access** — `ROLE_ADMIN` and `ROLE_STAFF`
- 📦 **Product Management** — Full CRUD with SKU tracking
- 🗂 **Category Management** — Organize products by category
- 🚚 **Supplier Management** — Track product suppliers
- 📊 **Stock Transactions** — RESTOCK, USAGE, WASTE, ADJUSTMENT
- ⚠️ **Low Stock Alerts** — Alert when stock falls below minimum
- 🛡 **Soft Delete** — Business entities are deactivated, not deleted
- 🕵️ **Audit Trail** — Every stock change records before/after values
- ✅ **21 Unit Tests** — Service layer + Controller layer tests
- 🌐 **Global Exception Handling** — Consistent error responses

---

## 📁 Project Structure

```
src/main/java/com/cafe/stockmanagement/
│
├── config/
│   └── SecurityConfig.java          # Spring Security rules & filter chain
│
├── controller/
│   ├── AuthController.java          # Register, Login, OAuth2 success
│   ├── CategoryController.java      # Category CRUD
│   ├── SupplierController.java      # Supplier CRUD
│   ├── ProductController.java       # Product CRUD + low stock + search
│   └── StockController.java         # Stock transactions
│
├── service/
│   ├── AuthService.java             # Register & login logic
│   ├── CategoryService.java         # Category business logic
│   ├── SupplierService.java         # Supplier business logic
│   ├── ProductService.java          # Product business logic
│   └── StockService.java            # Transaction processing
│
├── repository/
│   ├── UserRepository.java
│   ├── CategoryRepository.java
│   ├── SupplierRepository.java
│   ├── ProductRepository.java       # Custom queries: low stock, search
│   └── StockTransactionRepository.java
│
├── entity/
│   ├── BaseEntity.java              # Shared: id, createdAt, updatedAt
│   ├── User.java
│   ├── Category.java
│   ├── Supplier.java
│   ├── Product.java
│   └── StockTransaction.java
│
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── CategoryRequest.java
│   │   ├── SupplierRequest.java
│   │   ├── ProductRequest.java
│   │   └── StockTransactionRequest.java
│   └── response/
│       ├── ApiResponse.java         # Generic wrapper: {success, message, data}
│       ├── AuthResponse.java
│       ├── CategoryResponse.java
│       ├── SupplierResponse.java
│       ├── ProductResponse.java
│       ├── StockTransactionResponse.java
│       └── UserResponse.java
│
├── security/
│   ├── JwtTokenProvider.java        # Generate & validate JWT tokens
│   ├── JwtAuthFilter.java           # Filter: extract JWT from requests
│   ├── CustomUserDetailsService.java # Load user from DB for Spring Security
│   └── OAuth2SuccessHandler.java    # Handle Google login success
│
├── exception/
│   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
│
└── enums/
    ├── Role.java                    # ROLE_ADMIN, ROLE_STAFF
    └── TransactionType.java         # RESTOCK, USAGE, WASTE, ADJUSTMENT

src/test/java/com/cafe/stockmanagement/
├── config/
│   └── TestSecurityConfig.java
├── controller/
│   └── AuthControllerTest.java
└── service/
    ├── AuthServiceTest.java
    ├── CategoryServiceTest.java
    ├── ProductServiceTest.java
    └── StockServiceTest.java
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL database (local or [Supabase](https://supabase.com))
- Google Cloud Console project (for OAuth2)

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/cafe-stock-management.git
cd cafe-stock-management
```

### 2. Configure application.properties

```properties
# src/main/resources/application.properties

spring.application.name=cafe-stock-management

# Database (Supabase Transaction Pooler)
spring.datasource.url=jdbc:postgresql://YOUR_SUPABASE_HOST:6543/postgres
spring.datasource.username=postgres.YOUR_PROJECT_REF
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection Pool
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.data-source-properties.sslmode=require

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=email,profile
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8089/login/oauth2/code/google

# JWT
jwt.secret=cafestockmanagement2024supersecretkeythatisverylongandsecure
jwt.expiration=86400000

# Server
server.port=8089
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Tables are **automatically created** in your database via `ddl-auto=update`.

### 4. Run Tests

```bash
mvn test
```

Expected: `Tests run: 21, Failures: 0, Errors: 0`

---

## ⚙️ Configuration

### Supabase Setup

1. Create a project at [supabase.com](https://supabase.com)
2. Go to **Settings → Database → Connection Pooling**
3. Use **Transaction Pooler** (port `6543`)
4. Username format: `postgres.YOUR_PROJECT_REF`

> ⚠️ Use Transaction Pooler (port 6543), NOT the direct connection (port 5432), to avoid SSL/connection issues.

### Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Create a new project
3. Enable **Google+ API** or **People API**
4. Go to **Credentials → Create OAuth2 Client ID**
5. Set Authorized redirect URI: `http://localhost:8089/login/oauth2/code/google`
6. Copy Client ID and Secret into `application.properties`

### JWT Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `jwt.secret` | (see above) | Min 256-bit secret key |
| `jwt.expiration` | `86400000` | Token expiry in ms (24 hours) |

---

## 🌐 API Endpoints

### Authentication

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |
| GET | `/api/auth/oauth2/success` | Public | OAuth2 token exchange |
| GET | `/oauth2/authorize/google` | Public | Initiate Google login (browser) |

### Categories

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/categories` | Any | Get all categories |
| GET | `/api/categories/{id}` | Any | Get category by ID |
| POST | `/api/categories` | ADMIN | Create category |
| PUT | `/api/categories/{id}` | ADMIN | Update category |
| DELETE | `/api/categories/{id}` | ADMIN | Delete category |

### Suppliers

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/suppliers` | Any | Get all active suppliers |
| GET | `/api/suppliers/{id}` | Any | Get supplier by ID |
| POST | `/api/suppliers` | ADMIN | Create supplier |
| PUT | `/api/suppliers/{id}` | ADMIN | Update supplier |
| DELETE | `/api/suppliers/{id}` | ADMIN | Deactivate supplier |

### Products

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/products` | Any | Get all active products |
| GET | `/api/products/{id}` | Any | Get product by ID |
| GET | `/api/products/low-stock` | Any | Get low stock products |
| GET | `/api/products/search?name=` | Any | Search products by name |
| POST | `/api/products` | ADMIN | Create product |
| PUT | `/api/products/{id}` | ADMIN | Update product |
| DELETE | `/api/products/{id}` | ADMIN | Deactivate product |

### Stock Transactions

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/stock/transaction` | Any | Process stock transaction |
| GET | `/api/stock/transactions` | Any | Get all transactions |
| GET | `/api/stock/transactions/product/{id}` | Any | Get by product |
| GET | `/api/stock/transactions/type/{type}` | Any | Get by type |

---

## 🔐 Authentication

### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Hafiz",
  "email": "hafiz@cafe.com",
  "password": "password123"
}
```

Response:
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "name": "Hafiz",
    "email": "hafiz@cafe.com",
    "role": "ROLE_STAFF"
  }
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "hafiz@cafe.com",
  "password": "password123"
}
```

### Using the JWT Token

All protected endpoints require the token in the `Authorization` header:

```http
GET /api/products
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Google OAuth2 Flow

1. Open browser: `http://localhost:8089/oauth2/authorize/google`
2. Complete Google login
3. Redirected to: `GET /api/auth/oauth2/success?token=YOUR_JWT`
4. Use the JWT token for subsequent requests

### Role Escalation (Dev)

New users default to `ROLE_STAFF`. To make yourself ADMIN, run in Supabase SQL Editor:

```sql
UPDATE users SET role = 'ROLE_ADMIN' WHERE email = 'your@email.com';
```

---

## 🗃 Database Design

### Entity Relationship

```
users
  └── stock_transactions (many)

categories
  └── products (many)

suppliers
  └── products (many)

products
  └── stock_transactions (many)
```

### Tables

**users**
| Column | Type | Notes |
|--------|------|-------|
| id | BIGSERIAL | PK |
| name | VARCHAR | Not null |
| email | VARCHAR | Unique |
| password | VARCHAR | Null for OAuth2 users |
| role | VARCHAR | ROLE_ADMIN / ROLE_STAFF |
| google_id | VARCHAR | OAuth2 users only |
| profile_picture | VARCHAR | |
| is_active | BOOLEAN | Default true |
| created_at | TIMESTAMP | Auto |
| updated_at | TIMESTAMP | Auto |

**products**
| Column | Type | Notes |
|--------|------|-------|
| id | BIGSERIAL | PK |
| name | VARCHAR | Not null |
| sku | VARCHAR | Unique product code |
| unit_price | DECIMAL(10,2) | BigDecimal |
| unit | VARCHAR | kg, liters, pieces |
| current_stock | INTEGER | Default 0 |
| minimum_stock | INTEGER | Low stock threshold |
| category_id | BIGINT | FK → categories |
| supplier_id | BIGINT | FK → suppliers |
| is_active | BOOLEAN | Soft delete |

**stock_transactions**
| Column | Type | Notes |
|--------|------|-------|
| id | BIGSERIAL | PK |
| product_id | BIGINT | FK → products |
| user_id | BIGINT | FK → users |
| type | VARCHAR | RESTOCK/USAGE/WASTE/ADJUSTMENT |
| quantity | INTEGER | |
| stock_before | INTEGER | Audit trail |
| stock_after | INTEGER | Audit trail |
| total_cost | DECIMAL | Optional |
| notes | VARCHAR | Optional |

### Transaction Types

| Type | Stock Change | Use Case |
|------|-------------|---------|
| `RESTOCK` | +quantity | New stock from supplier |
| `USAGE` | -quantity | Used in cafe operations |
| `WASTE` | -quantity | Spoiled or damaged goods |
| `ADJUSTMENT` | =quantity | Manual correction (sets exact value) |

---

## 🧪 Testing

### Test Structure

```
21 tests total:
├── AuthServiceTest     (3 tests) — register success, duplicate email, login
├── CategoryServiceTest (4 tests) — get all, create, duplicate name, not found, update
├── ProductServiceTest  (5 tests) — get all, create, low stock, not found, deactivate
├── StockServiceTest    (4 tests) — restock, usage, insufficient stock, adjustment
└── AuthControllerTest  (3 tests) — register success, invalid email, login
```

### Run Tests

```bash
# All tests
mvn test

# Specific class
mvn test -Dtest=AuthServiceTest

# Specific method
mvn test -Dtest=StockServiceTest#processTransaction_Restock_IncreasesStock
```

### Testing Approach

- **Unit Tests** (`@ExtendWith(MockitoExtension.class)`) — Service layer with mocked dependencies
- **Controller Tests** (`@SpringBootTest + @AutoConfigureMockMvc`) — Full context with MockMvc
- **AAA Pattern** — Every test follows Arrange, Act, Assert
- **`@MockitoBean`** — Spring Boot 4.x replacement for `@MockBean`

### Key Spring Boot 4.x Test Changes

```java
// Old (Spring Boot 3.x)                    // New (Spring Boot 4.x)
@MockBean                          →         @MockitoBean
import ...boot.test.mock.mockito   →         import ...test.context.bean.override.mockito
import ...web.servlet.WebMvcTest   →         import ...boot.webmvc.test.autoconfigure
import ...web.servlet.AutoConfig.. →         import ...boot.webmvc.test.autoconfigure
```

---

## 🧠 Key Concepts Learned

| Concept | Where Applied |
|---------|--------------|
| Layered Architecture | Controller → Service → Repository |
| JWT Stateless Auth | JwtTokenProvider, JwtAuthFilter |
| Google OAuth2 | OAuth2SuccessHandler, SecurityConfig |
| Spring Security 7.x | DaoAuthenticationProvider constructor change |
| JPA Relationships | @OneToMany, @ManyToOne, @JoinColumn |
| FetchType.LAZY | All entity relationships |
| DTO Pattern | Prevents lazy loading errors, hides internals |
| BigDecimal | All price/money fields |
| Soft Delete | isActive=false instead of DELETE |
| Audit Trail | stockBefore/stockAfter in transactions |
| @MappedSuperclass | BaseEntity with id, createdAt, updatedAt |
| @Transactional | StockService transaction processing |
| Global Exception Handler | @RestControllerAdvice |
| BCrypt | Password hashing in AuthService |
| Builder Pattern | All entities via Lombok @Builder |

---

## 🔧 Troubleshooting

### Supabase Connection Issues

| Error | Fix |
|-------|-----|
| Connection timeout | Use Transaction Pooler port `6543`, not `5432` |
| SSL required | Add `sslmode=require` to hikari properties |
| Auth failed | Use `postgres.YOUR_REF` as username for pooler |
| Project paused | Resume project in Supabase dashboard (free tier pauses after inactivity) |
| Too many connections | HikariCP `maximum-pool-size=5` already handles this |

### Spring Security Issues

| Error | Fix |
|-------|-----|
| `NoSuchBeanDefinitionException: AuthenticationProvider` | Use `new DaoAuthenticationProvider(userDetailsService)` not empty constructor |
| OAuth2 login fails | Use `SessionCreationPolicy.IF_REQUIRED`, not `STATELESS` |
| 403 on all requests | Check JWT filter extracts token correctly |

### Lazy Loading Error

```
org.hibernate.LazyInitializationException: could not initialize proxy - no Session
```

**Fix:** Never return `@Entity` objects directly from controllers. Always convert to DTOs in service layer using `mapToResponse()`.

### Test Issues (Spring Boot 4.x)

| Error | Fix |
|-------|-----|
| `package ...web.servlet does not exist` | Add `spring-boot-starter-webmvc-test` dependency |
| `MockBean not found` | Replace `@MockBean` with `@MockitoBean` |
| `ObjectMapper not found` | Use `new ObjectMapper()` instead of `@Autowired` |
| `ApplicationContext failed` | Add `@MockitoBean` for all security beans |

---

## 📮 Postman Testing Order

1. `POST /api/auth/register` → save token
2. Update role to `ROLE_ADMIN` in Supabase → re-login
3. `POST /api/categories` → save `categoryId`
4. `POST /api/suppliers` → save `supplierId`
5. `POST /api/products` (with categoryId + supplierId) → save `productId`
6. `POST /api/stock/transaction` (RESTOCK, quantity: 100)
7. `POST /api/stock/transaction` (USAGE, quantity: 10)
8. `GET /api/products/low-stock` → verify alert threshold

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

Built by **Hafiz** as a learning portfolio project.

> *"Without tests: 'I think my code works.' With tests: 'I can PROVE my code works.'"*
