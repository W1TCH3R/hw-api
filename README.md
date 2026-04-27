# hw-api

This project contains API automation tests for the Swagger Petstore API.

The tests are written as code using:

- Java
- Gradle
- JUnit 5
- REST Assured

---

## Tested API

Base URL:

```text
https://petstore.swagger.io/v2
```

Main endpoints used in the automated tests:

```text
POST   /pet
GET    /pet/{petId}
DELETE /pet/{petId}

POST   /store/order
GET    /store/order/{orderId}
DELETE /store/order/{orderId}
```

For DELETE requests, the required API key is used:

```text
api_key: special-key
```

---

## Test Scope

The automation covers the following homework scenario:

1. Create 4 pets with status `available`
2. Place multiple orders for each created pet
3. Delete all orders and pets created during the test
4. Verify that deleted orders and pets cannot be retrieved and return HTTP `404`
5. Implement proper cleanup and error handling for DELETE endpoints

---

## Project Structure

```text
.
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src
│   └── test
│       └── java
│           └── api
│               ├── client
│               │   ├── PetApiClient.java
│               │   └── StoreApiClient.java
│               ├── helper
│               │   └── TestResourceHelper.java
│               ├── model
│               │   ├── Category.java
│               │   ├── Order.java
│               │   ├── Pet.java
│               │   └── Tag.java
│               ├── testdata
│               │   └── TestDataFactory.java
│               ├── tests
│               │   ├── BaseApiTest.java
│               │   ├── PetApiTest.java
│               │   ├── PetStoreE2ETest.java
│               │   └── StoreOrderApiTest.java
│               └── util
│                   ├── DeleteHelper.java
│                   └── IdGenerator.java
├── .gitignore
├── build.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

---

## Class Responsibilities

| Class | Responsibility |
|---|---|
| `PetApiClient` | Handles API calls for `/pet` endpoints |
| `StoreApiClient` | Handles API calls for `/store/order` endpoints |
| `Category`, `Pet`, `Tag`, `Order` | Request and response model classes |
| `TestDataFactory` | Creates reusable test data objects |
| `IdGenerator` | Generates unique IDs for pets and orders |
| `DeleteHelper` | Handles DELETE retry logic and safe cleanup |
| `TestResourceHelper` | Creates prerequisite test resources such as pets and orders |
| `BaseApiTest` | Contains common test setup and response logging |
| `PetApiTest` | Contains separate tests for pet endpoints |
| `StoreOrderApiTest` | Contains separate tests for store order endpoints |
| `PetStoreE2ETest` | Contains the full end-to-end homework scenario |

---

## Prerequisites

Before running the project, make sure the following is installed:

### Java

Check Java version:

```bash
java -version
```

### Gradle

You do not need to install Gradle manually because the project includes the Gradle Wrapper.

Use this command on macOS/Linux:

```bash
./gradlew
```

Use this command on Windows:

```bash
gradlew.bat
```

---

## Setup Instructions

Clone the repository:

```bash
git clone https://github.com/W1TCH3R/hw-api
```

Open the project folder:

```bash
cd hw-api
```

Make sure the Gradle wrapper is executable on macOS/Linux:

```bash
chmod +x gradlew
```

Run a clean build with tests:

```bash
./gradlew clean test
```
---

## Running Tests

### Run API Endpoint Tests

The default Gradle test task runs endpoint-level API tests and excludes the E2E test.

```bash
./gradlew clean test
```

This runs:

```text
PetApiTest
StoreOrderApiTest
```

These tests validate the individual API endpoints used in the scenario.

---

### Run "E2E" Scenario Test

**The full scenario that is required by the task** is tagged separately as `e2e`.

Run it with:

```bash
./gradlew e2eTest
```

This runs:

```text
PetStoreE2ETest
```
---

## Test Tags

JUnit 5 tags are used to separate test types.

| Tag | Purpose |
|---|---|
| `api` | Endpoint-level API tests |
| `e2e` | Full end-to-end scenario test |

The default `test` task excludes the `e2e` tag, so the E2E test does not run together with regular endpoint tests.

---

## Logging

REST Assured request and response logging is enabled in the API client classes.

The logs include:

- request method
- request URL
- request headers
- request body
- response status code
- response headers
- response body

Example log output:

```text
Request method: POST
Request URI: https://petstore.swagger.io/v2/pet
Headers: Accept=application/json
Content-Type=application/json

Body:
{
    "id": 123456,
    "category": {
        "id": 1,
        "name": "dogs"
    },
    "name": "doggie-1",
    "photoUrls": [
        "https://example.com/photo-1.jpg"
    ],
    "tags": [
        {
            "id": 1,
            "name": "automation-test"
        }
    ],
    "status": "available"
}

HTTP/1.1 200 OK

{
    "id": 123456,
    "category": {
        "id": 1,
        "name": "dogs"
    },
    "name": "doggie-1",
    "photoUrls": [
        "https://example.com/photo-1.jpg"
    ],
    "tags": [
        {
            "id": 1,
            "name": "automation-test"
        }
    ],
    "status": "available"
}
```

To make logs visible in Gradle output, `showStandardStreams = true` is enabled in `build.gradle`.

---

## DELETE Handling

The task mentions that DELETE endpoints often fail, so DELETE handling is implemented in `DeleteHelper`.

The helper supports:

- retrying DELETE requests
- accepting successful delete status codes
- safe cleanup in `finally` blocks
- not failing cleanup when a resource is already deleted

The following status codes are treated as acceptable for DELETE cleanup:

```text
200
202
204
404
```

`404` is accepted during cleanup because it means the resource is already deleted or no longer exists.

---
## Automated Test Cases

### Pet API Tests

| Test class | Test case | Endpoint |
|---|---|---|
| `PetApiTest` | Create pet with status `available` | `POST /pet` |
| `PetApiTest` | Get created pet by ID | `GET /pet/{petId}` |
| `PetApiTest` | Delete created pet and verify it cannot be retrieved | `DELETE /pet/{petId}` + `GET /pet/{petId}` |

### Store Order API Tests

| Test class | Test case | Endpoint |
|---|---|---|
| `StoreOrderApiTest` | Create order for existing pet | `POST /store/order` |
| `StoreOrderApiTest` | Get created order by ID | `GET /store/order/{orderId}` |
| `StoreOrderApiTest` | Delete created order and verify it cannot be retrieved | `DELETE /store/order/{orderId}` + `GET /store/order/{orderId}` |

### E2E Test

| Test class | Test case |
|---|---|
| `PetStoreE2ETest` | Create 4 available pets, create multiple orders for each pet, delete all orders and pets, verify deleted resources return `404` |

---

## E2E Scenario Details

The E2E test performs the following flow:

```text
1. Create 4 pets with status available
2. Verify created pets can be retrieved
3. Create 2 orders for each pet
4. Verify created orders can be retrieved
5. Delete all created orders
6. Verify deleted orders return 404
7. Delete all created pets
8. Verify deleted pets return 404
9. Run cleanup in finally block (in case test fails midway)
```

The number of orders per pet is currently:

```text
2 orders per pet
```

Total orders created in the E2E test:

```text
4 pets × 2 orders = 8 orders
```

---

## Test Data

Unique IDs are generated for each test run using `IdGenerator`.

This helps avoid conflicts with existing data in the public Swagger Petstore environment.

Example pet:

```json
{
  "id": 123456,
  "category": {
    "id": 1,
    "name": "dogs"
  },
  "name": "doggie-1",
  "photoUrls": [
    "https://example.com/photo-1.jpg"
  ],
  "tags": [
    {
      "id": 1,
      "name": "automation-test"
    }
  ],
  "status": "available"
}
```

Example order:

```json
{
  "id": 987654,
  "petId": 123456,
  "quantity": 1,
  "shipDate": "2026-04-27T10:00:00.000Z",
  "status": "placed",
  "complete": true
}
```

---

## Possible additional Test Cases (as required by the task, not automated)

### POST /pet

| Test case | Description | Expected result |
|---|---|---|
| Create pet with invalid status | Send request with status value outside allowed values, for example `unknown123` | API should reject the request or behavior should be documented if the API accepts it |
| Create pet with missing required fields | Send request without fields such as `name`, or `status` | API should return validation error |
| Create pet with duplicate ID | Create a pet using an already existing pet ID | API should either update the existing pet or return conflict, depending on API behavior |
| Create pet with very long name | Send a pet name with an unusually long string | API should handle it correctly or return validation error |

### GET /pet/{petId}

| Test case | Description | Expected result |
|---|---|---|
| Get pet with invalid ID format | Request pet using non-numeric ID, for example `/pet/abc` | API should return `400` or `404` |
| Get pet with negative ID | Request pet using negative ID | API should return validation error or `404` |

### POST /store/order

| Test case | Description | Expected result |
|---|---|---|
| Create order for non-existing pet | Place an order using a `petId` that does not exist | API should reject the request|
| Create order with quantity `0` | Send order request with quantity equal to zero | API should return validation error |
| Create order with negative quantity | Send order request with negative quantity | API should return validation error |
| Create order with invalid status | Send status outside allowed values, for example `unknown123` | API should reject the request or behavior should be documented |
| Create order without `petId` | Send order request without pet ID | API should return validation error |
| Create order with invalid `shipDate` | Send incorrectly formatted date | API should return validation error |

### GET /store/order/{orderId}

| Test case | Description | Expected result |
|---|---|---|
| Get order with invalid ID format | Request order using non-numeric ID, for example `/store/order/abc` | API should return `400` or `404` |
| Get order with negative ID | Request order using negative ID | API should return validation error or `404` |

### DELETE /pet/{petId}

| Test case | Description | Expected result |
|---|---|---|
| Delete pet without API key | Send DELETE request without `api_key` header | API should return `401` or `403` |
| Delete pet with invalid API key | Send DELETE request with incorrect API key | API should return `401` or `403` |
| Delete non-existing pet | Delete pet ID that does not exist | API should return `404` |
| Delete already deleted pet | Delete the same pet twice | API should return `404` |

### DELETE /store/order/{orderId}

| Test case | Description | Expected result |
|---|---|---|
| Delete order without API key | Send DELETE request without `api_key` header | API should return `401` or `403` |
| Delete order with invalid API key | Send DELETE request with incorrect API key | API should return `401` or `403` |
| Delete non-existing order | Delete order ID that does not exist | API should return `404` |
| Delete already deleted order | Delete the same order twice | API should return `404` |

### Scenario-level test cases

| Test case | Description | Expected result |
|---|---|---|
| Create four pets and place one order for each | Validate minimum order flow | All pets and orders are created successfully |
| Create four pets and place multiple orders for each | Validate multiple orders per pet | All orders are created and linked to correct pet IDs |
| Delete pets before deleting orders | Check whether orders remain available after pet deletion | Behavior should match API contract |
| Cleanup after partial failure | Simulate failure in the middle of scenario | Created resources should still be cleaned up |
| Retry failed DELETE requests | Simulate temporary DELETE failure | Delete should be retried and handled safely |
| Validate response time | Measure API response time for each endpoint | Response time should be within acceptable threshold |