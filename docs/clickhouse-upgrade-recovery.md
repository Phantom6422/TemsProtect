# Resume an Interrupted ClickHouse Upgrade

Normally, restart the installation that began the upgrade using its original TemsProtect data directory. Preserve the `.clickhouse-writer` file in place. Moving the data directory, copying the file to another filesystem, or recreating the file can change the installation's upgrade owner. If the original installation is unavailable, a database administrator can transfer the existing upgrade claim to a replacement installation.

This procedure applies to TemsProtect's ClickHouse schema 4 to 5 upgrade. Use a build that supports the handoff on the replacement.

## Isolate the Database

1. Stop all installations using the ClickHouse database and table prefix, including any replacement. Disable automatic restarts. Revoke the previous writers' database access or isolate the database so only the DBA and the replacement can connect. Keep that exclusion in place through verification and any retries. An empty query list only confirms the database is idle at the time of the check.
2. Let already accepted queries, queued asynchronous inserts and mutations finish. Take a backup of the affected namespace, including the marker tables. Confirm the endpoint, database and table prefix from the replacement's configuration. Have one DBA perform the transfer and pause other changes to the namespace while it is in progress.
3. Enable `database-lock` on the replacement. Use the expected owner UUID from its startup ownership error. If you need to obtain that error, start only the replacement once. Stop the replacement before editing the database, and preserve its `.clickhouse-writer` file in place for the rest of the handoff.

## Transfer the Claim

Read the existing table identities and marker comments. In the examples, replace `your_database` and `co_` with the configured database and prefix. TemsProtect uses `patch_25_1_lock`.

```sql
SELECT name, toString(uuid) AS table_uuid, comment
FROM system.tables
WHERE database = 'your_database'
  AND name IN ('co_event_data', 'co_patch_25_1_lock');
```

The upgrade marker's comment must have the platform tag below, followed by the current claim owner, the original `event_data` table UUID and, after a previous handoff, the gate owner. Confirm the source UUID still matches `event_data`. Use the recorded gate owner when present. For the first handoff, use the current claim owner as the gate owner. This keeps the readiness record attached to the original backfill across repeated transfers.

Immediately before each change, re-read the affected table identities and comments. Stop if the upgrade marker is missing, malformed or changed, or if the source UUID differs. Modify the comment in place, preserving the marker's UUID. Substitute the replacement's expected owner and the two recorded UUIDs in the example for your platform. [ClickHouse table comment syntax](https://clickhouse.com/docs/reference/statements/alter/comment)

**TemsProtect**

```sql
ALTER TABLE `your_database`.`co_patch_25_1_lock`
    MODIFY COMMENT 'coreprotect-25.1:NEW_OWNER_UUID:ORIGINAL_EVENT_UUID:GATE_OWNER_UUID';
```

Re-read the marker comments and UUIDs after each edit, and confirm the `event_data` table UUID is unchanged. The upgrade manages its own write constraint, schema version and lookup readiness rows.

## Verify the Upgrade

Start only the replacement. It must finish the upgrade and start successfully. Confirm that `event_data` has its original UUID, the upgrade marker is gone, and `storage_metadata.schema_version` reports 5. If a gate row for the recorded owner exists, confirm it reports `status = 1`.

```sql
SELECT status
FROM `your_database`.`co_event_data` FINAL
WHERE lookup_kind = 3 AND batch_id = toUUID('GATE_OWNER_UUID');
```

Run representative lookups against existing history before restoring access for other installations running a compatible version.

If startup fails, keep the old writers fenced. Inspect the startup error and re-read the source and markers before retrying.
