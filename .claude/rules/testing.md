# Testing

## Levels

| Level | Subject | Tools | Runs |
|---|---|---|---|
| Characterization | Original `updateBalance` behaviour | JUnit 5, AssertJ | Every build |
| Unit | Interest policies, application service with a mocked port | JUnit 5, AssertJ, Mockito | Every build |
| Integration | Persistence adapter against real PostgreSQL | Testcontainers, Spring Boot test slice | Every build, needs Docker |
| End to end | Both endpoints through HTTP against real PostgreSQL | SpringBootTest, Testcontainers | Every build, needs Docker |

## Rules

- Characterization tests are written against the original code before the first refactoring
  commit and never weakened afterwards. They pin exact balances, including half-cent cases
  where floating-point order matters, and the boundaries 30/31, 45/46, 365/366 days.
- Names: `should_<expected behaviour>_when_<condition>`. The name is the specification; a
  reader should not need the body to know what is tested.
- One behaviour per test. Several assertions are fine when they describe one outcome.
- Arrange, act, assert, separated by a blank line. No logic in tests: no loops, no
  conditionals, no computing the expected value with the code under test.
- Test data is built in the test or in a small private factory method in the same class. No
  shared mutable fixtures, no test inheritance.
- Mocks only at ports. Never mock a domain object or a value.
- Integration tests use the real Flyway migrations, so the schema under test is the schema
  that ships.
- Cover: happy path per plan type, each boundary, empty list, unknown plan type, in-place
  mutation of the list elements. Do not cover invalid input or exceptions; the README
  excludes them.
- A test that cannot fail is dead code. The placeholder `assertThat(1).isEqualTo(1)` is
  replaced with a real expectation.
