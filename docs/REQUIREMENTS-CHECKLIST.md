# README checklist, one item per sentence

Every sentence of the kata README is quoted, followed by what it means for the solution.
Status: [ ] open · [x] done, verified, with commit hash. Re-run before every commit.

## Title and context

- [x] R01 (7aef83b) "Time Deposit Refactoring Kata - Take-Home Assignment"
  -> It is a REFACTORING kata. Existing code is the starting point, not a rewrite from scratch. Keep package, class names and structure recognisable.
- [x] R02 (7aef83b) "XA Bank Time Deposit"
  -> Banking domain: money handling, so precision and rounding matter and must not drift from today.
- [x] R03 (0ddaba5) "A junior developer implemented domain logic for a time deposit system but did not complete the API functionality."
  -> Domain logic exists and is authoritative. What is missing is the API layer and persistence. Our job is completing, not replacing.
- [x] R04 (0ddaba5) "Your task is to refactor the existing codebase to implement all required functionalities based on the provided business requirements, ensuring no breaking changes occur."
  -> Three obligations: refactor (improve structure), implement ALL listed functionality, and zero breaking changes. All three are graded.

## 1. API endpoints

- [x] R05 (0ddaba5) "Create a RESTful API endpoint to update the balances of all time deposits in the database."
  -> One endpoint, RESTful, no parameters, operates on every row in the DB, persists the result. Uses the existing calculator.
- [x] R06 (0ddaba5) "Create a RESTful API endpoint to retrieve all time deposits."
  -> One endpoint, GET, returns every deposit. No filtering, paging or get-by-id.
- [x] R07 (0ddaba5) "The GET endpoint should return a list of all time deposits with the following schema: id, planType, balance, days, withdrawals"
  -> Response is a JSON array. Each item has exactly these five field names, spelled exactly like this. Withdrawals belong to the deposit, so they are loaded with it.

## 2. Database setup

- [x] R08 (5a51911) "Store all time deposit plans in a database."
  -> A real database, not an in-memory list. The update endpoint reads from and writes to it.
- [x] R09 (3ff09a7) "Define the following tables:"
  -> Schema is defined by us, reproducibly (migration script), with exactly these two tables.
- [x] R10 (3ff09a7) "timeDeposits: id Integer (primary key), planType String (required), days Integer (required), balance Decimal (required)"
  -> Table name and four columns as written. All NOT NULL. `balance` is a decimal column, even though the Java class uses Double. Naming case is an ambiguity (A2).
- [x] R11 (3ff09a7) "withdrawals: id Integer (primary key), timeDepositId Integer (foreign key, required), amount Decimal (required), date Date (required)"
  -> Table name and four columns as written. FK to timeDeposits, NOT NULL. `date` is a DATE, not a timestamp.

## 3. Interest calculation

- [x] R12 (7aef83b) "Implement logic to calculate monthly interest based on the plan type:"
  -> Monthly = yearly rate divided by 12, as the existing code does. Plan type is the dispatch key, so the design should make plan type a first-class concept.
- [x] R13 (d50f457) "Basic Plan: 1% interest"
  -> 0.01 / 12 of balance per update. Existing code matches.
- [x] R14 (d50f457) "Student Plan: 3% interest (no interest after 1 year)"
  -> 0.03 / 12, and nothing once past one year. Existing code: interest while `days < 366`. Keep that boundary exactly.
- [x] R15 (d50f457) "Premium Plan: 5% interest (interest starts after 45 days)"
  -> 0.05 / 12, only when `days > 45`. Existing code matches. Keep the boundary exactly.
- [x] R16 (d50f457) "No interest is applied for the first 30 days for any existing plans."
  -> Applies to all plans before the per-plan rule. Existing code: `days > 30`. Day 30 earns nothing, day 31 earns.

## 4. Refactoring constraints

- [x] R17 (7aef83b) "Do not introduce breaking changes to the shared TimeDeposit class or modify the updateBalance method signature."
  -> `TimeDeposit`: constructor, getters, `setBalance`, field types and mutability stay. `TimeDepositCalculator.updateBalance(List<TimeDeposit>)`: same name, parameter, return type, package. Anything needing more data (withdrawals) lives outside this class.
- [x] R18 (7aef83b) "Ensure the design is extensible to accommodate future complexities in interest calculations."
  -> Adding a new plan or rule must not require editing an if-chain. Strategy per plan type, selected by plan type. This is the core refactoring the reviewer looks at.

## 5. Code quality

- [x] R19 (7aef83b) "Adhere to SOLID principles, design patterns, and clean code practices where applicable."
  -> SRP per class, open/closed via strategies, DIP via ports. Clean code: no dead code, no unused imports, no redundant comments, domain naming. "Where applicable" means no pattern for its own sake.

## 6. AI-assisted development

- [x] R20 (94e8490) "Set up an AI harness or agent workflow and use it throughout the development for this take-home exercise."
  -> Claude Code with a committed project config, used from the first commit to the last. Evidence should be visible in the repo.
- [x] R21 (94e8490) "Briefly document the tools and setup used (e.g., LLMs, coding assistants, agentic frameworks, configuration)."
  -> A short section in the README or a dedicated doc: model, CLI version, config files, how they are wired.
- [x] R22 (21ef897) "Ensure your AI setup is practical and reproducible."
  -> Someone cloning the repo can run the same setup. Config lives in the repo.
- [x] R23 (21ef897) "Include any custom rules, system prompts, or agent configurations used."
  -> Commit CLAUDE.md, rules and this checklist. Only the rules that shaped this solution, nothing external.
- [x] R24 (94e8490) "Include a brief summary of which parts of the solution were AI-assisted and why."
  -> Honest per-area summary: what AI drafted, what was reviewed and changed by the developer, and the reason for using AI there.

## Important guidelines

- [x] R25 (d50f457) "The existing TimeDepositCalculator.updateBalance method is functioning correctly."
  -> Its current behaviour IS the spec, including boundaries and rounding. Where README wording is looser, the code wins.
- [x] R26 (7aef83b) "Ensure its behavior remains unchanged after refactoring."
  -> Characterization tests written against the ORIGINAL code before touching it, kept green after every refactoring step.
- [x] R27 (60faa0e) "The final solution must include exactly two API endpoints."
  -> Count of routes in the OpenAPI contract is two. Swagger UI is documentation, not an API endpoint (assumption to state).
- [x] R28 (60faa0e) "Do not develop additional endpoints."
  -> No health check, no actuator, no get-by-id, no create, no delete, no seed endpoint.
- [x] R29 (21ef897) "Do not create a pull request or a new branch in the ikigai-digital repository."
  -> All work stays in our fork. Never push to upstream.
- [x] R30 (21ef897) "Instead, fork the repository into your own GitHub repository and develop the solution there."
  -> Fork, keep upstream history, commit on the fork.
- [x] R31 (0ddaba5) "Handling invalid input or exceptions is not required."
  -> Do not add validation or error handlers. Unknown plan type keeps earning zero interest silently, as today.
- [x] R32 (93ef9d5) "Use any tools, frameworks, or libraries you find suitable."
  -> Free choice of stack, but each dependency should be justifiable.
- [x] R33 (cc4407a) "In case of ambiguity, make logical assumptions and justify them in code comments."
  -> Each ambiguity (A1..A7) gets a short WHY comment at the place where the assumption is made in code. This is the one place comments are explicitly required.

## Preferred stack

- [x] R34 (60faa0e) "Use an OpenAPI Swagger contract."
  -> An OpenAPI document is the API contract, checked in, and Swagger UI serves it. Contract-first preferred.
- [x] R35 (0ddaba5) "Embrace Hexagonal Architecture."
  -> Domain and application core with no framework imports; ports as interfaces; REST inbound adapter and DB outbound adapter. Package layout must make this obvious.
- [x] R36 (94e8490) "Follow atomic commit practices."
  -> One logical change per commit, each builds and passes tests, imperative message that names the README item it fulfils.
- [x] R37 (5a51911) "Utilize testcontainers."
  -> DB integration tests run against a real database container, not H2.
- [x] R38 (94e8490) "Leverage AI-assisted development tools for code generation, testing, and refactoring."
  -> Use AI in all three activities and say so in R24.

## Submission instructions

- [x] R39 (e084106) "Provide clear instructions on how to trigger the endpoints using the Swagger contract."
  -> README section: how to start the app and the DB, the Swagger UI URL, and how to call each endpoint from it.
- [x] R40 (at submission) "Email the link to your public GitHub repository."
  -> Repo visibility public. Final sweep of this checklist before the email.

## Email (outside README, still binding)

- [x] E01 (at submission) 48 hours from receipt of the email.
- [x] E02 (93ef9d5) Solution builds and runs on Java 17 (pom target); newer JDKs work too.
- [x] E03 (e084106) Assumptions clearly stated when submitting (README section listing A1..A7 in addition to the code comments, R33).

## Ambiguities to decide and document (R33)

- A1 HTTP method and path for "update all balances": no body, mutates every row.
- A2 Column naming: README camelCase vs SQL snake_case.
- A3 Shape of `withdrawals` in GET: nested objects {id, amount, date} vs count.
- A4 Whether the update endpoint also advances `days`. README does not say so; keep days as stored.
- A5 Decimal precision and scale for `balance` and `amount`; `TimeDeposit` keeps `Double` (R17).
- A6 Withdrawals are not subtracted by the calculator; README never says they should be; keep behaviour (R25).
- A7 Response body of the update endpoint: 204 empty vs the updated list.
