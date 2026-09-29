# API contract and persistence

## OpenAPI contract

- `openapi.yaml` is the source of truth. The controller implements the interface generated
  from it; nothing in the API is described only in annotations.
- Exactly two operations. Adding a third path is a README violation, not a feature.
- Field names in the contract are the README's: `id`, `planType`, `balance`, `days`,
  `withdrawals`. `balance` and `amount` are `number` with `format: decimal` semantics
  documented; `date` is `string`, `format: date`.
- Every operation has an `operationId` that reads as a verb phrase and becomes the Java
  method name (`getAllTimeDeposits`, `updateAllBalances`).
- Swagger UI serves the same file, so what the reviewer clicks is what the code implements.

## HTTP semantics

- Retrieval is `GET` and has no side effects.
- The balance update accrues a month of interest each time it is called, so it is not
  idempotent: `POST`, never `PUT`. The path and response shape follow the decision recorded
  in `docs/DESIGN.md` (A1, A7).
- Responses use `200` for a body and nothing else in this kata. No custom error format,
  because errors are out of scope.

## Schema and migrations

- Table and column names are exactly the README's (`timeDeposits`, `planType`,
  `timeDepositId`), quoted in PostgreSQL. The reason is recorded once, in the migration and
  in `docs/DESIGN.md` (A2), not repeated per entity.
- Flyway migrations are immutable once committed: a change is a new version, never an edit.
- `V1` is schema only, `V2` is seed data. Seed data is small, covers every plan type and
  every interest boundary, and includes withdrawals for at least one deposit.
- Monetary columns are `NUMERIC(19,2)`, `NOT NULL` everywhere the README says required, and
  the foreign key is declared, not implied.

## Persistence adapter

- JPA entities live only in `adapter.out.persistence` and are never returned to the
  application layer. The adapter maps entity to domain and back.
- Balance updates are written in one transaction. Reading all deposits with their withdrawals
  is one query per table, not one query per deposit.
- No repository method that the two use cases do not need.
