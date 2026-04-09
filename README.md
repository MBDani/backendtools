# Product Sorting API

A backend REST API that sorts a product catalog by weighted scoring criteria, built as a technical assessment using **Java 21**, **Spring Boot 3.4**, and **Hexagonal Architecture**.

---

## Problem Statement

Given a list of products in a t-shirt category, implement an algorithm that ranks them based on configurable weighted criteria. The final score for each product is the **weighted sum** of all active criteria scores.

Two criteria are implemented out of the box:

- **`sales_units`** — based on the number of units sold
- **`stock_ratio`** — based on the proportion of sizes currently in stock

New criteria can be added without modifying any existing code.

---

## Architecture

The project follows **Hexagonal Architecture (Ports & Adapters)**, split into four Maven modules:

```
backendtools/               ← Parent POM
├── domain/                 ← Business logic. No framework dependencies.
├── application/            ← Use case orchestration.
├── infrastructure/         ← Spring adapters (REST, persistence, config).
└── main/                   ← Spring Boot entry point and integration tests.
```

---

## Tech Stack

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.4 |
| Build | Maven (multi-module) |
| Mapping | MapStruct 1.5.5 |
| Boilerplate reduction | Lombok |
| Validation | Jakarta Validation |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Mockito + MockMvc |

---

## Scoring Algorithm

### Normalization

Each strategy returns a value in **[0, 100]** so that weights are truly comparable regardless of the metric's scale.

#### Sales Units Score

Normalized against the **maximum sales units in the current dataset** (computed at runtime, not hardcoded):

```
salesScore(p) = (p.salesUnits / max(salesUnits)) × 100
```

#### Stock Ratio Score

Percentage of sizes that currently have stock:

```
stockScore(p) = (sizesWithStock / totalSizes) × 100
```

#### Final Weighted Score

```
score(p) = Σ [ strategyScore(p) × weight ]
```

### Example — Dataset from the problem statement

With weights `sales_units = 0.4`, `stock_ratio = 0.6`:

| # | Product | Sales | Sales score | Sizes in stock | Stock score | **Final score** |
|---|---|---|---|---|---|---|
| 5 | CONTRASTING LACE T-SHIRT | 650 | 100.00 | 1 / 3 | 33.33 | 40.00 + 20.00 = **60.00** |
| 1 | V-NECK BASIC SHIRT | 100 | 15.38 | 2 / 3 | 66.67 | 6.15 + 40.00 = **46.15** |
| 2 | CONTRASTING FABRIC T-SHIRT | 50 | 7.69 | 3 / 3 | 100.00 | 3.08 + 60.00 = **63.08** |
| 3 | RAISED PRINT T-SHIRT | 80 | 12.31 | 3 / 3 | 100.00 | 4.92 + 60.00 = **64.92** |
| 4 | PLEATED T-SHIRT | 3 | 0.46 | 3 / 3 | 100.00 | 0.18 + 60.00 = **60.18** |
| 6 | SLOGAN T-SHIRT | 20 | 3.08 | 3 / 3 | 100.00 | 1.23 + 60.00 = **61.23** |

**Result order** (descending score):

```
1. RAISED PRINT T-SHIRT          (64.92)
2. CONTRASTING FABRIC T-SHIRT    (63.08)
3. SLOGAN T-SHIRT                (61.23)
4. PLEATED T-SHIRT               (60.18)
5. CONTRASTING LACE T-SHIRT      (60.00)
6. V-NECK BASIC SHIRT            (46.15)
```

> Note: the product with highest sales (CONTRASTING LACE, 650 units) ends up in position 5 because it only has stock in 1 out of 3 sizes. This is expected and demonstrates that the weighting system works correctly.

---

## Running the Application

```bash
./mvnw spring-boot:run -pl main --also-make
```

The API will be available at `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## API Reference

### `POST /api/v1/products/sort`

Returns the full product catalog sorted by the provided weighted criteria.

**Request body:**

```json
{
  "criteria": [
    { "name": "sales_units", "weight": 0.4 },
    { "name": "stock_ratio", "weight": 0.6 }
  ]
}
```

| Field | Type | Constraints |
|---|---|---|
| `criteria` | array | Not empty |
| `criteria[].name` | string | Not blank. Must match a registered strategy. |
| `criteria[].weight` | number | Must be positive |

**Response `200 OK`:**

```json
[
  {
    "id": 3,
    "name": "RAISED PRINT T-SHIRT",
    "salesUnits": 80,
    "stock": [
      { "size": "S", "quantity": 20 },
      { "size": "M", "quantity": 2  },
      { "size": "L", "quantity": 20 }
    ]
  },
  {
    "id": 5,
    "name": "CONTRASTING LACE T-SHIRT",
    "salesUnits": 650,
    "stock": [
      { "size": "S", "quantity": 0 },
      { "size": "M", "quantity": 1 },
      { "size": "L", "quantity": 0 }
    ]
  }
]
```

> The `stock` field makes the ranking self-explanatory: CONTRASTING LACE T-SHIRT has 650 sales but ends up in position 5 because only 1 of its 3 sizes has stock.

**Error responses:**

| Status | Cause |
|---|---|
| `400 Bad Request` | Validation failure (empty criteria, negative weight, etc.) |
| `422 Unprocessable Entity` | Unknown criteria name |

**cURL example:**

```bash
curl -X POST http://localhost:8080/api/v1/products/sort \
  -H "Content-Type: application/json" \
  -d '{
    "criteria": [
      { "name": "sales_units", "weight": 0.4 },
      { "name": "stock_ratio", "weight": 0.6 }
    ]
  }'
```