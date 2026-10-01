# Time Deposit Kata: working rules for the AI harness

This repository is a take-home refactoring exercise. `README.md` is the specification.
`docs/REQUIREMENTS-CHECKLIST.md` breaks it into one item per README sentence and
`docs/DESIGN.md` records the architecture, the assumptions and the commit plan.
Detailed conventions live in `.claude/rules/` and are loaded with this file.

## Process

- The solution lives in `java/`. Other language folders are untouched.
- Every change must map to a checklist item. If it maps to none, it is not made.
- Before every commit: `mvn -f java/pom.xml test` is green, the checklist is re-run and the
  fulfilled items get the commit hash, and the developer reads the full diff.
- One logical change per commit. Imperative message, cites the checklist ids it closes.
- The AI drafts and commits only on the developer's explicit request, after the developer has
  read the diff. Nothing is committed that the developer cannot explain. The AI never pushes.

## Hard constraints from the README

- `TimeDeposit` and the signature of `TimeDepositCalculator.updateBalance` are never changed.
- `updateBalance` behaviour is fixed, including its `double` arithmetic order and
  `new BigDecimal(double)` rounding. Characterization tests guard it.
- Exactly two REST endpoints. No health, no get-by-id, no create, no delete.
- No input validation or exception handling. It is explicitly not required.
- Ambiguities get a stated assumption in a code comment at the place of the decision.

## Command

- `/task <what to build>`: plans against the README and the design, implements with tests,
  stops before the commit. Defined in `.claude/commands/task.md`.

## Rule files

- `.claude/rules/architecture.md`: hexagonal layout, dependency direction, what is a port.
- `.claude/rules/java-style.md`: naming, structure, immutability, comments, dead code.
- `.claude/rules/testing.md`: test levels, naming, what to cover, what not to test.
- `.claude/rules/api-and-persistence.md`: OpenAPI contract, HTTP semantics, schema, migrations.
- `.claude/rules/git.md`: atomic commits, message format, what never enters the history.
