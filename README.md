Monk Application
A Spring Boot–based service to manage and apply coupons.
Supports Cart-wise, Product-wise, and Buy X Get Y (BxGy) coupon types.

Features
Create, update, delete coupons
Fetch all coupons or by ID
Soft & hard delete support (currently hard delete)
Find applicable coupons for a cart

Apply coupons to a cart and calculate discounts
Tech Stack
Java 17+
Spring Boot
Spring Data JPA
H2 / MySQL (configurable)
Jackson

Setup Instructions

Clone the repo:

git clone https://github.com/your-repo/coupon-service.git
cd coupon-service


Run the application:

mvn spring-boot:run

```bash
App will start at: http://localhost:8080

📌 API Endpoints
1. Create Coupon

POST /coupons

Cart-wise coupon example
```bash
curl -X POST http://localhost:8080/coupons \
  -H "Content-Type: application/json" \
  -d '{
    "type": "cart-wise",
    "expiryDate": "2025-12-31",
    "details": {
      "threshold": 100,
      "discount": 10
    }
  }'


Product-wise coupon example
```bash
curl -X POST http://localhost:8080/coupons \
  -H "Content-Type: application/json" \
  -d '{
    "type": "product-wise",
    "expiryDate": "2025-12-31",
    "details": {
      "product_id": 1,
      "discount": 20
    }
  }'


BxGy coupon example
```bash
curl -X POST http://localhost:8080/coupons \
  -H "Content-Type: application/json" \
  -d '{
    "type": "bxgy",
    "expiryDate": "2025-12-31",
    "details": {
      "buy_products": [
        { "product_id": 1, "quantity": 2 }
      ],
      "get_products": [
        { "product_id": 2, "quantity": 1 }
      ],
      "repition_limit": 2
    }
  }'

2. Get All Coupons

GET /coupons
```bash
curl -X GET http://localhost:8080/coupons

3. Get Coupon by ID

GET /coupons/{id}
```bash
curl -X GET http://localhost:8080/coupons/1

4. Update Coupon

PUT /coupons/{id}
```bash
curl -X PUT http://localhost:8080/coupons/1 \
  -H "Content-Type: application/json" \
  -d '{
    "type": "cart-wise",
    "expiryDate": "2026-01-31",
    "details": {
      "threshold": 150,
      "discount": 15
    }
  }'

5. Delete Coupon

DELETE /coupons/{id}

curl -X DELETE http://localhost:8080/coupons/1

6. Get Applicable Coupons

POST /coupons/applicable-coupons

Request:
```bash
curl -X POST http://localhost:8080/coupons/applicable-coupons \
  -H "Content-Type: application/json" \
  -d '{
    "cart": {
      "items": [
        { "productId": 1, "quantity": 2, "price": 50 },
        { "productId": 2, "quantity": 1, "price": 100 }
      ]
    },
    "totalPrice": 200,
    "finalPrice": 200,
    "totalDiscount": 0
  }'


Response:
```bash
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Applicable coupons fetched successfully",
  "method": "POST",
  "data": [
    { "couponId": 1, "type": "CART_WISE", "discount": 20.0 }
  ]
}

7. Apply Coupon

POST /coupons/apply-coupon/{id}

Request:
```bash
curl -X POST http://localhost:8080/coupons/apply-coupon/1 \
  -H "Content-Type: application/json" \
  -d '{
    "cart": {
      "items": [
        { "productId": 1, "quantity": 2, "price": 50, "totalDiscount": 0 },
        { "productId": 2, "quantity": 1, "price": 100, "totalDiscount": 0 }
      ]
    },
    "totalPrice": 200,
    "finalPrice": 200,
    "totalDiscount": 0
  }'


Response (after applying coupon):

```bash
{
  "cart": {
    "items": [
      { "productId": 1, "quantity": 2, "price": 50, "totalDiscount": 20 },
      { "productId": 2, "quantity": 1, "price": 100, "totalDiscount": 0 }
    ]
  },
  "totalPrice": 200,
  "finalPrice": 180,
  "totalDiscount": 20
}

📂 Project Structure
src/main/java/org/example/
```bash
 ├── controller/   # REST controllers
 ├── dto/          # DTO classes
 ├── model/        # Entity + Enum
 ├── repository/   # JPA repositories
 ├── service/      # Business logic
 └── utils/        # Constants & messages
