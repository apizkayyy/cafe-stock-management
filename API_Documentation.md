# ☕ Cafe Stock Management System — API Documentation

**Base URL:** `http://localhost:8089`  
**Version:** 1.0.0  
**Auth:** Bearer JWT Token (in `Authorization` header)

---

## 📋 Table of Contents

1. [Response Format](#1-response-format)
2. [Authentication](#2-authentication)
3. [Categories API](#3-categories-api)
4. [Suppliers API](#4-suppliers-api)
5. [Products API](#5-products-api)
6. [Stock Transactions API](#6-stock-transactions-api)
7. [Error Codes](#7-error-codes)
8. [Data Models](#8-data-models)

---

## 1. Response Format

All responses follow a consistent wrapper:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { }
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

| Field | Type | Description |
|-------|------|-------------|
| `success` | Boolean | `true` = success, `false` = error |
| `message` | String | Human-readable description |
| `data` | Object/Array/null | Payload |

---

## 2. Authentication

### 2.1 Register

Creates a new user account. All new users default to `ROLE_STAFF`.

```
POST /api/auth/register
```

**Request Body:**

```json
{
  "name": "Hafiz Ahmad",
  "email": "hafiz@cafe.com",
  "password": "password123"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `name` | String | ✅ | Not blank |
| `email` | String | ✅ | Valid email format |
| `password` | String | ✅ | Min 6 characters |

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWZpekBjYWZlLmNvbSJ9...",
    "name": "Hafiz Ahmad",
    "email": "hafiz@cafe.com",
    "role": "ROLE_STAFF"
  }
}
```

**Error — Email already exists `400`:**

```json
{
  "success": false,
  "message": "Email already registered",
  "data": null
}
```

---

### 2.2 Login

```
POST /api/auth/login
```

**Request Body:**

```json
{
  "email": "hafiz@cafe.com",
  "password": "password123"
}
```

| Field | Type | Required |
|-------|------|----------|
| `email` | String | ✅ |
| `password` | String | ✅ |

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "name": "Hafiz Ahmad",
    "email": "hafiz@cafe.com",
    "role": "ROLE_ADMIN"
  }
}
```

**Error — Bad credentials `401`:**

```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

---

### 2.3 Google OAuth2 Login

**Step 1 — Initiate Login (open in browser):**

```
GET /oauth2/authorize/google
```

This redirects to Google's login page. After the user approves, Google redirects back.

**Step 2 — Get JWT Token:**

```
GET /api/auth/oauth2/success?token={jwt_token}
```

| Query Param | Description |
|-------------|-------------|
| `token` | JWT token returned after Google login success |

**Response `200 OK`:**

```json
{
  "success": true,
  "message": "OAuth2 login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "name": "Hafiz Ahmad",
    "email": "hafiz@gmail.com",
    "role": "ROLE_STAFF"
  }
}
```

---

### 2.4 Using the JWT Token

Include the token in the `Authorization` header for all protected endpoints:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Token Details:**
- Algorithm: `HS256`
- Expiry: 24 hours (86400000 ms)
- Subject: user email

---

## 3. Categories API

### 3.1 Get All Categories

```
GET /api/categories
Authorization: Bearer {token}
```

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Categories retrieved",
  "data": [
    {
      "id": 1,
      "name": "Coffee Beans",
      "description": "All types of coffee beans",
      "createdAt": "2026-03-01T10:00:00",
      "updatedAt": "2026-03-01T10:00:00"
    },
    {
      "id": 2,
      "name": "Dairy",
      "description": "Milk and dairy products",
      "createdAt": "2026-03-01T10:05:00",
      "updatedAt": "2026-03-01T10:05:00"
    }
  ]
}
```

---

### 3.2 Get Category by ID

```
GET /api/categories/{id}
Authorization: Bearer {token}
```

**Path Parameter:**

| Param | Type | Description |
|-------|------|-------------|
| `id` | Long | Category ID |

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Category retrieved",
  "data": {
    "id": 1,
    "name": "Coffee Beans",
    "description": "All types of coffee beans",
    "createdAt": "2026-03-01T10:00:00",
    "updatedAt": "2026-03-01T10:00:00"
  }
}
```

**Error — Not found `404`:**

```json
{
  "success": false,
  "message": "Category not found with id: 999",
  "data": null
}
```

---

### 3.3 Create Category

```
POST /api/categories
Authorization: Bearer {token}    ← ROLE_ADMIN required
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "Syrups",
  "description": "Flavour syrups and sweeteners"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `name` | String | ✅ | Not blank, unique |
| `description` | String | ❌ | Optional |

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Category created",
  "data": {
    "id": 3,
    "name": "Syrups",
    "description": "Flavour syrups and sweeteners",
    "createdAt": "2026-03-10T09:00:00",
    "updatedAt": "2026-03-10T09:00:00"
  }
}
```

**Error — Duplicate name `400`:**

```json
{
  "success": false,
  "message": "Category already exists: Syrups",
  "data": null
}
```

---

### 3.4 Update Category

```
PUT /api/categories/{id}
Authorization: Bearer {token}    ← ROLE_ADMIN required
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "Premium Syrups",
  "description": "High quality flavour syrups"
}
```

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Category updated",
  "data": {
    "id": 3,
    "name": "Premium Syrups",
    "description": "High quality flavour syrups",
    "createdAt": "2026-03-10T09:00:00",
    "updatedAt": "2026-03-10T10:30:00"
  }
}
```

---

### 3.5 Delete Category

```
DELETE /api/categories/{id}
Authorization: Bearer {token}    ← ROLE_ADMIN required
```

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Category deleted",
  "data": null
}
```

---

## 4. Suppliers API

### 4.1 Get All Suppliers

```
GET /api/suppliers
Authorization: Bearer {token}
```

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Suppliers retrieved",
  "data": [
    {
      "id": 1,
      "name": "Bean Masters Sdn Bhd",
      "email": "order@beanmasters.com",
      "phone": "+60123456789",
      "address": "Jalan Bukit Bintang, Kuala Lumpur",
      "isActive": true,
      "createdAt": "2026-03-01T08:00:00",
      "updatedAt": "2026-03-01T08:00:00"
    }
  ]
}
```

---

### 4.2 Get Supplier by ID

```
GET /api/suppliers/{id}
Authorization: Bearer {token}
```

---

### 4.3 Create Supplier

```
POST /api/suppliers
Authorization: Bearer {token}    ← ROLE_ADMIN required
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "Fresh Dairy Co",
  "email": "supply@freshdairy.com",
  "phone": "+60198765432",
  "address": "Shah Alam, Selangor"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `name` | String | ✅ | Not blank |
| `email` | String | ❌ | Valid email if provided, unique |
| `phone` | String | ❌ | |
| `address` | String | ❌ | |

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Supplier created",
  "data": {
    "id": 2,
    "name": "Fresh Dairy Co",
    "email": "supply@freshdairy.com",
    "phone": "+60198765432",
    "address": "Shah Alam, Selangor",
    "isActive": true,
    "createdAt": "2026-03-10T09:00:00",
    "updatedAt": "2026-03-10T09:00:00"
  }
}
```

---

### 4.4 Update Supplier

```
PUT /api/suppliers/{id}
Authorization: Bearer {token}    ← ROLE_ADMIN required
Content-Type: application/json
```

**Request Body:** Same as Create Supplier.

---

### 4.5 Deactivate Supplier (Soft Delete)

```
DELETE /api/suppliers/{id}
Authorization: Bearer {token}    ← ROLE_ADMIN required
```

> ⚠️ This sets `isActive = false`. The supplier record is preserved for historical data integrity.

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Supplier deactivated",
  "data": null
}
```

---

## 5. Products API

### 5.1 Get All Products

```
GET /api/products
Authorization: Bearer {token}
```

Returns only active products (`isActive = true`).

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Products retrieved",
  "data": [
    {
      "id": 1,
      "name": "Arabica Coffee Beans",
      "description": "Premium single origin arabica",
      "sku": "COFFEE-001",
      "unitPrice": 25.00,
      "unit": "kg",
      "currentStock": 90,
      "minimumStock": 10,
      "isLowStock": false,
      "categoryId": 1,
      "categoryName": "Coffee Beans",
      "supplierId": 1,
      "supplierName": "Bean Masters Sdn Bhd",
      "isActive": true,
      "createdAt": "2026-03-01T10:00:00",
      "updatedAt": "2026-03-10T09:00:00"
    }
  ]
}
```

---

### 5.2 Get Product by ID

```
GET /api/products/{id}
Authorization: Bearer {token}
```

---

### 5.3 Get Low Stock Products

```
GET /api/products/low-stock
Authorization: Bearer {token}
```

Returns products where `currentStock <= minimumStock`.

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Low stock products retrieved",
  "data": [
    {
      "id": 3,
      "name": "Oat Milk",
      "sku": "MILK-003",
      "currentStock": 2,
      "minimumStock": 5,
      "isLowStock": true,
      "categoryName": "Dairy",
      "supplierName": "Fresh Dairy Co"
    }
  ]
}
```

---

### 5.4 Search Products by Name

```
GET /api/products/search?name={keyword}
Authorization: Bearer {token}
```

**Query Parameters:**

| Param | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | String | ✅ | Partial name match (case-insensitive) |

**Example:**

```
GET /api/products/search?name=coffee
```

---

### 5.5 Create Product

```
POST /api/products
Authorization: Bearer {token}    ← ROLE_ADMIN required
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "Arabica Coffee Beans",
  "description": "Premium single origin arabica",
  "sku": "COFFEE-001",
  "unitPrice": 25.00,
  "unit": "kg",
  "minimumStock": 10,
  "categoryId": 1,
  "supplierId": 1
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `name` | String | ✅ | Not blank |
| `description` | String | ❌ | |
| `sku` | String | ❌ | Unique if provided |
| `unitPrice` | Decimal | ✅ | > 0 |
| `unit` | String | ✅ | e.g. kg, liters, pieces |
| `minimumStock` | Integer | ✅ | >= 0 |
| `categoryId` | Long | ✅ | Must exist |
| `supplierId` | Long | ❌ | Must exist if provided |

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Product created",
  "data": {
    "id": 1,
    "name": "Arabica Coffee Beans",
    "sku": "COFFEE-001",
    "unitPrice": 25.00,
    "unit": "kg",
    "currentStock": 0,
    "minimumStock": 10,
    "isLowStock": true,
    "categoryId": 1,
    "categoryName": "Coffee Beans",
    "supplierId": 1,
    "supplierName": "Bean Masters Sdn Bhd",
    "isActive": true
  }
}
```

> 💡 Note: `currentStock` starts at `0`. Use the Stock Transaction API to add stock.

---

### 5.6 Update Product

```
PUT /api/products/{id}
Authorization: Bearer {token}    ← ROLE_ADMIN required
Content-Type: application/json
```

**Request Body:** Same as Create Product.

---

### 5.7 Deactivate Product (Soft Delete)

```
DELETE /api/products/{id}
Authorization: Bearer {token}    ← ROLE_ADMIN required
```

Sets `isActive = false`. Product still appears in transaction history.

---

## 6. Stock Transactions API

### 6.1 Process Stock Transaction

The core of the system. Adjusts product stock and records full audit trail.

```
POST /api/stock/transaction
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**

```json
{
  "productId": 1,
  "type": "RESTOCK",
  "quantity": 50,
  "notes": "Monthly restock from Bean Masters"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `productId` | Long | ✅ | Must be an active product |
| `type` | String | ✅ | `RESTOCK`, `USAGE`, `WASTE`, `ADJUSTMENT` |
| `quantity` | Integer | ✅ | Must be > 0 |
| `notes` | String | ❌ | Optional remarks |

**Transaction Types:**

| Type | Stock Effect | When to Use |
|------|-------------|-------------|
| `RESTOCK` | `current + quantity` | New stock arrives from supplier |
| `USAGE` | `current - quantity` | Stock used in cafe |
| `WASTE` | `current - quantity` | Stock spoiled or damaged |
| `ADJUSTMENT` | `= quantity` | Manual correction (sets exact value) |

**Success Response `200 OK` — RESTOCK example:**

```json
{
  "success": true,
  "message": "Transaction processed",
  "data": {
    "id": 1,
    "productId": 1,
    "productName": "Arabica Coffee Beans",
    "performedBy": "Hafiz Ahmad",
    "type": "RESTOCK",
    "quantity": 50,
    "stockBefore": 0,
    "stockAfter": 50,
    "notes": "Monthly restock from Bean Masters",
    "createdAt": "2026-03-10T09:30:00"
  }
}
```

**Error — Insufficient Stock `400`:**

```json
{
  "success": false,
  "message": "Insufficient stock. Available: 10, Requested: 50",
  "data": null
}
```

> ⚠️ `USAGE` and `WASTE` will fail if `quantity > currentStock`.

---

### 6.2 Get All Transactions

```
GET /api/stock/transactions
Authorization: Bearer {token}
```

**Success Response `200 OK`:**

```json
{
  "success": true,
  "message": "Transactions retrieved",
  "data": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Arabica Coffee Beans",
      "performedBy": "Hafiz Ahmad",
      "type": "RESTOCK",
      "quantity": 50,
      "stockBefore": 0,
      "stockAfter": 50,
      "notes": "Monthly restock",
      "createdAt": "2026-03-10T09:30:00"
    },
    {
      "id": 2,
      "productId": 1,
      "productName": "Arabica Coffee Beans",
      "performedBy": "Hafiz Ahmad",
      "type": "USAGE",
      "quantity": 5,
      "stockBefore": 50,
      "stockAfter": 45,
      "notes": null,
      "createdAt": "2026-03-10T10:00:00"
    }
  ]
}
```

---

### 6.3 Get Transactions by Product

```
GET /api/stock/transactions/product/{productId}
Authorization: Bearer {token}
```

**Path Parameter:**

| Param | Type | Description |
|-------|------|-------------|
| `productId` | Long | Filter transactions for this product |

---

### 6.4 Get Transactions by Type

```
GET /api/stock/transactions/type/{type}
Authorization: Bearer {token}
```

**Path Parameter:**

| Param | Type | Values |
|-------|------|--------|
| `type` | String | `RESTOCK`, `USAGE`, `WASTE`, `ADJUSTMENT` |

**Example:**

```
GET /api/stock/transactions/type/RESTOCK
```

---

## 7. Error Codes

| HTTP Status | Meaning | Common Causes |
|-------------|---------|---------------|
| `200` | OK | Request successful |
| `400` | Bad Request | Validation failure, duplicate data, insufficient stock |
| `401` | Unauthorized | Missing or invalid JWT token |
| `403` | Forbidden | Valid token but insufficient role (e.g., STAFF accessing ADMIN endpoint) |
| `404` | Not Found | Resource with given ID doesn't exist |
| `500` | Internal Server Error | Unexpected server error |

**Validation Error Response `400`:**

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "must be a well-formed email address",
    "password": "size must be between 6 and 2147483647"
  }
}
```

---

## 8. Data Models

### AuthResponse

```json
{
  "token": "string (JWT)",
  "name": "string",
  "email": "string",
  "role": "ROLE_ADMIN | ROLE_STAFF"
}
```

### CategoryResponse

```json
{
  "id": "long",
  "name": "string",
  "description": "string | null",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### SupplierResponse

```json
{
  "id": "long",
  "name": "string",
  "email": "string | null",
  "phone": "string | null",
  "address": "string | null",
  "isActive": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### ProductResponse

```json
{
  "id": "long",
  "name": "string",
  "description": "string | null",
  "sku": "string | null",
  "unitPrice": "decimal",
  "unit": "string",
  "currentStock": "integer",
  "minimumStock": "integer",
  "isLowStock": "boolean (currentStock <= minimumStock)",
  "categoryId": "long",
  "categoryName": "string",
  "supplierId": "long | null",
  "supplierName": "string | null",
  "isActive": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### StockTransactionResponse

```json
{
  "id": "long",
  "productId": "long",
  "productName": "string",
  "performedBy": "string (user name)",
  "type": "RESTOCK | USAGE | WASTE | ADJUSTMENT",
  "quantity": "integer",
  "stockBefore": "integer",
  "stockAfter": "integer",
  "totalCost": "decimal | null",
  "notes": "string | null",
  "createdAt": "datetime"
}
```

---

## 🧪 Postman Quick Start

### Collection Setup

1. Set base URL variable: `{{base_url}} = http://localhost:8089`
2. Set token variable: `{{token}} = ` (fill after login)

### Authorization Header

```
Key:   Authorization
Value: Bearer {{token}}
```

### Recommended Test Flow

```
Step 1:  POST   {{base_url}}/api/auth/register
Step 2:  POST   {{base_url}}/api/auth/login          → copy token
Step 3:  [SQL]  UPDATE users SET role = 'ROLE_ADMIN'  → via Supabase
Step 4:  POST   {{base_url}}/api/auth/login           → re-login for ADMIN token
Step 5:  POST   {{base_url}}/api/categories           → save categoryId
Step 6:  POST   {{base_url}}/api/suppliers            → save supplierId
Step 7:  POST   {{base_url}}/api/products             → save productId
Step 8:  POST   {{base_url}}/api/stock/transaction    → RESTOCK (quantity: 100)
Step 9:  POST   {{base_url}}/api/stock/transaction    → USAGE (quantity: 10)
Step 10: GET    {{base_url}}/api/products/low-stock   → check alerts
```

---

*Last updated: March 2026 | Spring Boot 4.0.3 | Java 17*
