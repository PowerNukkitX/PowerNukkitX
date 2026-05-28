# Contributing to PowerNukkitX

Welcome, and thank you for investing your time in contributing to PowerNukkitX! Whether you're fixing a bug, proposing a new feature, or improving documentation — every contribution matters and we're genuinely glad to have you here.

This guide walks you through the full contribution workflow: opening issues, making changes, and submitting pull requests.

---

## 🌱 New to Open Source?

If this is your first time contributing to an open-source project, these resources are a great place to start:

- [Finding ways to contribute to open source on GitHub](https://docs.github.com/en/get-started/exploring-projects-on-github/finding-ways-to-contribute-to-open-source-on-github)
- [Setting up Git](https://docs.github.com/en/get-started/getting-started-with-git/set-up-git)
- [Understanding GitHub flow](https://docs.github.com/en/get-started/using-github/github-flow)
- [Collaborating with pull requests](https://docs.github.com/en/github/collaborating-with-pull-requests)

Also make sure to read the [README](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/README.md) to get an overview of the project before diving in.

---

## 📋 Issues

### Reporting a bug or problem

If you spot something wrong, [search existing issues](https://github.com/PowerNukkitX/PowerNukkitX/issues) first to avoid duplicates — your problem may already be tracked or resolved. If no related issue exists, open a new one using the appropriate [issue form](https://github.com/PowerNukkitX/PowerNukkitX/issues/new/choose).

For bug reports, please include:
- Your server version and Java version (JDK 21 required)
- Steps to reproduce the issue
- What you expected to happen vs. what actually happened
- Any relevant plugins or configuration

> **Note:** Issues are not a support channel. For general help, questions, or discussion, please use our [Discord server](https://discord.com/invite/powernukkitx-944227466912870410).

### Working on an existing issue

Browse [open issues](https://github.com/PowerNukkitX/PowerNukkitX/issues) and use `labels` to filter for areas that interest you. We generally don't pre-assign issues — if you find something you want to fix, you're welcome to open a PR. For significant changes, it's worth leaving a comment on the issue first so we can align on the approach before you invest time in implementation.

---

## 🔧 Making Changes

1. **Fork** the repository and clone your fork locally.
   - [How to fork with IntelliJ IDEA](https://www.jetbrains.com/help/idea/fork-github-projects.html#fork)
   - [How to clone from GitHub](https://www.jetbrains.com/help/idea/manage-projects-hosted-on-github.html#clone-from-GitHub)
2. Make sure you have **JDK 21** installed.
3. Make your changes.
4. Build and verify using the Gradle tasks:
   - `buildSkipChores` — first full build
   - `buildFast` — apply incremental changes
   - `clean` — clean the build folder

   To produce a distributable `powernukkitx.jar`, run the `shadowJar` task. The output will be placed in `build/`.

   ![Build tasks](https://github.com/PowerNukkitX/PowerNukkitX/raw/master/.github/img/001.png)

5. **Start the server and test your changes** before opening a PR.

   ![Run server](https://github.com/PowerNukkitX/PowerNukkitX/raw/master/.github/img/002.png)

---

## 🎨 Code Quality & Style

- Follow the existing **code style and formatting conventions** used throughout the codebase. When in doubt, match the surrounding code.
- Write **meaningful commit messages**, e.g. `fix: prevent crash when chunk loads before world init`.
- Add or update **Javadocs** for any public API you introduce or modify.
- Avoid committing **dead code, commented-out blocks, or debug logging**.
- If your change is performance-sensitive, include **benchmarks or profiling notes** in your PR description.
- Keep changes **focused and minimal** — avoid unrelated formatting changes, whitespace fixes, or refactors outside the scope of your PR.

---

## 📬 Pull Requests

When your changes are ready, open a pull request:

- Fill in the **"Ready for review" template** completely — it helps maintainers understand what you changed and why.
- [Link your PR to the related issue](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue) using `Closes #123` or `Fixes #123`.
- Enable **"Allow maintainer edits"** so we can make minor adjustments to help with merging.
- **All PRs must be tested by a human** before submission — no exceptions.
- Respond to review feedback and mark conversations as [resolved](https://docs.github.com/en/github/collaborating-with-pull-requests/commenting-on-a-pull-request#resolving-conversations) once addressed.
- If you hit merge conflicts, this [git tutorial](https://github.com/skills/resolve-merge-conflicts) can help you work through them.

Once merged, your contributions will be publicly visible in the project. You're officially a PowerNukkitX contributor! 🎉

---

## 🤖 AI Tool Usage

We recognize that AI tools can be helpful, and we don't ban their use outright — but we ask for transparency and responsibility:

- **No LLM-generated text in GitHub discussions or PR descriptions.** We want to hear from *you*. If you can't explain what your PR does in your own words, it needs more review before submission.
- **Be transparent about AI usage.** One sentence is enough: *"This PR used GitHub Copilot to assist with boilerplate"* or *"ChatGPT helped draft the Javadoc comments."*
- **You must understand your own code.** AI-assisted contributions are your responsibility — if a maintainer asks a question about your change, you should be able to answer it.
- **Using a spell checker or translator is fine.** If you translate your PR description, please also include the original language version below the English text.
- **No AI-generated media assets** — images, icons, audio, or video. We genuinely prefer a rough human sketch over a polished AI-generated image. ❤️

Repeated misuse of AI — such as submitting untested, unreviewed, or hallucinated code — may result in closed PRs or loss of contribution access.

---

## ⚖️ Licensing & Copyright

- By submitting a pull request, you agree that your contribution will be licensed under the **same license as PowerNukkitX**.
- Do not include code copied from incompatibly-licensed projects without explicit permission and proper attribution.
- If your contribution is derived from another open-source project, note the original source and its license in your PR description.
- When using AI tools for code generation, review the output carefully to ensure it is original and does not reproduce copyrighted material.

---

## 🤝 Community Conduct

- Be kind, patient, and constructive — especially with newcomers. We all started somewhere.
- **Critique code, not people.** Feedback should always focus on the work, not the contributor.
- Maintainers have the final say on whether a change fits the project's direction. A closed PR is not a personal rejection.
- Harassment, discrimination, or hostile behavior of any kind will not be tolerated and may result in a permanent ban.

---

We know a large codebase can be daunting at first. If you're unsure where to start or need guidance, don't hesitate to reach out on [Discord](https://discord.com/invite/powernukkitx-944227466912870410) — we're happy to help point you in the right direction. Happy contributing! 🚀

---

> This contributing guide was written by human and improved with the help of [Claude Sonnet 4.5](https://claude.ai).
