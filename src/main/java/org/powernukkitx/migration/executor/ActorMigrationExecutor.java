package org.powernukkitx.migration.executor;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.format.leveldb.LevelDBActorStorage;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.leveldb.LevelDBMigrationChunkSerializer;
import org.powernukkitx.migration.MigrationService;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.steps.LevelDBActorV3_1_0Migration;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Orchestrates actor migrations, relationship reconstruction and canonical LevelDB actor storage reconciliation.
 *
 * @author Curse
 */
@Slf4j
public final class ActorMigrationExecutor {
    private static final byte[] ACTOR_PREFIX = "actorprefix".getBytes(StandardCharsets.UTF_8);
    private static final byte[] DIGEST_PREFIX = "digp".getBytes(StandardCharsets.UTF_8);

    private ActorMigrationExecutor() {
    }

    /**
     * Executes pending actor migrations for one stored chunk.
     */
    public static void migrate(
            DB db,
            WriteBatch batch,
            int chunkX,
            int chunkZ,
            DimensionData dimensionData,
            Map<LevelDBActorV3_1_0Migration.CreakingHeartPosition, Long> migratedCreakingLinks,
            Map<UUID, Long> migratedActorUniqueIdsByUuid,
            List<LevelDBActorV3_1_0Migration.LegacyRidingLink> migratedRidingLinks,
            MigrationService migrationService,
            MigrationVersion currentVersion
    ) throws IOException {
        if (migrationService.getStepsAfter(MigrationFormat.ACTOR, currentVersion).size() == 0) {
            return;
        }

        byte[] digestKey = LevelDBActorStorage.getDigestKey(chunkX, chunkZ, dimensionData);
        byte[] legacyEntityKey = LevelDBKeyUtil.ENTITIES.getKey(chunkX, chunkZ, dimensionData);
        List<Long> actorStorageKeys = new ArrayList<>();
        Set<Long> uniqueActorStorageKeys = new HashSet<>();

        byte[] existingDigest = db.get(digestKey);
        if (existingDigest != null) {
            for (long actorStorageKey : LevelDBActorStorage.readDigest(existingDigest)) {
                byte[] actorBytes = db.get(LevelDBActorStorage.getActorKey(actorStorageKey));
                if (actorBytes == null) continue;

                List<CompoundTag> actorTags = LevelDBMigrationChunkSerializer.readLittleEndianCompounds(actorBytes);
                if (actorTags.size() != 1) {
                    throw new IOException(
                            "Invalid actorprefix record while migrating chunk ["
                                    + chunkX
                                    + ","
                                    + chunkZ
                                    + "] for storage key "
                                    + Long.toUnsignedString(actorStorageKey));
                }

                CompoundTag tag = actorTags.get(0);
                long expectedUniqueId = LevelDBActorStorage.getActorUniqueId(actorStorageKey);
                if (tag.contains("UniqueID")) {
                    long storedUniqueId = tag.getLong("UniqueID");
                    if (storedUniqueId != 0 && storedUniqueId != expectedUniqueId) {
                        throw new IOException(
                                "Actor UniqueID does not match actorprefix storage key in chunk ["
                                        + chunkX
                                        + ","
                                        + chunkZ
                                        + "]: stored="
                                        + storedUniqueId
                                        + ", expected="
                                        + expectedUniqueId);
                    }
                }

                if (uniqueActorStorageKeys.add(actorStorageKey)) {
                    actorStorageKeys.add(actorStorageKey);
                }
            }
        }

        byte[] legacyEntities = db.get(legacyEntityKey);
        if (legacyEntities != null && legacyEntities.length > 0) {
            List<CompoundTag> entityTags = LevelDBMigrationChunkSerializer.readLittleEndianCompounds(legacyEntities);

            for (CompoundTag tag : entityTags) {
                long actorUniqueId = migrationService.getContext().getServer().getNewActorUniqueId();

                LevelDBActorV3_1_0Migration.Data migrated = migrationService.apply(
                        MigrationFormat.ACTOR,
                        currentVersion,
                        new LevelDBActorV3_1_0Migration.Data(
                                tag,
                                dimensionData.getDimensionId(),
                                actorUniqueId,
                                migratedActorUniqueIdsByUuid,
                                migratedRidingLinks,
                                migratedCreakingLinks,
                                true
                        )
                );

                if (!migrated.keep()) continue;

                long actorStorageKey = LevelDBActorStorage.getActorStorageKey(actorUniqueId);
                boolean newlyAdded = uniqueActorStorageKeys.add(actorStorageKey);

                batch.put(
                        LevelDBActorStorage.getActorKey(actorStorageKey),
                        LevelDBMigrationChunkSerializer.writeLittleEndianCompound(migrated.actor())
                );

                if (newlyAdded) {
                    actorStorageKeys.add(actorStorageKey);
                }
            }
        }

        batch.put(digestKey, LevelDBActorStorage.writeDigest(actorStorageKeys));
        batch.put(LevelDBKeyUtil.ACTOR_DIGEST_VERSION.getKey(chunkX, chunkZ, dimensionData), new byte[]{0});
        batch.delete(legacyEntityKey);
    }

    /**
     * Rebuilds canonical actor riding links collected during migration.
     */
    public static void reconcileRidingLinks(
            LevelDBStorage storage,
            Map<UUID, Long> migratedActorUniqueIdsByUuid,
            List<LevelDBActorV3_1_0Migration.LegacyRidingLink> migratedRidingLinks
    ) throws IOException {
        if (migratedRidingLinks.size() == 0) return;

        DB db = storage.getDb();
        Map<Long, Set<Long>> ridersByVehicle = new HashMap<>();

        for (LevelDBActorV3_1_0Migration.LegacyRidingLink legacyLink : migratedRidingLinks) {
            Long vehicleUniqueId = migratedActorUniqueIdsByUuid.get(legacyLink.vehicleUuid());
            if (vehicleUniqueId == null) {
                log.warn("[LevelDB Migration] Dropping unresolved legacy RidingUUID {} for rider {}", legacyLink.vehicleUuid(), legacyLink.riderUniqueId());
                continue;
            }

            if (vehicleUniqueId == legacyLink.riderUniqueId()) {
                log.warn("[LevelDB Migration] Dropping self-referencing legacy RidingUUID for actor {}", legacyLink.riderUniqueId());
                continue;
            }

            ridersByVehicle.computeIfAbsent(vehicleUniqueId, ignored -> new HashSet<>()).add(legacyLink.riderUniqueId());
        }

        if (ridersByVehicle.size() == 0) return;

        try (WriteBatch batch = storage.createBatch()) {
            boolean changed = false;

            for (Map.Entry<Long, Set<Long>> entry : ridersByVehicle.entrySet()) {
                long vehicleUniqueId = entry.getKey();
                long actorStorageKey = LevelDBActorStorage.getActorStorageKey(vehicleUniqueId);
                byte[] actorBytes = db.get(LevelDBActorStorage.getActorKey(actorStorageKey));

                if (actorBytes == null) {
                    log.warn("[LevelDB Migration] Missing vehicle actor {} while converting RidingUUID", vehicleUniqueId);
                    continue;
                }

                List<CompoundTag> actorTags = LevelDBMigrationChunkSerializer.readLittleEndianCompounds(actorBytes);
                if (actorTags.size() != 1) {
                    throw new IOException("Invalid actorprefix record while converting RidingUUID for vehicle " + vehicleUniqueId);
                }

                CompoundTag vehicleTag = actorTags.get(0);

                if (vehicleTag.contains(Entity.NBT_LINKS_TAG)) {
                    if (!vehicleTag.containsList(Entity.NBT_LINKS_TAG)) {
                        throw new IOException("Vehicle " + vehicleUniqueId + " has invalid canonical LinksTag type");
                    }

                    continue;
                }

                List<Long> riderUniqueIds = new ArrayList<>(entry.getValue());
                riderUniqueIds.sort(Long::compare);
                ListTag<CompoundTag> links = new ListTag<>();

                for (int linkId = 0; linkId < riderUniqueIds.size(); linkId++) {
                    links.add(
                            new CompoundTag()
                                    .putLong(Entity.NBT_LINK_ENTITY_ID, riderUniqueIds.get(linkId))
                                    .putInt(Entity.NBT_LINK_ID, linkId)
                    );
                }

                if (links.size() == 0) continue;

                vehicleTag.putList(Entity.NBT_LINKS_TAG, links);
                batch.put(
                        LevelDBActorStorage.getActorKey(actorStorageKey),
                        LevelDBMigrationChunkSerializer.writeLittleEndianCompound(vehicleTag)
                );
                changed = true;
            }

            if (changed) {
                storage.writeBatch(batch);
            }
        }
    }

    /**
     * Reconciles migrated Creaking actors with their persisted Creaking Heart Block Entities.
     */
    public static void reconcileCreakingHeartLinks(
            LevelDBStorage storage,
            DimensionData dimensionData,
            Map<LevelDBActorV3_1_0Migration.CreakingHeartPosition, Long> links
    ) throws IOException {
        if (links.size() == 0) return;

        DB db = storage.getDb();
        Map<Long, List<LevelDBActorV3_1_0Migration.CreakingHeartPosition>> byChunk = new HashMap<>();

        for (LevelDBActorV3_1_0Migration.CreakingHeartPosition position : links.keySet()) {
            int chunkX = position.x() >> 4;
            int chunkZ = position.z() >> 4;
            byChunk.computeIfAbsent(getChunkKey(chunkX, chunkZ), ignored -> new ArrayList<>()).add(position);
        }

        try (WriteBatch batch = storage.createBatch()) {
            boolean changed = false;

            for (List<LevelDBActorV3_1_0Migration.CreakingHeartPosition> positions : byChunk.values()) {
                LevelDBActorV3_1_0Migration.CreakingHeartPosition first = positions.getFirst();
                int chunkX = first.x() >> 4;
                int chunkZ = first.z() >> 4;

                if (db.get(LevelDBKeyUtil.PNX_EXTRA_DATA.getKey(chunkX, chunkZ, dimensionData)) == null) {
                    continue;
                }

                byte[] key = LevelDBKeyUtil.BLOCK_ENTITIES.getKey(chunkX, chunkZ, dimensionData);
                byte[] data = db.get(key);
                if (data == null || data.length == 0) continue;

                List<CompoundTag> blockEntities = LevelDBMigrationChunkSerializer.readLittleEndianCompounds(data);
                boolean chunkChanged = false;

                for (CompoundTag blockEntity : blockEntities) {
                    if (!"CreakingHeart".equals(blockEntity.getString("id"))) continue;

                    LevelDBActorV3_1_0Migration.CreakingHeartPosition position =
                            new LevelDBActorV3_1_0Migration.CreakingHeartPosition(
                                    blockEntity.getInt("x"),
                                    blockEntity.getInt("y"),
                                    blockEntity.getInt("z")
                            );

                    Long actorUniqueId = links.get(position);
                    if (actorUniqueId == null) continue;

                    if (blockEntity.contains("SpawnedCreakingID")
                            && !blockEntity.containsNumber("SpawnedCreakingID")) {
                        throw new IOException("CreakingHeart has invalid SpawnedCreakingID at "
                                + position.x() + "," + position.y() + "," + position.z());
                    }

                    long existing = blockEntity.containsNumber("SpawnedCreakingID")
                            ? blockEntity.getLong("SpawnedCreakingID")
                            : 0L;

                    if (existing != 0L && existing != actorUniqueId) {
                        throw new IOException("CreakingHeart ActorUniqueID conflict at "
                                + position.x() + "," + position.y() + "," + position.z()
                                + ": stored=" + existing + ", migrated=" + actorUniqueId);
                    }

                    if (existing != actorUniqueId) {
                        blockEntity.putLong("SpawnedCreakingID", actorUniqueId);
                        chunkChanged = true;
                    }
                }

                if (chunkChanged) {
                    batch.put(key, LevelDBMigrationChunkSerializer.writeLittleEndianCompounds(blockEntities));
                    changed = true;
                }
            }

            if (changed) {
                storage.writeBatch(batch);
            }
        }
    }

    /**
     * Reconciles actorprefix keys and actor digests with their canonical ActorUniqueIDs.
     */
    public static void reconcileActorStorage(LevelDBStorage storage, DB db) throws IOException {
        List<Long> actorStorageKeys = new ArrayList<>();
        List<byte[]> digestKeys = new ArrayList<>();

        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                byte[] key = iterator.peekNext().getKey();
                if (isActorStorageKey(key)) {
                    actorStorageKeys.add(readLongBigEndian(key, ACTOR_PREFIX.length));
                } else if (isActorDigestKey(key)) {
                    digestKeys.add(key.clone());
                }
            }
        }

        Map<Long, Long> canonicalStorageKeys = new HashMap<>();
        Map<Long, CompoundTag> actorTags = new HashMap<>();
        Map<Long, byte[]> actorData = new HashMap<>();

        for (long actorStorageKey : actorStorageKeys) {
            byte[] data = db.get(LevelDBActorStorage.getActorKey(actorStorageKey));
            if (data == null) {
                throw new IOException(
                        "Actorprefix disappeared while reconciling storage key "
                                + Long.toUnsignedString(actorStorageKey)
                );
            }

            List<CompoundTag> tags = LevelDBMigrationChunkSerializer.readLittleEndianCompounds(data);
            if (tags.size() != 1) {
                throw new IOException(
                        "Invalid actorprefix record while reconciling storage key "
                                + Long.toUnsignedString(actorStorageKey)
                );
            }

            CompoundTag actor = tags.get(0);
            long canonicalStorageKey = actorStorageKey;
            long expectedUniqueId = LevelDBActorStorage.getActorUniqueId(actorStorageKey);

            if (actor.contains("UniqueID")) {
                long storedUniqueId = actor.getLong("UniqueID");
                if (storedUniqueId != 0 && storedUniqueId != expectedUniqueId) {
                    try {
                        canonicalStorageKey = LevelDBActorStorage.getActorStorageKey(storedUniqueId);
                    } catch (IllegalArgumentException e) {
                        throw new IOException(
                                "Invalid Actor UniqueID while reconciling storage key "
                                        + Long.toUnsignedString(actorStorageKey)
                                        + ": "
                                        + storedUniqueId,
                                e
                        );
                    }
                }
            }

            canonicalStorageKeys.put(actorStorageKey, canonicalStorageKey);
            actorTags.put(actorStorageKey, actor);
            actorData.put(actorStorageKey, data);
        }

        Map<Long, Long> keeperByCanonicalStorageKey = new HashMap<>();

        for (long actorStorageKey : actorStorageKeys) {
            long canonicalStorageKey = canonicalStorageKeys.get(actorStorageKey);
            Long keeperStorageKey = keeperByCanonicalStorageKey.putIfAbsent(canonicalStorageKey, actorStorageKey);

            if (keeperStorageKey == null) continue;

            if (!areEquivalentActorTags(actorTags.get(keeperStorageKey), actorTags.get(actorStorageKey))) {
                throw new IOException(
                        "Duplicate ActorUniqueID conflict while reconciling canonical storage key "
                                + Long.toUnsignedString(canonicalStorageKey)
                );
            }

            if (keeperStorageKey != canonicalStorageKey && actorStorageKey == canonicalStorageKey) {
                keeperByCanonicalStorageKey.put(canonicalStorageKey, actorStorageKey);
            }
        }

        Set<Long> finalActorStorageKeys = new HashSet<>(keeperByCanonicalStorageKey.keySet());

        try (WriteBatch batch = storage.createBatch()) {
            boolean changed = false;
            int rekeyedActors = 0;
            int duplicateActors = 0;
            int updatedDigests = 0;

            for (long actorStorageKey : actorStorageKeys) {
                long canonicalStorageKey = canonicalStorageKeys.get(actorStorageKey);
                long keeperStorageKey = keeperByCanonicalStorageKey.get(canonicalStorageKey);

                if (actorStorageKey != keeperStorageKey) {
                    batch.delete(LevelDBActorStorage.getActorKey(actorStorageKey));
                    duplicateActors++;
                    changed = true;
                    continue;
                }

                if (actorStorageKey != canonicalStorageKey) {
                    batch.delete(LevelDBActorStorage.getActorKey(actorStorageKey));
                    batch.put(LevelDBActorStorage.getActorKey(canonicalStorageKey), actorData.get(actorStorageKey));
                    rekeyedActors++;
                    changed = true;
                }
            }

            for (byte[] digestKey : digestKeys) {
                byte[] digestData = db.get(digestKey);
                if (digestData == null) continue;

                List<Long> actorKeys = LevelDBActorStorage.readDigest(digestData);
                List<Long> validActorKeys = new ArrayList<>(actorKeys.size());
                Set<Long> seenActorKeys = new HashSet<>();
                boolean digestChanged = false;

                for (long actorStorageKey : actorKeys) {
                    long canonicalStorageKey = canonicalStorageKeys.getOrDefault(actorStorageKey, actorStorageKey);

                    if (canonicalStorageKey != actorStorageKey) {
                        digestChanged = true;
                    }

                    if (!finalActorStorageKeys.contains(canonicalStorageKey)) {
                        digestChanged = true;
                        continue;
                    }

                    if (!seenActorKeys.add(canonicalStorageKey)) {
                        digestChanged = true;
                        continue;
                    }

                    validActorKeys.add(canonicalStorageKey);
                }

                if (digestChanged) {
                    batch.put(digestKey, LevelDBActorStorage.writeDigest(validActorKeys));
                    updatedDigests++;
                    changed = true;
                }
            }

            if (changed) {
                storage.writeBatch(batch);
                log.info(
                        "[LevelDB Migration] Actor storage reconciliation completed: {} re-keyed, {} duplicates removed, {} digests updated",
                        rekeyedActors, duplicateActors, updatedDigests
                );
            }
        }
    }

    /**
     * Reconciles one native BDS actor digest without converting the chunk to PNX storage.
     */
    public static void reconcileNativeBdsActorDigest(
            LevelDBStorage storage,
            DB db,
            int chunkX,
            int chunkZ,
            DimensionData dimensionData
    ) throws IOException {
        byte[] digestKey = LevelDBActorStorage.getDigestKey(chunkX, chunkZ, dimensionData);
        byte[] digestBytes = db.get(digestKey);
        if (digestBytes == null) return;

        List<Long> actorStorageKeys = LevelDBActorStorage.readDigest(digestBytes);
        List<Long> validActorStorageKeys = new ArrayList<>(actorStorageKeys.size());
        Set<Long> seenDigestStorageKeys = new HashSet<>();
        Set<Long> canonicalActorStorageKeys = new HashSet<>();

        try (WriteBatch batch = storage.createBatch()) {
            boolean changed = false;

            for (long actorStorageKey : actorStorageKeys) {
                if (!seenDigestStorageKeys.add(actorStorageKey)) {
                    changed = true;
                    continue;
                }

                byte[] actorKey = LevelDBActorStorage.getActorKey(actorStorageKey);
                byte[] actorBytes = db.get(actorKey);
                if (actorBytes == null) {
                    changed = true;
                    continue;
                }

                List<CompoundTag> actorTags = LevelDBMigrationChunkSerializer.readLittleEndianCompounds(actorBytes);
                if (actorTags.size() != 1) {
                    throw new IOException(
                            "Invalid actorprefix record while reconciling chunk ["
                                    + chunkX
                                    + ","
                                    + chunkZ
                                    + "] for storage key "
                                    + Long.toUnsignedString(actorStorageKey)
                    );
                }

                CompoundTag actor = actorTags.get(0);
                long canonicalActorStorageKey = actorStorageKey;
                long expectedUniqueId = LevelDBActorStorage.getActorUniqueId(actorStorageKey);

                if (actor.contains("UniqueID")) {
                    long storedUniqueId = actor.getLong("UniqueID");
                    if (storedUniqueId != 0 && storedUniqueId != expectedUniqueId) {
                        try {
                            canonicalActorStorageKey = LevelDBActorStorage.getActorStorageKey(storedUniqueId);
                        } catch (IllegalArgumentException e) {
                            throw new IOException(
                                    "Invalid Actor UniqueID while reconciling chunk ["
                                            + chunkX
                                            + ","
                                            + chunkZ
                                            + "]: "
                                            + storedUniqueId,
                                    e
                            );
                        }

                        byte[] canonicalActorKey = LevelDBActorStorage.getActorKey(canonicalActorStorageKey);
                        if (db.get(canonicalActorKey) != null) {
                            throw new IOException(
                                    "Canonical actorprefix storage key already exists while reconciling chunk ["
                                            + chunkX
                                            + ","
                                            + chunkZ
                                            + "] for ActorUniqueID "
                                            + storedUniqueId
                            );
                        }

                        batch.put(canonicalActorKey, actorBytes);
                        batch.delete(actorKey);
                        changed = true;
                    }
                }

                if (!canonicalActorStorageKeys.add(canonicalActorStorageKey)) {
                    throw new IOException(
                            "Duplicate live ActorUniqueID while reconciling chunk ["
                                    + chunkX
                                    + ","
                                    + chunkZ
                                    + "] for storage key "
                                    + Long.toUnsignedString(canonicalActorStorageKey)
                    );
                }

                validActorStorageKeys.add(canonicalActorStorageKey);
            }

            if (!changed && validActorStorageKeys.size() == actorStorageKeys.size()) return;

            batch.put(digestKey, LevelDBActorStorage.writeDigest(validActorStorageKeys));
            storage.writeBatch(batch);
        }
    }

    private static boolean areEquivalentActorTags(Tag first, Tag second) {
        if (first instanceof CompoundTag firstCompound && second instanceof CompoundTag secondCompound) {
            Map<String, Tag> firstTags = firstCompound.getTags();
            Map<String, Tag> secondTags = secondCompound.getTags();
            if (!firstTags.keySet().equals(secondTags.keySet())) return false;

            for (String name : firstTags.keySet()) {
                if (!areEquivalentActorTags(firstTags.get(name), secondTags.get(name))) return false;
            }

            return true;
        }

        if (first instanceof ListTag<?> firstList && second instanceof ListTag<?> secondList) {
            if (firstList.size() != secondList.size()) return false;
            if (firstList.size() == 0) return true;
            if (firstList.type != secondList.type) return false;

            for (int i = 0; i < firstList.size(); i++) {
                if (!areEquivalentActorTags(firstList.get(i), secondList.get(i))) return false;
            }

            return true;
        }

        return first.equals(second);
    }

    private static boolean isActorStorageKey(byte[] key) {
        return key.length == ACTOR_PREFIX.length + Long.BYTES && startsWith(key, ACTOR_PREFIX);
    }

    private static boolean isActorDigestKey(byte[] key) {
        return (key.length == DIGEST_PREFIX.length + Integer.BYTES * 2
                || key.length == DIGEST_PREFIX.length + Integer.BYTES * 3)
                && startsWith(key, DIGEST_PREFIX);
    }

    private static boolean startsWith(byte[] key, byte[] prefix) {
        if (key.length < prefix.length) return false;

        for (int i = 0; i < prefix.length; i++) {
            if (key[i] != prefix[i]) return false;
        }

        return true;
    }

    private static long readLongBigEndian(byte[] data, int offset) {
        long value = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            value = value << 8 | data[offset + i] & 0xffL;
        }
        return value;
    }

    private static long getChunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX & 0xffffffffL) << 32 | ((long) chunkZ & 0xffffffffL);
    }
}
