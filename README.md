# 🛍️ Product API (Quarkus + Hibernate Reactive)

This API allows you to manage products including creating, retrieving, updating, deleting, and checking product availability. It is built using Quarkus, Hibernate Reactive, and Mutiny for asynchronous and reactive operations.

---

## 📦 Base Path
```
/products
```

---

## 📘 Endpoints

### ✅ Get All Products
**GET** `/products?asc=true|false`

- **Description:** Retrieves all products from the database. If `asc=true` is passed as a query parameter, the products are sorted by price in ascending order.
- **Query Parameters:**
  - `asc` (optional, boolean): If `true`, sort products by ascending price.
- **Response:**
  - `200 OK` with JSON array of products.

---

### ➕ Create Product
**POST** `/products`

- **Description:** Creates a new product in the database.
- **Request Body:**
```json
{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 1500.0,
  "quantity": 10
}
```
- **Response:**
  - `201 Created` with location header `/products/{id}`

---

### 🔍 Get Product or Check Availability
**GET** `/products/{id}`

- **Description:** 
  - If `count` query param is **not provided**, returns the product details.
  - If `count` **is provided**, returns availability information.

- **Path Parameters:**
  - `id` (required): Product ID

- **Query Parameters (optional):**
  - `count` (integer): Quantity to check for availability

- **Response Examples:**
  - `200 OK` with product details OR:
  - `200 OK` with:
```json
{
  "available": true,
  "unavailableCount": 0
}
```
- **404/204 NO_CONTENT** if product not found.

---

### ✏️ Update Product
**PUT** `/products/{id}`

- **Description:** Updates an existing product by its ID.
- **Path Parameters:**
  - `id` (required): Product ID
- **Request Body:** Same as create
- **Response:**
  - `200 OK` with updated product JSON
  - `204 NO_CONTENT` if product not found

---

### ❌ Delete Product
**DELETE** `/products/{id}`

- **Description:** Deletes a product by its ID.
- **Path Parameters:**
  - `id` (required): Product ID
- **Response:**
  - `204 NO_CONTENT` if deleted successfully
  - `200 OK` if not found

---

## 🪵 Logging

- Log messages are stored in: `D:/logs/`
- The application creates the directory if it does not exist.

---

## 🛠 Tech Stack

- **Quarkus**
- **Hibernate Reactive + Panache**
- **Mutiny Uni**
- **RESTEasy Reactive**
- **Lombok** (optional)
- **SLF4J / JBoss Logging**

---

# quarkus-test-api

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quarkus-test-api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Mutiny ([guide](https://quarkus.io/guides/mutiny-primer)): Write reactive applications with the modern Reactive Programming library Mutiny
- REST resources for Hibernate Reactive with Panache ([guide](https://quarkus.io/guides/rest-data-panache)): Generate Jakarta REST resources for your Hibernate Reactive Panache entities and repositories
- Reactive PostgreSQL client ([guide](https://quarkus.io/guides/reactive-sql-clients)): Connect to the PostgreSQL database using the reactive pattern

---

## 🧪 Improvements

- I will add unit or integration tests using `@QuarkusTest` later on as right now getting some errors
- There is always room for improvement

