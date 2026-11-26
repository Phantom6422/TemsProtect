# Security Policy

TemsProtect is maintained for **internal use only**. Public support is not provided. Security reports are still welcome and will be handled privately.

## Reporting a vulnerability

Please do **not** open a public GitHub issue, pull request, or Discord channel message for a security problem.

Report it privately through either channel:

- **Email:** [mail@phantomsnetwork.org](mailto:mail@phantomsnetwork.org)
- **Discord:** [https://phantomsnetwork.org/discord](https://phantomsnetwork.org/discord) — contact a PhantomsNetwork administrator privately; do not post details in a public channel.

Please include:

- the affected TemsProtect version,
- the server platform and version (for example Paper 26.3 build 262),
- the smallest reproduction you can share,
- the impact you observed (data exposure, permission bypass, crash, etc.),
- and any suggested fix.

Do not include real player data, database credentials, tokens, IP addresses or production addresses. Use a test server and dummy data where possible.

## What to expect

- Acknowledgment of the report as soon as practical.
- Private triage and a fix in a future release.
- Coordinated disclosure only after a fix is available. Please do not publish details before then.
- No bug bounty is offered.

## Scope

TemsProtect is a Paper server plugin. Relevant boundaries include:

- command and permission checks,
- lookup, rollback, restore and purge authorization,
- database connection, identifier and query handling,
- configuration, language and data-folder file handling,
- plugin-message networking (`coreprotect:handshake` / `coreprotect:data`),
- the optional GitHub update check.

The following are out of scope:

- vulnerabilities in Paper, the JDK, MySQL, ClickHouse, DuckDB or other third-party software (report those upstream),
- issues that require an already-compromised server console, filesystem or database,
- denial of service caused by an administrator granting destructive commands,
- features intentionally enabled by configuration (for example, granting `/temsprotect give`),
- support requests, configuration help or feature requests.
