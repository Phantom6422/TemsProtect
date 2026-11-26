# Contributing

Thanks for helping improve TemsProtect.

## Before you start

- TemsProtect is a privately maintained fork of [CoreProtect](https://github.com/PlayPro/CoreProtect). Keep the upstream credit and the Artistic License 2.0 intact.
- The internal Java packages (`net.coreprotect.*`), `CoreProtectAPI`, the `CoreProtectPreLogEvent` class, the `coreprotect:*` plugin channels and the `co_` table prefix are compatibility surfaces. Do not rename them without a migration plan.
- User-facing names belong to TemsProtect: plugin name, commands, `temsprotect.*` permissions, `plugins/TemsProtect`, messages, documentation and release artifacts.

## Build and test

```bash
./gradlew clean build
```

Requires JDK 25. The shaded jar is written to `build/libs/TemsProtect-<version>.jar`.

## Pull requests

- Keep changes focused and explain the user-visible effect.
- Add or update a unit test when touching parsing, permissions, adapters or configuration.
- Do not include database files, server logs, tokens, credentials or player data.
- Run `./gradlew build` before opening the pull request.
- Never commit `AGENT.md`; it is a local-only file and is ignored by Git.

## Reporting security issues

Do not open a public issue for a security problem. Contact the maintainer directly through the repository owner account.
