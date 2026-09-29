# Java style

## Naming

- A name says what the thing is in the domain (`timeDeposits`, `monthlyInterest`,
  `withdrawalsByDepositId`), not its shape or its role in the code (`xs`, `data`, `result`,
  `tmp`, `helper`, `manager`, `util`).
- Classes are nouns for things (`Withdrawal`, `PremiumInterestPolicy`), methods are verbs for
  what happens (`monthlyInterest`, `updateAllBalances`). Booleans read as predicates
  (`earnsInterest`).
- One concept, one word. It is `timeDeposit` everywhere, never `deposit` here and `plan`
  there, unless the README uses the other word for a different thing.
- Constants in `UPPER_SNAKE_CASE` with the unit or meaning in the name
  (`MINIMUM_DAYS_FOR_INTEREST`, not `DAYS`).
- No abbreviations except those the domain already uses (`id`).

## Structure

- Methods do one thing and fit on a screen without scrolling; about 20 lines is the alarm.
- Early return over nested conditionals. No `else` after a `return`.
- One responsibility per class; a class name that needs "and" is two classes.
- Records for immutable values (`Withdrawal`, read models). Fields of a record are final by
  definition, so no defensive copies are needed for immutable members; wrap lists with
  `List.copyOf` in the compact constructor.
- Collections handed out are unmodifiable (`List.of`, `List.copyOf`, `stream().toList()`).
- Streams for transformations, loops for side effects. Never a stream with a mutating lambda.
- No static mutable state. No singletons by hand; Spring owns lifecycle in adapters only.
- Parameters are not reassigned. Local variables are effectively final where possible.

## Money and arithmetic

- The protected calculator keeps `double` and `new BigDecimal(double)` on purpose; see
  `docs/DESIGN.md` section 4. Do not "fix" it.
- Everywhere else a monetary value is a `BigDecimal` with scale 2, `RoundingMode.HALF_UP`,
  and the conversion to and from `Double` happens only at the persistence boundary.

## Comments

- A comment answers "why", never "what". If the code needs a "what" comment, rename or split.
- Documented assumptions (README: "justify them in code comments") are one or two lines at
  the point of the decision, starting with the ambiguity id from `docs/DESIGN.md` (`A1`, `A2`).
- No JavaDoc that restates the signature. No section banners, no author tags, no dates.

## Dead code and hygiene

- No unused imports, parameters, fields, methods, classes, dependencies or configuration.
- No commented-out code. No `TODO`, `FIXME`, `XXX`. Either do it or write it in `docs/DESIGN.md`.
- No `System.out`. Nothing to log in this kata, so no logger either.
- No Lombok: records and short constructors cover the need and keep the code readable
  without a plugin.
- Dependencies are added when the commit that needs them lands, never in advance.

## Exceptions and null

- No exception handling and no validation, as the README states. Unknown plan type earns no
  interest, exactly as the original code does.
- Nothing returns `null`. Empty collections are empty, not null. The two endpoints return
  "all", so there is no lookup that could miss.
