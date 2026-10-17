# SDEV200 Budgeting App

A Java 17 / Spring Boot application for personal monthly budgets using the 50/30/20 rule. It provides a JSP web interface and a JSON REST API.

## Run

```powershell
mvn spring-boot:run
```

The app stores users, budgets, and expenses in a file-backed H2 database at `data/budgeting`. The database directory is created automatically and survives application restarts. The project is packaged as a WAR because Spring Boot's embedded Tomcat JSP support requires WAR packaging.

Open `http://localhost:8080/` after startup. Create a profile, create a monthly budget, then open it to add expenses. Budget totals refresh when a new expense is submitted. The UI and API share the same services and database.

Run the tests with:

```powershell
mvn test
```

## API

Create a user:

```http
POST /api/users
Content-Type: application/json

{"name":"Alex Example","email":"alex@example.com"}
```

Create a monthly budget for the returned user ID:

```http
POST /api/users/1/budgets
Content-Type: application/json

{"month":"2026-10","monthlyIncome":4000.00}
```

Budget responses contain a per-category allocation, spending total, and remaining amount. The app allocates 50% to `NEEDS`, 30% to `WANTS`, and 20% to `SAVINGS`, rounding to cents while keeping the three allocations equal to the full income.

Add an expense to a category:

```http
POST /api/users/1/budgets/1/expenses
Content-Type: application/json

{"description":"Groceries","amount":125.50,"category":"NEEDS"}
```

Available endpoints:

- `POST /api/users`, `GET /api/users/{userId}`
- `POST /api/users/{userId}/budgets`, `GET /api/users/{userId}/budgets`
- `GET /api/users/{userId}/budgets/{budgetId}`
- `POST /api/users/{userId}/budgets/{budgetId}/expenses`
- `GET /api/users/{userId}/budgets/{budgetId}/expenses`

Each user can have one budget per month. Budgets and expenses are scoped to the user in the URL; nonexistent resources return `404`, and duplicate email/month combinations return `409`.

The project does not implement login/authentication or associate browser sessions with profiles. Profile IDs in page URLs are identifiers, not access controls. Use only in a trusted local development environment; add authentication and authorization before exposing personal financial data.

## Project design

- `AppUser`, `Budget`, and `Expense` are persisted JPA entities.
- `BudgetCategory` is the expense-category enum.
- `AllocationCalculator` is the allocation interface, implemented by `FiftyThirtyTwentyCalculator`.
- `UserService` and `BudgetService` contain the application processing logic; REST controllers expose it.
- `BudgetPageController` supplies server-rendered JSP pages; its forms call the same services as the API.

## Documentation

See [the codebase walkthrough](docs/CODEBASE-WALKTHROUGH.md) for the request flow, package responsibilities, data model, and UI/API behavior.
