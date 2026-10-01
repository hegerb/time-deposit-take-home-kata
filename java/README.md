# Time Deposit API (Java)

Spring Boot service for XA Bank time deposits: two endpoints, PostgreSQL, hexagonal layout.

## Run

Requirements: Java 17 or newer, Maven, Docker (the app starts PostgreSQL through `docker-compose.yml`).

```
cd java
mvn spring-boot:run
```

From an IDE, run `TimeDepositApplication` with the working directory set to `java`, where `docker-compose.yml` lives.

Swagger UI: http://localhost:8080/swagger-ui.html (serves the contract `src/main/resources/static/openapi.yaml`).

## Trigger the endpoints in Swagger UI

1. Open Swagger UI, expand **TimeDeposits**.
2. `GET /time-deposits`, **Try it out**, **Execute**: every deposit with `id`, `planType`, `balance`, `days`, `withdrawals`. The seed data has two deposits per plan, one on each side of the plan's interest boundary.
3. `POST /time-deposits/update-balances`, **Try it out**, **Execute**: credits one month of interest to every deposit and returns the updated list. Each call credits another month.
4. Repeat step 2 to see the persisted balances.

Same calls with curl:

```
curl http://localhost:8080/time-deposits
curl -X POST http://localhost:8080/time-deposits/update-balances
```

## Tests

```
mvn test
```

Needs Docker: the persistence and end-to-end tests run against PostgreSQL in Testcontainers, one shared container per run.

## Layout

| Package | Content |
|---|---|
| `org.ikigaidigital` | `TimeDeposit` and `TimeDepositCalculator` as given; the calculator delegates to interest policies |
| `domain.interest` | `InterestPolicy` and one policy per plan type, `InterestPolicies` registry, `AnnualRate` |
| `domain.model` | `PlanTypes`, `Withdrawal`, `TimeDepositWithWithdrawals` |
| `application.port.in` | `GetAllTimeDepositsUseCase`, `UpdateAllBalancesUseCase` |
| `application.port.out` | `TimeDepositRepository` |
| `application.service` | `TimeDepositService` |
| `adapter.in.rest` | controller implementing the interface generated from `openapi.yaml` |
| `adapter.out.persistence` | JPA entities and the repository port implementation |
| `config` | bean wiring |

`domain` and `application` import nothing from Spring, JPA or generated code; `HexagonalArchitectureTest` fails the build if that changes.

## Assumptions

Each one is also stated as a comment where the decision is made in code.

| Id | Assumption |
|---|---|
| A1 | Balance update is `POST /time-deposits/update-balances`: each call accrues a month, so it is not idempotent |
| A2 | Table and column names are the README's, quoted in PostgreSQL |
| A3 | `withdrawals` is a nested list of `{id, amount, date}` |
| A4 | Updating balances does not change `days` |
| A5 | Money is `NUMERIC(19,2)`; `TimeDeposit.balance` stays `Double`, converted at the persistence and API boundaries |
| A6 | Withdrawals do not affect the interest calculation, as in the original code |
| A7 | The update endpoint returns the updated list |
| A8 | Seed data is provided by a Flyway migration |
| A9 | The placeholder assertion in the original test was replaced with the real expected balance |
| A10 | Root README keeps the assignment; this file holds the solution docs |
| A11 | The parameter `xs` was renamed; parameter names are not part of a signature |
| A12 | The 30-day grace period stays in the calculator, since the README states it for all plans |

More: `docs/DESIGN.md` (architecture, commit plan), `docs/REQUIREMENTS-CHECKLIST.md` (README traced line by line), `docs/AI-ASSISTED-DEVELOPMENT.md`.
