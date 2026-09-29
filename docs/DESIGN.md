# Design: Time Deposit Refactoring Kata (Java)

Status: accepted. Decisions below are final unless a later commit records a change here.
Checklist references (R.., A..) point to REQUIREMENTS-CHECKLIST.md.

## 1. Goal in one paragraph

Complete the junior developer's time deposit system (R03) by adding two REST endpoints (R05, R06)
and a database (R08..R11), while refactoring the interest calculation to be extensible (R18)
and keeping `TimeDeposit` and `updateBalance` unbroken (R17, R25, R26). Structure follows
hexagonal architecture (R35), the API is contract-first OpenAPI (R34), the DB is tested with
Testcontainers (R37), and the whole thing is built with a documented AI harness (R20..R24).

## 2. Stack

| Concern | Choice | Why |
|---|---|---|
| Runtime | Java 17 target | pom already targets 17; assignment requires >= 17 (E02) |
| Framework | Spring Boot 3.x | de facto standard, reviewer familiarity, small config |
| API contract | `openapi.yaml` + openapi-generator (interface only) + springdoc Swagger UI | literal reading of "use an OpenAPI Swagger contract" (R34): the file is the source of truth, the controller implements the generated interface, Swagger UI serves the same file (R39) |
| Database | PostgreSQL 16 | real DB (R08), Decimal and Date types map cleanly (R10, R11) |
| Schema | Flyway migrations | reproducible schema (R09), seed data in a migration |
| Persistence | Spring Data JPA | reviewer familiarity; live-coding follow-up likely adds entities |
| Local run | `docker-compose.yml` + `spring-boot-docker-compose` | `mvn spring-boot:run` starts Postgres for the reviewer (R39) |
| Tests | JUnit 5, AssertJ (already present), Mockito, Testcontainers Postgres | R37; existing test deps stay |

Alternative considered: Spring Data JDBC / JdbcClient instead of JPA. Less code for two tables,
but JPA is what a reviewer expects to see and extends better in the live session.

## 3. Hexagonal layout

Package root stays `org.ikigaidigital` (R01, R17: moving the shared class would break imports).

```
org.ikigaidigital
  TimeDeposit                      shared class, UNCHANGED (R17)
  TimeDepositCalculator            same public signature, delegates to interest policies (R17, R18)
  domain
    interest/  InterestPolicy, BasicInterestPolicy, StudentInterestPolicy, PremiumInterestPolicy,
               InterestPolicies (lookup by plan type, unknown -> no interest, R31)
    model/     PlanTypes (plan type codes), Withdrawal (record), TimeDepositWithWithdrawals (record)
  application
    port/in    UpdateAllBalancesUseCase, GetAllTimeDepositsUseCase
    port/out   TimeDepositRepository (findAll, saveBalances)
    service/   TimeDepositService implements both use cases
  adapter
    in/rest/         TimeDepositController implements generated API interface, response mapper
    out/persistence/ JPA entities, Spring Data repositories, TimeDepositPersistenceAdapter
  config/    Spring wiring (beans for calculator, policies), kept out of domain
  TimeDepositApplication  Spring Boot main
```

Rules that make it hexagonal:
- `domain` and `application` import nothing from Spring, JPA or generated API code.
- Ports are interfaces owned by `application`; adapters depend on ports, never the reverse.
- The shared `TimeDeposit` stays the domain entity. Withdrawals are attached via a separate
  record instead of touching the class (R17, R07).

## 4. Interest calculation refactoring (R18, R19)

Current code: one method with a nested if-chain over plan type strings.
Target: strategy per plan.

```
interface InterestPolicy {
    String planType();
    double monthlyInterest(TimeDeposit deposit);
}
```

`TimeDepositCalculator.updateBalance(List<TimeDeposit>)` keeps its signature and its no-arg
constructor (the existing test uses it). It applies the common 30-day rule (R16, "for any
existing plans"), looks up the policy by plan type, and applies the same rounding as today.
A second constructor accepting the policy lookup is added for Spring wiring; adding a
constructor is not a breaking change.

Adding a future plan = one new `InterestPolicy` class registered in `InterestPolicies`.
No existing class changes (open/closed).

Behaviour preservation, the details that can silently break R26:
- Arithmetic order stays `balance * rate / 12` in `double`. Writing `balance * (rate / 12)`
  gives different floating-point results.
- Rounding stays `new BigDecimal(interest).setScale(2, HALF_UP).doubleValue()`.
  `BigDecimal.valueOf(interest)` is NOT the same and would change results.
- Boundaries stay: `days > 30`, student `days < 366`, premium `days > 45`.
- Unknown plan type: zero interest, no exception (R31).
- Deposits are mutated in place via `setBalance`, as today.

Why not exact BigDecimal arithmetic: it changes results. Measured on half-cent cases:
balance 18.00 at 1% gives 0.01 today (double product is 0.01499999...) but 0.02 with exact
decimal math; 30.00 at 1% gives 0.02 vs 0.03; 10.00 at 3% gives 0.02 vs 0.03. The README
declares the current behaviour correct (R25, R26), so the floating-point path is kept on
purpose and pinned by tests. BigDecimal is used only in the persistence adapter for the
NUMERIC column conversion, outside the protected method.

Characterization tests pin all of the above, including the half-cent cases, against the
ORIGINAL code before refactoring (commit 3), and stay green through every later commit.

## 5. Use cases

- Update all balances (R05): repository.findAll -> deposits -> calculator.updateBalance ->
  repository.saveBalances. One transaction.
- Get all (R06, R07): repository.findAll -> map to response schema
  {id, planType, balance, days, withdrawals}.

## 6. Decisions on ambiguities (each gets a WHY comment in code, R33)

| Id | Question | Decision | Reason |
|---|---|---|---|
| A1 | Method and path for "update all balances" | `POST /time-deposits/update-balances` | Each call accrues another month of interest, so it is not idempotent: POST, not PUT. No natural sub-resource exists, so a verb in the path is the honest choice. |
| A2 | Column naming | README names exactly: `timeDeposits`, `planType`, `timeDepositId` (quoted identifiers in Postgres) | README defines the tables literally; a reviewer checking the schema should find the same names. Cost: quoted identifiers and Hibernate naming strategy config. |
| A3 | Shape of `withdrawals` | Nested array of `{id, amount, date}` | The table exists with exactly these columns; a count would hide data the README bothered to model. |
| A4 | Does update advance `days`? | No | README says update balances, nothing about days. Days stay as stored. |
| A5 | Decimal precision | `NUMERIC(19,2)` for `balance` and `amount`; Double <-> BigDecimal conversion in the persistence adapter | Interest is rounded to 2 decimals today, so 2-decimal storage loses nothing; conversion is confined to the adapter because `TimeDeposit.balance` must stay `Double` (R17). |
| A6 | Do withdrawals affect the calculation? | No | The calculator is declared correct (R25) and never looks at withdrawals. |
| A7 | Response of the update endpoint | `200` with the updated list, same schema as GET | Lets the reviewer see the effect directly in Swagger UI; reuses one schema; still exactly two endpoints (R27). Alternative: `204` empty. |
| A8 | Seed data | Flyway migration inserts a few deposits per plan type plus withdrawals | "Existing plans" (R16) implies data exists; endpoints show something on first run (R39). |
| A9 | Existing placeholder test `assertThat(1).isEqualTo(1)` | Keep the file, replace the placeholder with the real expected balance in the characterization commit | Tests are not part of the protected API (R17); a meaningless assertion is dead code and would be left behind otherwise. |
| A11 | Parameter name `xs` in the protected `updateBalance` | Rename to `timeDeposits` | Parameter names are not part of a Java method signature, so callers are unaffected (R17 holds); leaving `xs` would keep the unclear naming the kata asks to refactor. |
| A10 | Where the solution docs live | Root README gets a short "Solution" section on top linking to `java/README.md`; design and checklist in `docs/` | Assignment README stays intact; reviewer lands on root and finds the way in. |

## 7. Test strategy

| Level | What | Tool |
|---|---|---|
| Unit | Calculator characterization: boundaries 30/31, 45/46, 365/366, rounding, unknown plan, in-place mutation | JUnit 5, AssertJ |
| Unit | One test class per InterestPolicy | JUnit 5 |
| Unit | TimeDepositService with mocked repository | Mockito |
| Integration | Persistence adapter against real Postgres, Flyway applied, quoted identifiers verified | Testcontainers |
| Integration | Both endpoints end to end: GET returns seed; POST updates and persists; response matches contract | SpringBootTest + Testcontainers |

Naming: `should_<behaviour>_when_<condition>`. No test for invalid input (R31).

## 8. Commit plan (R36)

Each commit builds, tests pass, message is imperative and cites checklist ids.

| # | Commit | Closes |
|---|---|---|
| 1 | Set up AI harness and README requirements checklist | R20, R22, R23 |
| 2 | Add design document with commit plan | R18 (design), R33 (assumptions listed) |
| 3 | Add characterization tests and plan type constants (`PlanTypes`, used by calculator and tests) | R25, R26, R13..R16, A9 |
| 4 | Extract interest policy per plan type | R18, R19, R17 verified |
| 5 | Add withdrawal model, ports and application service (core complete, framework-free) | R35, R07, A4, A6 |
| 6 | Add Spring Boot application skeleton | R32 |
| 7 | Add PostgreSQL schema migration and local docker-compose | R08..R11, A2, A5 |
| 8 | Add JPA persistence adapter with Testcontainers test | R08, R37 |
| 9 | Add OpenAPI contract for the two endpoints | R34, R27, R28, A1, A3, A7 |
| 10 | Add REST adapter implementing the contract | R05, R06, R07 |
| 11 | Add seed data migration | A8 |
| 12 | Document how to run and trigger the endpoints | R39, E03, A10 |
| 13 | Document AI-assisted development | R21, R24, R38 |

Hexagonal build-up: domain package appears in 3 (`PlanTypes` needs a home), ports and application
service in 5 (core complete and unit-tested with no framework), adapters in 8 and 10 plug into
existing ports. Nothing is moved without a change that needs it.

Review gate before every commit: tests green, checklist re-run and ticked with hash,
developer reads the full diff and can explain every line.

## 9. Final audit (after commit 13, before the email)

- `git diff upstream/main -- java/src/main/java/org/ikigaidigital/TimeDeposit.java` is empty.
- Calculator diff shows the same public signature and no-arg constructor.
- Route count in `openapi.yaml` is 2 (R27).
- Grep: unused imports, TODO, FIXME, commented-out code, "// create", "// return" style comments.
- Re-read README cold against the ticked checklist (R40).
- Repo public, email with link and the ticked checklist.

## 10. Explicitly NOT built (things AI tends to add)

Input validation, error handlers, get-by-id, create/delete endpoints, health/actuator,
pagination, DTO layers where a record suffices, a generic rules engine, Lombok.
