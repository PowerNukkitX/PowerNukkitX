package org.powernukkitx.migration.executor;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.Server;
import org.powernukkitx.ServerDBStorageFormat;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationService;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.PlayerMigrationData;
import org.powernukkitx.migration.steps.PositionTrackingV3_1_0Migration;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Orchestrates legacy and canonical player storage migrations into the server-global player database.
 *
 * @author Curse
 */
@Slf4j
public final class PlayerMigrationExecutor {
    private static final byte[] LEGACY_PNX_EXTRA_PREFIX = "\0pnx_player_extra\0".getBytes(StandardCharsets.UTF_8);
    private static final byte[] LEGACY_CUSTOM_PREFIX = "\0pnx_player_custom\0".getBytes(StandardCharsets.UTF_8);

    private PlayerMigrationExecutor() {
    }

    /**
     * Migrates legacy or canonical player records to the latest registered player storage version.
     */
    public static void migrate(DB sourceDB, DB targetDB, BiConsumer<UUID, CompoundTag> uniqueIdAssigner, MigrationService migrationService)
            throws IOException {
        MigrationVersion currentVersion = readMigrationVersion(targetDB, migrationService);
        MigrationVersion targetVersion = migrationService.getLatestVersion(MigrationFormat.PLAYER);

        if (currentVersion != null && currentVersion.compareTo(targetVersion) < 0) {
            migrateCanonical(targetDB, uniqueIdAssigner, migrationService, currentVersion, targetVersion);
            currentVersion = targetVersion;
        }

        if (sourceDB != null) {
            migrateLegacy(sourceDB, targetDB, uniqueIdAssigner, migrationService, targetVersion);
            return;
        }

        if (currentVersion == null) {
            targetDB.put(
                    ServerDBStorageFormat.PLAYERDB_STORAGE_VERSION_KEY,
                    targetVersion.toString().getBytes(StandardCharsets.UTF_8)
            );
        }
    }

    /**
     * Migrates native BDS player data into the existing server-global PNX player storage.
     */
    public static Map<Long, Long> migrateBdsWorldPlayers(LevelDBStorage storage, DB targetDB, BiConsumer<UUID, CompoundTag> uniqueIdAssigner) throws IOException {
        DB sourceDB = storage.getDb();
        Map<String, UUID> players = new TreeMap<>();

        try (DBIterator iterator = sourceDB.iterator()) {
            iterator.seekToFirst();

            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                String key = new String(entry.getKey(), StandardCharsets.UTF_8);

                if (!isBdsPlayerIdentityKey(key) || entry.getValue().length == 0) {
                    continue;
                }

                CompoundTag identity = readBdsCompound(entry.getValue());
                String msaId = identity.getString("MsaId");
                String serverId = identity.getString("ServerId");

                if (msaId.isEmpty() || !serverId.startsWith(LevelDBKeyUtil.PLAYER_SERVER_PREFIX)) {
                    continue;
                }

                UUID uuid;
                try {
                    uuid = UUID.fromString(msaId);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid BDS player MsaId " + msaId, e);
                }

                UUID previous = players.putIfAbsent(serverId, uuid);

                if (previous != null && !previous.equals(uuid)) {
                    throw new IOException("BDS player storage " + serverId + " is mapped to multiple MsaIds");
                }
            }
        }

        Map<Long, Long> uniqueIdRemap = new HashMap<>();
        int imported = 0;
        int preserved = 0;

        try (WriteBatch batch = targetDB.createWriteBatch()) {
            for (Map.Entry<String, UUID> entry : players.entrySet()) {
                byte[] value = sourceDB.get(entry.getKey().getBytes(StandardCharsets.UTF_8));

                if (value == null || value.length == 0) {
                    continue;
                }

                UUID uuid = entry.getValue();
                CompoundTag player = readBdsCompound(value);
                long oldUniqueId = player.getLong("UniqueID");
                byte[] existingValue = targetDB.get(playerKey(uuid));
                long newUniqueId;

                if (existingValue != null) {
                    CompoundTag existing = readPlayer(existingValue);
                    uniqueIdAssigner.accept(uuid, existing);
                    newUniqueId = existing.getLong("UniqueID");
                    preserved++;
                } else {
                    resetImportedPlayerLocation(player);
                    player.remove("UniqueID");
                    uniqueIdAssigner.accept(uuid, player);
                    newUniqueId = player.getLong("UniqueID");
                    batch.put(playerKey(uuid), writePlayer(player));
                    imported++;
                }

                if (oldUniqueId != 0) {
                    Long previous = uniqueIdRemap.putIfAbsent(oldUniqueId, newUniqueId);

                    if (previous != null && previous != newUniqueId) {
                        throw new IOException("BDS player UniqueID " + oldUniqueId + " maps to multiple PNX UniqueIDs");
                    }
                }
            }

            targetDB.write(batch);
        }

        log.info("[PlayerData Migration] Imported {} BDS players and preserved {} existing PNX players", imported, preserved);

        return uniqueIdRemap;
    }

    private static void migrateLegacy(
            DB sourceDB,
            DB targetDB,
            BiConsumer<UUID, CompoundTag> uniqueIdAssigner,
            MigrationService migrationService,
            MigrationVersion targetVersion
    ) throws IOException {
        String dynamicPropertiesNamespace = Server.getDefaultDynamicPropertiesGroupUUID();
        boolean remapPositionTracking = migrationService.getContext().hasLegacyPositionTrackingRemap();
        int positionSourceLastId = migrationService.getContext().getLegacyPositionTrackingSourceLastId();
        int positionOffset = migrationService.getContext().getLegacyPositionTrackingOffset();

        int migratedCount = 0;
        int preservedCount = 0;
        int pnxExtraCount = 0;
        int customCount = 0;
        int nameCount = 0;

        try (DBIterator iterator = sourceDB.iterator();
             WriteBatch batch = targetDB.createWriteBatch()) {
            iterator.seekToFirst();

            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                byte[] key = entry.getKey();
                byte[] value = entry.getValue();

                if (isPlayerRecord(key, value)) {
                    UUID uuid = readUuid(key);
                    CompoundTag source = readPlayer(value);
                    long oldUniqueId = source.getLong("UniqueID");
                    byte[] targetPlayerKey = playerKey(uuid);
                    byte[] existingValue = targetDB.get(targetPlayerKey);

                    if (existingValue != null) {
                        long newUniqueId = readPlayer(existingValue).getLong("UniqueID");

                        if (newUniqueId == 0) {
                            throw new IOException("Canonical player " + uuid + " has invalid ActorUniqueID 0");
                        }

                        if (oldUniqueId != 0) {
                            Long previous = migrationService.getContext().getMigratedLegacyPlayerUniqueIds().putIfAbsent(oldUniqueId, newUniqueId);
                            if (previous != null && previous != newUniqueId) {
                                throw new IOException("Legacy PNX player UniqueID " + oldUniqueId + " maps to multiple canonical UniqueIDs");
                            }
                        }

                        preservedCount++;
                        continue;
                    }

                    source.remove("UniqueID");

                    byte[] pnxExtraValue = sourceDB.get(legacySidecar(LEGACY_PNX_EXTRA_PREFIX, uuid));
                    CompoundTag sourcePnxExtra = pnxExtraValue != null
                            ? readPlayer(pnxExtraValue)
                            : new CompoundTag();

                    byte[] customValue = sourceDB.get(legacySidecar(LEGACY_CUSTOM_PREFIX, uuid));
                    CompoundTag custom = customValue != null
                            ? readPlayer(customValue)
                            : new CompoundTag();

                    if (remapPositionTracking) {
                        PositionTrackingV3_1_0Migration.remapLegacyPnxItems(source, positionSourceLastId, positionOffset);
                        PositionTrackingV3_1_0Migration.remapLegacyPnxItems(sourcePnxExtra, positionSourceLastId, positionOffset);
                        PositionTrackingV3_1_0Migration.remapLegacyPnxItems(custom, positionSourceLastId, positionOffset);
                    }

                    DynamicPropertiesMigrationExecutor.normalizeNbt(source, dynamicPropertiesNamespace);
                    DynamicPropertiesMigrationExecutor.normalizeNbt(sourcePnxExtra, dynamicPropertiesNamespace);
                    DynamicPropertiesMigrationExecutor.normalizeNbt(custom, dynamicPropertiesNamespace);

                    PlayerMigrationData migrated = migrationService.apply(
                            MigrationFormat.PLAYER,
                            null,
                            new PlayerMigrationData(source, sourcePnxExtra)
                    );

                    uniqueIdAssigner.accept(uuid, migrated.player());
                    long newUniqueId = migrated.player().getLong("UniqueID");

                    if (oldUniqueId != 0) {
                        Long previous = migrationService.getContext().getMigratedLegacyPlayerUniqueIds().putIfAbsent(oldUniqueId, newUniqueId);
                        if (previous != null && previous != newUniqueId) {
                            throw new IOException("Legacy PNX player UniqueID " + oldUniqueId + " maps to multiple canonical UniqueIDs");
                        }
                    }

                    batch.put(targetPlayerKey, writePlayer(migrated.player()));

                    if (!migrated.pnxExtra().isEmpty()) {
                        batch.put(pnxExtraKey(uuid), writePlayer(migrated.pnxExtra()));
                        pnxExtraCount++;
                    }

                    if (!custom.isEmpty()) {
                        batch.put(customKey(uuid), writePlayer(custom));
                        customCount++;
                    }

                    migratedCount++;
                    continue;
                }

                if (isLegacyNameRecord(key, value)) {
                    UUID uuid = readUuid(value);

                    if (targetDB.get(playerKey(uuid)) != null) continue;

                    byte[] targetNameKey = nameKey(new String(key, StandardCharsets.UTF_8));
                    if (targetDB.get(targetNameKey) == null) {
                        batch.put(targetNameKey, value);
                        nameCount++;
                    }
                }
            }

            batch.put(
                    ServerDBStorageFormat.PLAYERDB_STORAGE_VERSION_KEY,
                    targetVersion.toString().getBytes(StandardCharsets.UTF_8)
            );
            targetDB.write(batch);
        }

        log.info(
                "[PlayerData Migration] Imported {} legacy PNX players, preserved {} existing players, {} pnx_extra, {} custom and {} names",
                migratedCount,
                preservedCount,
                pnxExtraCount,
                customCount,
                nameCount
        );
    }

    private static void migrateCanonical(
            DB targetDB,
            BiConsumer<UUID, CompoundTag> uniqueIdAssigner,
            MigrationService migrationService,
            MigrationVersion currentVersion,
            MigrationVersion targetVersion
    ) throws IOException {
        int migratedCount = 0;

        try (DBIterator iterator = targetDB.iterator();
             WriteBatch batch = targetDB.createWriteBatch()) {
            iterator.seekToFirst();

            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                byte[] key = entry.getKey();
                byte[] value = entry.getValue();

                if (!isCanonicalPlayerRecord(key, value)) {
                    continue;
                }

                UUID uuid = readUuid(key, ServerDBStorageFormat.SERVER_DATA_PLAYER_PREFIX.length);
                CompoundTag source = readPlayer(value);

                byte[] pnxExtraValue = targetDB.get(pnxExtraKey(uuid));
                CompoundTag sourcePnxExtra = pnxExtraValue != null
                        ? readPlayer(pnxExtraValue)
                        : new CompoundTag();

                uniqueIdAssigner.accept(uuid, source);

                PlayerMigrationData migrated = migrationService.apply(
                        MigrationFormat.PLAYER,
                        currentVersion,
                        new PlayerMigrationData(source, sourcePnxExtra)
                );

                batch.put(playerKey(uuid), writePlayer(migrated.player()));

                if (migrated.pnxExtra().isEmpty()) {
                    batch.delete(pnxExtraKey(uuid));
                } else {
                    batch.put(pnxExtraKey(uuid), writePlayer(migrated.pnxExtra()));
                }

                migratedCount++;
            }

            batch.put(
                    ServerDBStorageFormat.PLAYERDB_STORAGE_VERSION_KEY,
                    targetVersion.toString().getBytes(StandardCharsets.UTF_8)
            );
            targetDB.write(batch);
        }

        log.info(
                "[PlayerData Migration] Migrated {} canonical player records from {} to {}",
                migratedCount,
                currentVersion,
                targetVersion
        );
    }

    /**
     * Returns whether the world still contains any BDS-owned player storage.
     */
    public static boolean hasBdsWorldPlayerStorage(DB sourceDB) throws IOException {
        if (sourceDB.get(LevelDBKeyUtil.LOCAL_PLAYER_KEY.getBytes(StandardCharsets.UTF_8)) != null) {
            return true;
        }

        try (DBIterator iterator = sourceDB.iterator()) {
            return hasKeyPrefix(iterator, LevelDBKeyUtil.PLAYER_PREFIX)
                    || hasKeyPrefix(iterator, LevelDBKeyUtil.LEGACY_CONSOLE_PLAYER_PREFIX);
        }
    }

    /**
     * Removes every BDS player-storage record after the canonical player data has been imported.
     */
    public static void removeBdsWorldPlayerStorage(LevelDBStorage storage) throws IOException {
        DB sourceDB = storage.getDb();
        int removed = 0;

        try (DBIterator iterator = sourceDB.iterator();
             WriteBatch batch = storage.createBatch()) {
            iterator.seekToFirst();

            while (iterator.hasNext()) {
                byte[] key = iterator.next().getKey();
                String name = new String(key, StandardCharsets.UTF_8);

                if (!isBdsPlayerStorageKey(name)) {
                    continue;
                }

                batch.delete(key);
                removed++;
            }

            if (removed != 0) {
                storage.writeBatch(batch);
            }
        }

        if (removed != 0) {
            log.info("[PlayerData Migration] Removed {} BDS player storage records", removed);
        }
    }

    private static boolean hasKeyPrefix(DBIterator iterator, String prefix) {
        iterator.seek(prefix.getBytes(StandardCharsets.UTF_8));

        if (!iterator.hasNext()) {
            return false;
        }

        return new String(iterator.next().getKey(), StandardCharsets.UTF_8).startsWith(prefix);
    }

    private static boolean isBdsPlayerStorageKey(String key) {
        return key.equals(LevelDBKeyUtil.LOCAL_PLAYER_KEY)
                || key.startsWith(LevelDBKeyUtil.PLAYER_PREFIX)
                || key.startsWith(LevelDBKeyUtil.LEGACY_CONSOLE_PLAYER_PREFIX);
    }

    private static boolean isBdsPlayerIdentityKey(String key) {
        return key.startsWith(LevelDBKeyUtil.PLAYER_PREFIX)
                && !key.startsWith(LevelDBKeyUtil.PLAYER_SERVER_PREFIX);
    }

    private static void resetImportedPlayerLocation(CompoundTag player) {
        player.remove(
                "Pos",
                "Motion",
                "Rotation",
                "DimensionId",
                "SpawnX",
                "SpawnY",
                "SpawnZ",
                "SpawnDimension",
                "SpawnBlockPositionX",
                "SpawnBlockPositionY",
                "SpawnBlockPositionZ"
        );
    }

    private static CompoundTag readBdsCompound(byte[] value) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(value);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            Object root = nbtInputStream.readTag();

            if (!(root instanceof NbtMap compound)) {
                throw new IOException("BDS player storage does not contain a compound root");
            }

            return CompoundTag.fromNetwork(compound);
        }
    }

    private static MigrationVersion readMigrationVersion(DB targetDB, MigrationService migrationService) throws IOException {
        byte[] stored = targetDB.get(ServerDBStorageFormat.PLAYERDB_STORAGE_VERSION_KEY);
        if (stored == null) return null;
        return migrationService.parseStoredVersion(MigrationFormat.PLAYER, new String(stored, StandardCharsets.UTF_8));
    }

    private static boolean isPlayerRecord(byte[] key, byte[] value) {
        return key.length == 16 && value.length >= 2 && value[0] == 0x1f && value[1] == (byte) 0x8b;
    }

    private static boolean isCanonicalPlayerRecord(byte[] key, byte[] value) {
        byte[] prefix = ServerDBStorageFormat.SERVER_DATA_PLAYER_PREFIX;

        if (key.length != prefix.length + 16 ||
            value.length < 2 ||
            value[0] != 0x1f ||
            value[1] != (byte) 0x8b) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (key[i] != prefix[i]) return false;
        }

        return true;
    }

    private static boolean isLegacyNameRecord(byte[] key, byte[] value) {
        return key.length > 0 && key[0] != 0 && value.length == 16;
    }

    private static byte[] playerKey(UUID uuid) {
        return uuidKey(ServerDBStorageFormat.SERVER_DATA_PLAYER_PREFIX, uuid);
    }

    private static byte[] pnxExtraKey(UUID uuid) {
        return uuidKey(ServerDBStorageFormat.SERVER_DATA_PLAYER_PNX_EXTRA_PREFIX, uuid);
    }

    private static byte[] customKey(UUID uuid) {
        return uuidKey(ServerDBStorageFormat.SERVER_DATA_PLAYER_CUSTOM_PREFIX, uuid);
    }

    private static byte[] nameKey(String name) {
        byte[] value = name.toLowerCase(Locale.ENGLISH).getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(ServerDBStorageFormat.SERVER_DATA_PLAYER_NAME_PREFIX.length + value.length);
        buffer.put(ServerDBStorageFormat.SERVER_DATA_PLAYER_NAME_PREFIX);
        buffer.put(value);
        return buffer.array();
    }

    private static byte[] uuidKey(byte[] prefix, UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(prefix.length + 16);
        buffer.put(prefix);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    private static byte[] legacySidecar(byte[] prefix, UUID uuid) {
        return uuidKey(prefix, uuid);
    }

    private static UUID readUuid(byte[] key) {
        return readUuid(key, 0);
    }

    private static UUID readUuid(byte[] key, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(key, offset, 16);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    private static CompoundTag readPlayer(byte[] value) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(value);
             NBTInputStream nbtInputStream = NbtUtils.createGZIPReader(inputStream)) {
            return CompoundTag.fromNetwork((NbtMap) nbtInputStream.readTag());
        }
    }

    private static byte[] writePlayer(CompoundTag nbt) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createGZIPWriter(outputStream)) {
            nbtOutputStream.writeTag(nbt.toNetwork());
            nbtOutputStream.close();
            return outputStream.toByteArray();
        }
    }
}
