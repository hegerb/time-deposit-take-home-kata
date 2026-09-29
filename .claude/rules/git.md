# Git

## Atomic commits

- One logical change per commit: it does one thing the message names, it compiles, and the
  full test suite passes at that commit. `git bisect` must be able to land on any of them.
- Order in `docs/DESIGN.md` section 8 is the plan. A deviation is fine when the reason is
  written into the design document in the same commit.
- Refactoring and behaviour change never share a commit. Renames are their own commit or
  ride with the change that needs them, never mixed with new logic.

## Messages

- Subject: imperative, capitalised, no period, at most 72 characters
  (`Extract interest policy per plan type`).
- Body: why the change exists and what it closes, in prose, wrapped at 72. Ends with the
  checklist ids it fulfils (`Closes R18, R19.`).
- No ticket numbers (there are none), no emoji, no "WIP", no "fix typo" commits; amend before
  the commit is made, never after.

## What never enters the history

- Generated code (`target/`, generated API sources), IDE files, local notes, secrets,
  credentials, machine-specific paths or tool versions.
- Changes outside `java/`, `docs/`, the root `README.md` "Solution" pointer and the harness
  files. The other language folders and the upstream files are never touched.
- Anything the developer has not read.
