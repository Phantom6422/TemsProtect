# Permissions

TemsProtect uses the `temsprotect.*` namespace. Every permission has a legacy `coreprotect.*` alias declared in `plugin.yml`, so existing permission groups continue to work after the rebrand.

## Base permissions

| Permission | Default | Description |
| --- | --- | --- |
| `temsprotect.*` | op | Grants every TemsProtect permission below |
| `temsprotect.co` | true | Access to `/temsprotect`, `/tems` and `/co` |
| `temsprotect.inspect` | op | Toggle and use the inspector |
| `temsprotect.lookup` | op | Use `/temsprotect lookup` |
| `temsprotect.rollback` | op | Perform rollbacks |
| `temsprotect.restore` | op | Perform restores |
| `temsprotect.teleport` | op | Teleport to lookup coordinates |
| `temsprotect.help` | op | View command help |
| `temsprotect.purge` | op | Delete old data |
| `temsprotect.reload` | op | Reload the configuration |
| `temsprotect.status` | op | View plugin, database and runtime status |
| `temsprotect.consumer` | op | Pause or resume the database writer |
| `temsprotect.migrate` | op | Migrate between database backends |
| `temsprotect.networking` | op | Use the plugin-message networking API |
| `temsprotect.give` | false | Give lookup items with `/temsprotect give` |

## Lookup permissions

These can be granted or negated individually to limit what a lookup may return.

| Permission | Default | Restricts |
| --- | --- | --- |
| `temsprotect.lookup.near` | op | `/temsprotect near` |
| `temsprotect.lookup.block` | op | Block data |
| `temsprotect.lookup.chat` | op | Chat messages |
| `temsprotect.lookup.command` | op | Player commands |
| `temsprotect.lookup.click` | op | Player interactions |
| `temsprotect.lookup.container` | op | Container transactions |
| `temsprotect.lookup.inventory` | op | Player inventory transactions |
| `temsprotect.lookup.item` | op | Item transactions |
| `temsprotect.lookup.kill` | op | Entity kills |
| `temsprotect.lookup.spawn` | op | Entity spawns |
| `temsprotect.lookup.sign` | op | Sign text |
| `temsprotect.lookup.session` | op | Player sessions |
| `temsprotect.lookup.username` | op | Username changes |

## Legacy aliases

The following are retained for compatibility:

- `coreprotect.*` grants `temsprotect.*`
- `coreprotect.co` grants `temsprotect.co`
- `coreprotect.lookup.*`, `coreprotect.rollback`, `coreprotect.restore`, `coreprotect.inspect`, `coreprotect.purge`, `coreprotect.teleport`, `coreprotect.help`, `coreprotect.status`, `coreprotect.reload`, `coreprotect.consumer`, `coreprotect.networking`, `coreprotect.migrate` and `coreprotect.give` map to their `temsprotect` equivalents
- `coreprotect.core` and `coreprotect.coreprotect` gate the legacy `/core` and `/coreprotect` commands

## Command permissions

`/temsprotect`, `/tems` and `/co` all use `temsprotect.co`. The long legacy commands remain opt-in through `coreprotect.core` and `coreprotect.coreprotect`.
