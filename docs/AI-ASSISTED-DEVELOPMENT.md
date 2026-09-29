# AI-assisted development

## Tools and setup

| What | Used |
|---|---|
| Coding assistant | Claude Code (Anthropic's CLI agent), Claude model, run in the terminal in this repository |
| Project instructions | `CLAUDE.md` at the repo root, loaded automatically by Claude Code |
| Rules | `.claude/rules/*.md`: architecture, Java style, testing, API and persistence, git; loaded with `CLAUDE.md` |
| Permissions | `.claude/settings.json`: the agent may run Maven and read-only git, may stage and commit only on request, may never push |
| Working documents | `docs/REQUIREMENTS-CHECKLIST.md` (README traced sentence by sentence), `docs/DESIGN.md` (architecture, decisions, commit plan) |

The rule files are the generic subset of a broader Claude Code configuration the developer maintains for team use; nothing employer-specific is included.

## Reproduce

1. Install Claude Code and open a terminal at the repository root.
2. Run `claude`. `CLAUDE.md` and `.claude/rules/` are picked up automatically; `.claude/settings.json` applies the permissions.
3. Work the same way: read the README, keep the checklist current, one planned commit at a time, `mvn -f java/pom.xml test` before each commit.

## Workflow

1. README read and turned into the checklist before any code. Design and commit plan written and reviewed next.
2. Per commit: the agent drafts code and tests, the developer reads the full diff and asks for changes, tests run, the checklist is ticked with the commit hash, the developer approves the commit.
3. For the two core commits (interest policies, application core) an independent review agent with no access to the drafting conversation reviewed the diff; its findings were fixed before committing.
4. The refactored calculator was compared with the original algorithm on about 169 million generated inputs with zero differences, in addition to the characterization tests.

## What was AI-assisted, and why

| Area | AI assistance | Developer decisions |
|---|---|---|
| README analysis, checklist | Drafted the sentence-by-sentence checklist | Reviewed every item; insisted on treating the README as the specification |
| Design | Drafted layout, decisions on ambiguities, commit plan | Chose Java, the strategy pattern, package names, the composed-method style of the calculator, what not to build |
| Characterization tests | Drafted tests and expected values | Required plan type constants shared by tests and code; verified the half-cent cases |
| Interest policies | Drafted the strategy classes | Rejected constants on the interface (became `AnnualRate`); required positive, README-worded predicates and named private methods |
| Ports, service, adapters, contract | Drafted code, tests, migrations, OpenAPI file | Set the port granularity, the use-case naming, the test levels, one shared database container |
| Documentation | Drafted READMEs and this file | Cut explanatory prose; kept only what the code and README do not already say |

AI was used for generation, testing and refactoring throughout because it makes the mechanical parts fast; every design choice, every rejected draft and every commit was the developer's.
