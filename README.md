# 🛒 Spring Checkout System

A Spring Boot checkout system with REST API and interactive CLI. Demonstrates pricing rules, quantity discounts, and containerized deployment for a take-home assessment.

## 🚀 Quick Start

**Quick Demo:**
```bash
cd checkout-kata
./run.sh
```

**Web Service Mode** (REST API at `localhost:8080`):
```bash
cd checkout-kata
docker-compose -f docker-compose-web.yml up -d --build
```

## 📡 API Endpoints

### Checkout API (`/api/checkout`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/scan` | Add items to cart |
| `POST` | `/remove` | Remove items from cart |
| `GET` | `/total` | Get checkout summary |
| `GET` | `/items` | Get current cart items |
| `POST` | `/clear` | Clear cart |

### Admin API (`/admin/pricing`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Get all pricing |
| `PATCH` | `/{item}/price` | Update item price |
| `PATCH` | `/{item}/offer` | Update discount offer |
| `DELETE` | `/{item}/offer` | Remove discount offer |

## 🧪 Running Tests

```bash
# Unit tests
cd checkout-kata && ./mvnw test

# Integration tests (requires web service running)
cd checkout-kata && docker-compose -f docker-compose-web.yml up -d
cd ../integration-tests && mvn test
cd ../checkout-kata && docker-compose -f docker-compose-web.yml down
```