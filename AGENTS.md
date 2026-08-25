# AGENTS.md

Instructions for AI coding agents working in the PowerNukkitX repository.

If you are a human, read [CONTRIBUTING.md](CONTRIBUTING.md) instead - it is the authoritative document. This file exists so that agents produce work that passes review instead of getting closed unread. Where the two disagree, CONTRIBUTING.md wins.

---

## The project

PowerNukkitX is a Minecraft: Bedrock Edition server software written in Java. Root package is `org.powernukkitx` - older `cn.nukkit` paths in search results, blog posts, or model memory are **out of date**, do not reintroduce them.

- **Java:** 21. Source and target compatibility are pinned; do not use preview features or a newer language level.
- **Build system:** Gradle (Kotlin DSL, `build.gradle.kts`).
- **License:** LGPL-3.0.
- **Branch:** work off `stable` if bug fixes, if it's major, use `beta`.

---

## Required reading

Read these before you write code. Do not work from assumptions about "how open-source Java projects usually do it" - this project has its own rules, and they are written down.

| File                                     | Read it for                                                                                                                                                                           |
|------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [CONTRIBUTING.md](CONTRIBUTING.md)       | **Mandatory.** The authoritative contribution rules, the AI disclosure requirements, and the list of PRs that get closed without review. Everything in this file is downstream of it. |
| [SECURITY.md](SECURITY.md)               | What counts as a vulnerability and how to report one privately.                                                                                                                       |
| [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) | Applies to anything you draft for a human to post.                                                                                                                                    |
| [README.md](README.md)                   | Project overview, supported versions, how the server is distributed.                                                                                                                  |

Pay particular attention to the **"PRs we close without review"** and **"AI Tool Usage"** sections of CONTRIBUTING.md. They describe the failure modes this file exists to prevent, and they apply to your output whether you read them.

---

## Commands

| Task                                         | Command                     |
|----------------------------------------------|-----------------------------|
| First full build                             | `./gradlew buildSkipChores` |
| Incremental rebuild                          | `./gradlew buildFast`       |
| Unit tests                                   | `./gradlew test`            |
| Distributable jar (`build/powernukkitx.jar`) | `./gradlew shadowJar`       |
| Clean                                        | `./gradlew clean`           |

On Windows use `gradlew.bat`. Run `./gradlew test` before you claim a change works. A change that does not compile is worse than no change.

---

## Layout

```
src/main/java/org/powernukkitx/   Server source
  ├─ block/  blockentity/         Blocks and block entities
  ├─ entity/                      Entities
  ├─ item/                        Items
  ├─ level/                       Worlds, chunks, generators
  ├─ network/                     Protocol, packets, session handling
  ├─ command/                     Commands
  ├─ plugin/  event/  api/        Public plugin API surface
  ├─ config/                      Config models (pnx.yml, server.properties)
  └─ registry/                    Runtime registries (blocks, items, recipes…)

src/main/resources/language/eng/  Source locale - the ONLY locale you may edit
src/test/java/                    Unit tests
src/test/jmh/                     JMH benchmarks
```

---

## Hard rules

Violating any of these gets the pull request closed without a line-by-line review.

1. **Never invent APIs.** Before you call a method, field, config key, event, or registry entry, grep the codebase and confirm it exists at that exact signature. Hallucinated API calls are the single most common reason PRs from agents get closed. If you cannot find it, say so instead of guessing.
2. **One logical change per branch.** Do not fix unrelated things you noticed along the way. Report them separately.
3. **No repo-wide reformatting**, import reordering, whitespace normalisation, or "while I was here" refactors. Match the formatting of the file you are editing, even if you disagree with it.
4. **No new dependencies** without the human opening an issue first.
5. **No dead code**, commented-out blocks, `System.out.println`, `printStackTrace()`, or debug logging left in the diff.
6. **Do not break the public plugin API.** Anything under `plugin/`, `event/`, `api/`, or any `public` method a plugin could call is a compatibility surface. Deprecate rather than delete, and flag the break explicitly.
7. **Do not weaken security checks** to make something work - permission checks, `xbox-auth`, packet validation, bounds checks, path sanitation. If a check is in your way, stop and explain the problem.
8. **Do not touch** `.github/workflows/`, `gradle/wrapper/`, signing config, or release automation unless that is the explicit task.
9. **Do not commit, push, or open a pull request** unless the human explicitly asks. Leave the work on a branch.

---

## Best practices

The hard rules above are what gets a PR closed. This section is what gets a PR *merged quickly*. Order of priority when they conflict: **correctness → not breaking the plugin API → readability → performance**. Never trade the first three for the last one without numbers to justify it.

### Readability

- **Match the surrounding file.** When it is ambiguous, follow the dominant style of the package. Consistency with neighbouring code beats your preferred style, every time.
- **Name things for what they are**, not what type they are. Follow the vocabulary already in the codebase - if the project says `chunk`, `level`, `runtimeId`, use those words rather than inventing `region`, `world`, `numericId`.
- **Keep methods short enough to read at a glance.** Prefer an early return over a nested `if`. Deep nesting in tick or packet code is where bugs hide.
- **Prefer the existing utility classes over new helpers** - grep `utils/` and `math/` before writing one. A duplicate helper is a review comment every time.
- **Comment density should match the neighbouring code.** Explain reasoning, edge cases, and protocol quirks. Do not annotate obvious lines, and never leave narration of your own process ("first we loop over the blocks") in the diff.
- **Refactoring for clarity is welcome inside the code you are already changing.** It is not welcome as a separate sweep across untouched files - that is the repo-wide reformat that hard rule 4 prohibits. The test: would this line have changed anyway to fix the bug? If not, leave it.
- **Fail loudly on programmer error, gracefully on user input.** Throw on an illegal internal state; log and recover on a malformed packet or a bad config value. A malformed packet from a client must never take the server down.
- Use `@NotNull` / `@Nullable` (`org.jetbrains.annotations`) on public API parameters and returns - that is the dominant convention here. Do not introduce `javax.annotation` or a different nullability library.
- Lombok is available and used (`@Slf4j`, `@Getter`, and others). Use it where the surrounding code does; do not hand-roll a getter next to a class that uses `@Getter`.

### Javadoc

- **Every public API you add or change gets Javadoc.** Anything a plugin author could call is public API.
- Document **behaviour, edge cases, and contracts**: what happens on null, on an empty collection, on an out-of-range coordinate, on a chunk that is not loaded. That is what a plugin developer actually needs.
- Note the **threading contract** when it is not obvious - must this be called on the server thread, or is it safe to call async?
- Use `@param`, `@return`, `@throws`, and `{@link}` to related types. Use `@deprecated` with a pointer to the replacement whenever you deprecate something.
- **Do not restate the method name in a sentence.** `/** Gets the level. */` on `getLevel()` adds nothing. If there is genuinely nothing to say beyond the name, say something about the contract instead - or leave it undocumented rather than adding noise.
- Never write Javadoc that describes behaviour you have not verified in the code. Wrong documentation is worse than none.

### Performance

PowerNukkitX targets 20 ticks per second, which at the default tick rate is a **50 ms budget per tick**. Code on that path is hot in a way most application code is not.

**Who that budget belongs to depends on `level-settings.levelThread` in `pnx.yml`,** and you must not assume either mode:

| `levelThread`     | Where levels tick                                       | Budget                                                                           |
|-------------------|---------------------------------------------------------|----------------------------------------------------------------------------------|
| `false` (default) | Inside the main server loop                             | One 50 ms budget **shared** by every level, plus players, network, and scheduler |
| `true`            | Each level on its own dedicated `Level Thread - <name>` | Each level gets its **own** 50 ms budget, ticking in parallel                    |

The mode is fixed at boot and exposed as `Server#isLevelThreadMode()`. Consequences for your work:

- **Never write "the server tick thread" as if there is exactly one.** With level threads enabled there is one per level, plus the main loop.
- A slow level starves every other level in shared mode, but only itself in threaded mode. Performance claims must state which mode was measured - a win in one mode can be a regression in the other.
- Shared state touched from level tick code is contended in threaded mode and uncontended in shared mode. Assuming the latter is how races get introduced.
- **50 ms is the default, not a constant.** The tick rate is configurable; `Server#getNanosPerTick()` is the authoritative budget. Do not hardcode 50 ms or 20 TPS in new code.
- `level-settings.autoTickRate` (default on) can also slow an individual level below the base rate under load, so a level is not guaranteed to tick every server tick.

- **Know whether your code is on the tick path.** Entity ticking, block updates, chunk loading, packet handling, and pathfinding are hot. Startup, registry init, and command handlers are not - do not contort those for speed.
- **Avoid allocation in per-tick paths.** No boxing in loops, no throwaway lambdas or iterators, no temporary collections, no `String` concatenation. Allocation pressure shows up as GC pauses, which show up as TPS drops.
- **Log with `{}` placeholders**, never concatenation: `log.debug("Chunk {} failed", pos)` - not `log.debug("Chunk " + pos + " failed")`. The concatenated version builds the string even when the level is disabled.
- **Prefer primitive collections** where the codebase already does (`it.unimi.dsi.fastutil`, and similar) over boxed `HashMap<Integer, …>` in hot paths.
- **Do not micro-optimise cold code.** Readability wins outside the tick path. "Optimisations" with no measurement attached are a common close reason.
- **Measure before and after.** Performance claims need numbers from `src/test/jmh` or a profiler, not reasoning about what should be faster. State the setup: view distance, player count, world type.
- **Do not cache aggressively to fix a benchmark.** A cache that goes stale is a correctness bug, and correctness outranks speed.

### Concurrency

- **There is no single "server thread" you can assume.** Networking, the scheduler, and player handling run on the main loop, but level ticking runs either on that loop or on one dedicated thread per level, depending on `level-settings.levelThread` (see [Performance](#performance)). Code reachable from a level tick must be correct in **both** modes.
- **`Server#isPrimaryThread()` tells you whether you are on the main loop** - check it rather than assuming. Note its own Javadoc caveat: matching the primary thread implies you are synchronised, but *not* matching does not prove you are unsynchronised. It is a main-loop check, not a lock.
- **Do not touch world, chunk, entity, or player state from an async task** unless the surrounding code clearly already does, and you understand the synchronisation it relies on. Schedule the work back onto the owning thread instead.
- **Cross-level access is the sharp edge.** Reading or mutating level B's state from level A's tick is safe in shared mode and a data race with level threads enabled. Do not do it without explicit synchronisation, and flag it if you must.
- Adding a lock, a `synchronized` block, or a concurrent collection to the tick path is a design decision, not a detail - call it out explicitly in your summary so a human reviews it.
- Deadlocks and races in chunk loading and entity init have bitten this project before. If your change touches those paths, say so loudly.

### Commits

Follow [Conventional Commits](https://www.conventionalcommits.org/): `fix:`, `feat:`, `perf:`, `refactor:`, `docs:`, `chore(deps):`. Write the subject as what changed and why it matters - `fix: prevent deadlock when entity home loads before chunk`, not `fix: bug`.

---

## Testing

- Add unit tests under `src/test/java` for anything testable without a live server. Helpers exist: `GameMockExtension`, `TestPlayer`, `TestUtils`, `TestPluginManager`.
- Performance work needs numbers. Benchmarks live in `src/test/jmh`. "Should be faster" is not a result.
- **You cannot verify gameplay.** You have no Bedrock client and no running server. Never write "tested in-game" or fill in the PR template's testing section yourself. Hand the human a concrete test plan - server steps, what to watch for, what would indicate failure - and let them run it. Every PR requires human in-game testing, and a fabricated test report is treated as a trust violation, not a mistake.

---

## What you must tell your human

Before the PR is opened, hand these to the human author:

1. **Your exact model name and version** - `Claude Opus 5`, `GPT-4o`, `Gemini 2.5 Pro`. The PR template requires a per-model, per-task disclosure table, and "Claude" or "AI" alone is not an accepted model name. State which parts you wrote: code, tests, Javadoc, commit messages, config, or research into how a subsystem works.
2. **A test plan**, since you cannot execute one.
3. **Anything you were unsure about** - an API you could not fully verify, an assumption you made, a case you did not handle. Say it plainly. An acknowledged gap is fine; a silent one is what gets a PR closed.

### What you must not write

- **The PR description.** It must be in the human's own words. Give them the facts to write it from; do not draft the text.
- **Replies to review comments.** Same rule.
- **Issue reports** based on your reading of the code. Never open a bug report for something you have not seen reproduce on a real server.
- **Media assets.** No generated images, icons, audio, or video.

---

## Security

Found something exploitable - remote code execution, path traversal, permission or auth bypass, a crash reachable from an unprivileged client? **Do not open a public issue or pull request, and do not include a proof of concept in a public branch.** Tell the human directly and point them at [SECURITY.md](SECURITY.md) for private reporting.

---

## Working style

- Read before you write. Grep the codebase for existing patterns rather than importing conventions from other projects.
- Prefer the smallest change that fixes the problem, but do not be afraid to refactor if it makes the code clearer or more maintainable.
- When the task is ambiguous, ask instead of picking a direction and building on it.
- If you cannot do part of the task, say which part and why. Do not quietly narrow the scope and report success.
