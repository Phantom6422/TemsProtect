# Welcome to TemsProtect

TemsProtect is a fast block-logging, rollback and anti-griefing plugin for Paper and Purpur 26.x. It is a rebranded, privately maintained fork of CoreProtect that keeps the original storage engine, API and command workflow.

- [Commands](commands.md)
- [Permissions](permissions.md)
- [Configuration](config.md)
- [Database migration](database-migration.md)
- [Automatic purging](auto-purge.md)
- [Language files](languages.md)
- [Integrations](tools-integrations.md)
- [API](api/index.md)
- [Networking API](api/networking.md)

## Quick start

```text
/temsprotect help
/temsprotect inspect
/temsprotect lookup u:<player> t:<time> r:<radius>
/temsprotect rollback u:<player> t:<time> r:<radius>
```

The short aliases `/tems`, `/co`, `/core` and `/coreprotect` are also available. Permissions use the `temsprotect.*` namespace, with `coreprotect.*` legacy aliases.

Source and releases: [github.com/Phantom6422/TemsProtect](https://github.com/Phantom6422/TemsProtect)
