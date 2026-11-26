26.3.3 - Tab completion fix

- Normalize Paper's empty suggestion arguments so first-level suggestions appear after `/temsprotect `
- Allow tab completion for console senders as well as players
- Add regression tests for first-level and help-topic suggestions

26.3.2 - Native Paper plugin

- Replace `plugin.yml` with `paper-plugin.yml`; Paper now lists TemsProtect under **Paper Plugins** in `/plugins`
- Register `/temsprotect`, `/tems`, `/co`, `/core` and `/coreprotect` through Paper's command API
- Move DuckDB library loading to a Paper `PluginLoader` using Paper's Maven mirror
- Declare WorldEdit, FastAsyncWorldEdit and AdvancedChests as optional Paper plugin dependencies
- Remove the `plugin.yml` branch resource; use the release branch internally
- Update README and platform requirements for the Paper plugin format

26.3.1 - Packaging and CI hardening

- Exclude duplicate SLF4J and JSpecify classes from the shaded jar; Paper provides them at runtime
- Remove unused test dependencies from the build
- Add a Dependabot cooldown and manual zizmor runs; zizmor now passes
- Keep the plugin version at 26.3.1 while the internal database schema version remains 2.26.3

26.3 - TemsProtect rebrand and Paper 26.3

- Convert the build from Maven to Gradle (Kotlin DSL, version catalog, wrapper, Java 25 toolchain)
- Rebrand the plugin, command, permissions, configuration folder, messages, documentation and release artifacts to TemsProtect
- Compile against Paper API 26.3.build.262-stable and add the Paper 26.3 adapter
- Track 26.3 shelf mushrooms, red shrubs, straw beds and cushion entities
- Keep `net.coreprotect` packages, the `CoreProtectAPI` class and the `co_` table prefix for plugin and database compatibility
- Add `/temsprotect` and `/tems` commands; keep `/co`, `/core` and `/coreprotect`
- Add `temsprotect.*` permissions with automatic `coreprotect.*` legacy aliases
- Migrate an existing `plugins/CoreProtect` data folder on first start
- Remove bStats, donation-key licence checks, statistics uploads, remote error reporting and remote language translation
- Replace the CoreProtect update endpoints with an opt-in, read-only GitHub release check (disabled by default)
- Make update-alert and status threads daemon threads
- Require Java 25 in the startup version check
- Add JUnit 5 tests for version comparison and 26.x adapter version mapping
- Add GitHub Actions build, test, Paper smoke test and tag-release workflows
- Keep the Artistic License 2.0 and upstream credits
