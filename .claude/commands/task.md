---
description: Implement a task the way this repository works - plan against the README and design first, then one reviewed change with tests, stop before committing
argument-hint: <the task in one or two sentences>
allowed-tools: Bash(mvn -f java/pom.xml test:*), Bash(git status:*), Bash(git diff:*), Bash(git log:*)
---

Task: $ARGUMENTS

Work in this order and report after each step. Do not commit; the developer commits.

1. Read `README.md`, `docs/REQUIREMENTS-CHECKLIST.md` and `docs/DESIGN.md` sections 3, 4 and 6.
   State which README items and which design decisions the task touches, and whether
   it conflicts with any hard constraint in `CLAUDE.md`. If it does, say so and stop.
2. Restate the task as a business rule in one sentence, in the README's words.
3. List the files to add or change, in the order they will be touched, with one line each on
   why. Production code first, then tests, then seed data or docs. Name every existing test
   that uses a name the task introduces.
4. Implement in that order. Follow `.claude/rules/`. New rules go into their own named
   methods; nothing in the calculator's signature, in `TimeDeposit`, or in the two existing
   endpoints changes.
5. Run `mvn -f java/pom.xml test` once. Fix failures that the change caused; do not weaken a
   characterization test.
6. Show `git status --short` and a short summary of the diff, then propose a commit message
   that names the checklist items it closes. Stop and wait.
