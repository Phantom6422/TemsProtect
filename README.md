# TemsProtect

TemsProtect is a fast block-logging, rollback, restore and anti-griefing plugin for **Paper 26.3** and the wider Paper/Purpur 26.x line. It is a native Paper plugin (`paper-plugin.yml`), so Paper lists it under **Paper Plugins** in `/plugins`. It keeps CoreProtect's proven storage engine and command set while adding a TemsProtect-branded command, permission namespace, configuration folder and an opt-in GitHub update check.

> A rebranded fork of [CoreProtect](https://github.com/PlayPro/CoreProtect) by Intelli and the CoreProtect contributors, maintained by [Phantom6422](https://github.com/Phantom6422) for [PhantomsNetwork](https://phantomsnetwork.org) internal use. The original project credit and the Artistic License 2.0 are retained.

## Install

1. Use **Java 25** (required by Paper 26.x) and a Paper or Purpur 26.1+ server.
2. Download the [latest release](https://github.com/Phantom6422/TemsProtect/releases/latest) and place `TemsProtect-<version>.jar` in `plugins/`.
3. Start the server. Paper loads TemsProtect as a **Paper plugin** and the plugin creates `plugins/TemsProtect/`.

If an existing `plugins/CoreProtect/` folder is present, it is moved to `plugins/TemsProtect/` on first start. Existing tables use the `co_` prefix and are opened as-is, so no database migration is required.

`/temsprotect` (aliases `/tems`, `/co`, `/core`, `/coreprotect`) is the main command. Existing `/co` workflows continue to work.

## Platforms

- Paper 26.3 (primary target, built against Paper API `26.3.build.262-stable`)
- Paper/Purpur 26.1 and 26.2
- Paper 26.x on Folia (best effort)
- Spigot, Bukkit and Fabric are not supported. Older Paper versions below 26.1 are not supported by this Paper plugin build.

## Commands

| Command | Description |
| --- | --- |
| `/temsprotect help` | Show the command list |
| `/temsprotect inspect` | Toggle the block inspector |
| `/temsprotect lookup <params>` | Look up block, container, item, chat, command, sign, session, username and entity data |
| `/temsprotect near` | Look up activity within a 5-block radius |
| `/temsprotect rollback <params>` | Roll back changes |
| `/temsprotect restore <params>` | Restore changes |
| `/temsprotect undo` | Reverse a rollback or restore |
| `/temsprotect apply` / `cancel` | Apply or cancel a preview |
| `/temsprotect purge <params>` | Delete old data |
| `/temsprotect status` | Show plugin, database and runtime status |
| `/temsprotect reload` | Reload the configuration |
| `/temsprotect consumer <pause|resume>` | Pause or resume database writes |
| `/temsprotect migrate-db <backend>` | Migrate between supported database backends (requires the optional migration extension) |
| `/temsprotect give <itemId>` | Give an item from a lookup result (permission required) |

## Permissions

The primary namespace is `temsprotect.*` (for example `temsprotect.rollback`, `temsprotect.lookup.container`, `temsprotect.migrate`). Legacy `coreprotect.*` nodes are still checked and are aliased in `plugin.yml`, so existing LuckPerms groups keep working. See [docs/permissions.md](docs/permissions.md).

## Configuration and data

- Data folder: `plugins/TemsProtect/`
- Default database: embedded DuckDB (`database.duckdb`)
- Optional backends: SQLite, MySQL, ClickHouse
- Table prefix: `co_` for embedded databases (kept for compatibility); configurable for MySQL/ClickHouse
- Language file: `plugins/TemsProtect/language.yml`

## Privacy

TemsProtect does **not** use bStats, donation keys, licence servers, crash-report uploads or remote language translation. The only optional network request is a read-only GitHub release check, disabled by default (`check-updates: false`). See `config.yml` for `update-checker-repository`.

## Build

```bash
./gradlew clean build
```

Requires JDK 25. The shaded jar is written to `build/libs/TemsProtect-<version>.jar`. Run the unit tests with `./gradlew test`.

## Credits

- Intelli and the [CoreProtect](https://github.com/PlayPro/CoreProtect) contributors — the original project

## License

Artistic License 2.0. See [LICENSE](LICENSE).
