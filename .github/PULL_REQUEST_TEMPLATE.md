<!--
  Read CONTRIBUTING.md before submitting:
  https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CONTRIBUTING.md

  PRs that leave this template unfilled, or fill it with generated filler,
  are closed without review. Delete the HTML comments as you go.

  SECURITY: never report a vulnerability in a public PR. See SECURITY.md.
-->

## What does this PR do?

<!-- In your own words. One or two sentences. No LLM-written descriptions. -->

## Why?

<!-- What problem does this solve? Link the issue: Closes #123 -->

Closes #

## Type of change

<!-- Tick what applies. -->

- [ ] Bug Fix
- [ ] New Feature
- [ ] Performance
- [ ] Refactor (no behaviour change)
- [ ] Documentation
- [ ] Build / CI / dependencies

## How was this tested?

<!--
  REQUIRED. Every PR must be tested by a human on a running server.
  "Tested" or "works fine" is not a test report. Be specific:
  what you did, what you saw, what you compared it against.
-->

- **Server build tested:** <!-- commit hash -->
- **Bedrock client version:** <!-- e.g. 1.21.x -->
- **Plugins loaded:** <!-- none / list them -->

**Steps performed and results:**

1.
2.

- [ ] I built the server and ran it with this change
- [ ] I reproduced the original problem and confirmed this fixes it (bug fixes)
- [ ] Existing unit tests pass (`gradlew test`)
- [ ] I added tests for the new logic, or explained below why that isn't practical

## Breaking changes / API impact

<!-- Any public API changed, removed, or deprecated? Any config or save-format change? Write "None" if none. -->

None

## Performance notes

<!-- Only for performance-sensitive changes: benchmark numbers, before/after TPS, profiling notes. Otherwise, write "N/A". -->

N/A

## AI usage disclosure

<!--
  REQUIRED. See the "AI Tool Usage" section in CONTRIBUTING.md.
  Name the exact model per task - "Claude" or "AI" is not a model name.
  If no AI was involved at any stage, delete the table and write "No AI used."
-->

| Model | What it did |
|-------|-------------|
|       |             |

- [ ] The disclosure above covers **all** AI use in this PR - code, tests, Javadoc, commit messages, config, and research
- [ ] I have read every line of this diff myself and can explain any of it
- [ ] This PR description and any review replies are written by me, not generated

## Checklist

- [ ] My change follows the existing code style
- [ ] The PR is one logical change, with no unrelated reformatting or refactors
- [ ] Public API additions/changes have Javadoc
- [ ] No dead code, commented-out blocks, or debug logging left behind
- [ ] Commit messages follow Conventional Commits
- [ ] My contribution is licensed under LGPL-3.0, and any third-party code is attributed
- [ ] "Allow maintainer edits" is enabled
