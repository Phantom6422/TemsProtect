# Security Policy

## Reporting a vulnerability

Please do **not** open a public issue for a security problem.

Report it privately to the repository owner (`Phantom6422`) through GitHub's
private vulnerability reporting, or by direct message on GitHub. Include:

- the affected TemsProtect version,
- the server platform and version,
- the smallest reproduction you can share,
- the impact you observed (data exposure, permission bypass, crash, etc.),
- and any suggested fix.

Do not include real player data, database credentials, tokens or production
addresses. Use a test server and dummy data where possible.

## Scope

TemsProtect is a Paper server plugin. Relevant boundaries include:

- command and permission checks,
- lookup/rollback/purge authorization,
- database connection and query handling,
- configuration and data-folder file handling,
- plugin-message networking,
- the optional GitHub update check.

The following are out of scope:

- vulnerabilities in Paper, the JDK, MySQL, ClickHouse, DuckDB or other
  third-party software (report those upstream),
- issues that require an already-compromised server console or filesystem,
- denial of service caused by an administrator granting destructive commands,
- features intentionally enabled by configuration (for example, a public
  `/temsprotect give` permission).
