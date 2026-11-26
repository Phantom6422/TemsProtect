# TemsProtect API

TemsProtect keeps the CoreProtect API package and class names (`net.coreprotect.CoreProtect`, `net.coreprotect.CoreProtectAPI`) so existing integrations continue to compile and run. The API version remains **13** (plugin 25.0-compatible); TemsProtect-specific additions are source-compatible.

## Getting the API

```java
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

CoreProtect plugin = (CoreProtect) Bukkit.getPluginManager().getPlugin("TemsProtect");
CoreProtectAPI api = plugin.getAPI();
```

Add the TemsProtect jar as a `provided` dependency in your build, or publish it to your internal Maven repository. TemsProtect is not published to Maven Central.

## Example lookup

```java
CoreProtectAPI.ParseResult result = api.parseResult("u:Notch t:1h r:10");
if (result != null) {
    for (CoreProtectAPI.ParseResult row : api.performLookup(result).getResults()) {
        // use row.getPlayer(), row.getType(), row.getCoordinates(), ...
    }
}
```

See [Commands](../commands.md) for lookup parameters and [Networking](networking.md) for the plugin-message protocol.

## Compatibility notes

- Event class: `net.coreprotect.event.CoreProtectPreLogEvent`
- Networking channels remain `coreprotect:handshake` and `coreprotect:data` so existing client mods keep working
- Database table names and the default embedded `co_` prefix are unchanged
