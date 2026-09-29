# Architecture: hexagonal, dependencies point inward

## Layers and what each one owns

| Package | Owns | May import |
|---|---|---|
| `org.ikigaidigital` (root) | The shared `TimeDeposit` and `TimeDepositCalculator` as given | JDK, domain (the calculator delegates to policies; `TimeDeposit` cannot move because of R17, so root and domain reference each other) |
| `domain` | Business rules: interest policies, `Withdrawal`, read models | JDK, root package |
| `application` | Use cases (inbound ports), outbound ports, application services | JDK, domain, root |
| `adapter.in.rest` | Controller implementing the generated API interface, response mapping | application, generated API, Spring Web |
| `adapter.out.persistence` | JPA entities, Spring Data repositories, port implementation | application, domain, JPA, Spring Data |
| `config` | Bean wiring only | everything above |

Rules:
- `domain` and `application` contain no annotation and no import from Spring, JPA, Jackson or
  generated code. A grep for `org.springframework`, `jakarta.persistence` and the generated
  package in those two packages returns nothing.
- Adapters depend on ports. Ports never know adapters exist.
- Mapping between layers happens at the boundary, in the adapter that crosses it. The domain
  never sees a DTO or an entity.

## What is a port and what is not

- A port is an interface owned by `application` that the core uses to talk to the outside:
  the repository (outbound) and the two use cases (inbound). Each gets its second
  implementation immediately through the test double, so the interface is not speculative.
- `InterestPolicy` is a strategy, not a port: it has three implementations from the first
  commit and lives in `domain`.
- No other interfaces. No `Service` + `ServiceImpl` pairs, no interface for a mapper, no
  interface for a lookup that has one implementation.

## Extensibility rule for interest calculation

Adding a plan type means adding one `InterestPolicy` class and one line in the registry's
`standard()` list. No other existing class changes. The common 30-day rule stays in the calculator because the README states it
for all plans; per-plan thresholds stay inside the plan's policy.
