# Security Policy

PowerNukkitX is a Minecraft: Bedrock Edition server software. A vulnerability here can mean a compromised host machine, hijacked player data, or a server that can be taken offline by anyone who can connect to it. We take reports seriously, and we're grateful to everyone who takes the time to send one.

---

## Supported Versions

PowerNukkitX ships as a rolling snapshot built from `master`. There are no long-term support branches.

| Version                    | Supported                          |
|----------------------------|------------------------------------|
| Latest snapshot (`master`) | ✅ Fixes land here                  |
| Older snapshots / releases | ❌ Not supported - update first     |
| Forks and modified builds  | ❌ Report to the fork's maintainer  |

**Before reporting, reproduce the issue on the latest snapshot.** Fixes are only issued forward on `master`; we do not backport to older builds.

---

## Reporting a Vulnerability

**Do not open a public issue, pull request, Discord message, or forum post for a security vulnerability.** Public disclosure before a fix exists puts every server running PNX at risk.

Report privately through **GitHub Private Vulnerability Reporting**:

👉 **[Report a vulnerability](https://github.com/PowerNukkitX/PowerNukkitX/security/advisories/new)**

(Also reachable from the repository's **Security** tab → **Report a vulnerability**.)

This creates a private advisory visible only to you and the maintainers. It stays private until we publish it.

If you cannot use GitHub Private Vulnerability Reporting for some reason, ask a maintainer on [Discord](https://discord.com/invite/powernukkitx-944227466912870410) for a private channel - **without describing the vulnerability in the message**.

### What to include

The more of this you provide, the faster we can act:

- The **commit hash** of the build you tested (`/version`)
- **Java version** and OS
- **Bedrock client version**, if the attack comes from a client
- **Impact**: what an attacker gains - code execution, file access, elevated permissions, server crash
- **Reproduction steps**, ideally a minimal proof of concept: a packet capture, a script, a test plugin, or a world file
- **Preconditions**: does the attacker need to be logged in? opped? does the server need a specific plugin or config?
- Any **suggested fix**, if you have one

---

## Scope

### In scope

- **Remote code execution** on the server host
- **Arbitrary file read, write, or deletion** outside the intended server directories, including path traversal via resource packs, world files, or plugin loading
- **Authentication and permission bypass** - operator escalation, permission check bypass, Xbox Live authentication or encryption bypass, spoofing another player's identity
- **Denial of service from client input** - a malformed packet, chunk, item, NBT payload, or command that crashes the server, hangs a tick loop, or exhausts memory or disk
- **Unsafe deserialization** of untrusted data (world saves, resource packs, network payloads) reachable by an unprivileged player
- **Duplication glitches** that allow an unprivileged player to create items or blocks they shouldn't be able to, or to bypass economy restrictions

### Out of scope

- **Bugs in third-party plugins.** Report those to the plugin's author. Vulnerabilities in the plugin *API itself* that let unprivileged players escalate are in scope; a badly written plugin is not.
- **Plugin sandbox escapes.** PNX does not sandbox plugins. A plugin runs with full JVM privileges by design - installing an untrusted plugin is equivalent to running untrusted code.
- **Anything requiring operator or console access.** Ops are trusted by design.
- **Anything requiring physical or filesystem access** to the server host, or a pre-compromised machine.
- **Vanilla Minecraft parity bugs** These are normal issues - file them publicly.
- **Client-side vulnerabilities** in the Minecraft Bedrock client itself. Report those to Mojang.
- **Misconfiguration**: exposing RCON to the internet with a weak password, running the server as root, disabling `xbox-auth`, and similar. We'll happily take documentation PRs about these.
- **Dependency CVEs with no demonstrated impact on PNX.** Tell us anyway - just open a normal issue, or let Dependabot handle it.
- Missing security headers, DMARC/SPF records, self-XSS, clickjacking on marketing pages, and other findings from automated scanners with no concrete attack path.

Not sure whether something is in scope? Report it privately. We'd rather triage an out-of-scope report than miss a real one.

---

## What Happens Next

PowerNukkitX is maintained by volunteers in their free time. We don't promise a fix deadline we can't meet, so here's what we actually commit to:

1. **Acknowledgement.** We aim to confirm receipt within a few days.
2. **Triage.** We reproduce the issue and tell you our assessment of severity and whether we consider it in scope.
3. **Fix.** We work on a patch and keep you updated on progress. Timelines depend on complexity and maintainer availability - we'll be honest with you about both.
4. **Coordinated disclosure.** We agree a disclosure date with you, rather than working to a fixed clock. Critical, actively exploited issues get disclosed as fast as we can ship a fix; lower-severity ones can wait for a convenient release.
5. **Publication.** We publish a GitHub Security Advisory, credit you by name or handle (or keep you anonymous, your choice), and announce the fixed build on Discord.

If you disagree with our severity assessment or feel a report is going nowhere, say so in the advisory thread - we'd rather hear it from you than read about it elsewhere.

---

## Disclosure Expectations

- **Give us a chance to fix it before going public.** We don't set a fixed embargo window, but we do ask you not to publish, demo, or sell details of an unfixed vulnerability.
- **Don't test against servers you don't own.** No attacking public PNX servers, no testing on other people's infrastructure, no accessing or exfiltrating other people's data. Use your own local server.
- **Don't use the vulnerability beyond what's needed to demonstrate it.** Stop at proof of concept.

We do not run a paid bug bounty program - this is a volunteer open-source project with no funding for one. What we can offer is credit in the advisory and in the release notes, and our genuine thanks.

---

## For Server Operators

If a security advisory is published, the fix is in the latest snapshot. To stay safe:

- Update to the [latest release](https://github.com/PowerNukkitX/PowerNukkitX/releases/latest) regularly
- Watch this repository (**Watch** → **Custom** → **Security alerts**) to be notified of advisories
- Keep `xbox-auth` enabled
- Never expose RCON to the public internet
- Run the server as an unprivileged user, not root or Administrator
- Only install plugins from sources you trust - plugins run with full JVM privileges

---

Thank you for helping keep PowerNukkitX and its server operators safe.
