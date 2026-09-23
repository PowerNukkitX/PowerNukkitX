package org.powernukkitx.migration.executor;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.ServerDBStorageFormat;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationService;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.ScoreboardMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.Config;
import org.powernukkitx.utils.MapParsingUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Orchestrates legacy and canonical scoreboard migrations into the server-global scoreboard storage.
 *
 * @author Curse
 */
@Slf4j
public final class ScoreboardMigrationExecutor {
    private static final byte IDENTITY_PLAYER = 1;
    private static final byte IDENTITY_ENTITY = 2;
    private static final byte IDENTITY_FAKE = 3;
    private static final Path LEGACY_SCOREBOARD_PATH = Path.of("command_data", "scoreboard.json");
    private static final Function<String, RuntimeException> SCOREBOARD_ERROR =
            field -> new IllegalArgumentException("Invalid legacy scoreboard data: " + field);

    private ScoreboardMigrationExecutor() {
    }

    /**
     * Migrates canonical scoreboard storage when a registered migration is pending.
     */
    public static void migrateIfNeeded(Path dataPath, DB targetDB, MigrationService migrationService) throws IOException {
        MigrationVersion currentVersion = readMigrationVersion(targetDB, migrationService);
        MigrationVersion targetVersion = migrationService.getLatestVersion(MigrationFormat.SCOREBOARD);

        if (currentVersion != null && currentVersion.compareTo(targetVersion) < 0) {
            ScoreboardMigrationData migrated = migrationService.apply(
                    MigrationFormat.SCOREBOARD,
                    currentVersion,
                    new ScoreboardMigrationData(readCurrentRoot(targetDB), null, null, 0)
            );

            writeMigration(targetDB, migrated.scoreboard(), targetVersion);
            log.info("[Scoreboard Migration] Migrated canonical scoreboard from {} to {}", currentVersion, targetVersion);
        } else if (currentVersion == null) {
            writeMigrationVersion(targetDB, targetVersion);
        }
    }

    /**
     * Merges legacy PNX scoreboard JSON after legacy world actor identities have been migrated.
     */
    public static boolean migrateLegacyPnxIfNeeded(Path dataPath, DB targetDB, MigrationService migrationService) throws IOException {
        Path legacyPath = dataPath.resolve(LEGACY_SCOREBOARD_PATH);

        if (!Files.exists(legacyPath)) {
            return false;
        }

        MigrationVersion targetVersion = migrationService.getLatestVersion(MigrationFormat.SCOREBOARD);
        Config json = new Config(legacyPath.toFile(), Config.JSON);
        Map<String, Object> rawScoreboards = MapParsingUtils.stringObjectMapOrNull(
                json.get("scoreboard"),
                "scoreboard",
                SCOREBOARD_ERROR
        );
        Map<String, String> rawDisplay = MapParsingUtils.stringStringMapOrNull(
                json.get("display"),
                "display",
                SCOREBOARD_ERROR
        );
        long legacyLastUniqueId = json.get("lastUniqueId") instanceof Number number
                ? Math.max(number.longValue(), 0)
                : 0;

        ScoreboardMigrationData migrated = migrationService.apply(
                MigrationFormat.SCOREBOARD,
                null,
                new ScoreboardMigrationData(
                        readCurrentRoot(targetDB),
                        rawScoreboards,
                        rawDisplay,
                        legacyLastUniqueId
                )
        );

        writeMigration(targetDB, migrated.scoreboard(), targetVersion);
        Files.delete(legacyPath);
        log.info("[Scoreboard Migration] Merged legacy PNX scoreboard into server_data");
        return true;
    }

    /**
     * Merges a native BDS scoreboard into the existing server-global scoreboard storage.
     */
    public static boolean migrateBdsWorldIfNeeded(
            LevelDBStorage storage,
            DB targetDB,
            Map<Long, Long> playerUniqueIdRemap
    ) throws IOException {
        DB sourceDB = storage.getDb();
        byte[] sourceBytes = sourceDB.get(ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_KEY);

        if (sourceBytes == null || sourceBytes.length == 0) {
            return false;
        }

        CompoundTag source = readRoot(sourceBytes);
        remapBdsPlayerUniqueIds(source, playerUniqueIdRemap);

        CompoundTag merged = mergeBdsScoreboard(readCurrentRoot(targetDB), source);
        targetDB.put(ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_KEY, writeRoot(merged));

        try (WriteBatch batch = storage.createBatch()) {
            batch.delete(ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_KEY);
            storage.writeBatch(batch);
        }

        log.info("[Scoreboard Migration] Merged BDS scoreboard into server_data");
        return true;
    }

    private static CompoundTag mergeBdsScoreboard(CompoundTag target, CompoundTag source) throws IOException {
        CompoundTag merged = target.copy();

        Map<Identity, Long> targetIdentityIds = new LinkedHashMap<>();
        Map<Long, Identity> targetIdOwners = new HashMap<>();
        readCanonicalIdentities(merged, targetIdentityIds, targetIdOwners);

        Map<Identity, Long> sourceIdentityIds = new LinkedHashMap<>();
        Map<Long, Identity> sourceIdOwners = new HashMap<>();
        readCanonicalIdentities(source, sourceIdentityIds, sourceIdOwners);

        long lastUniqueId = Math.max(0, Math.max(merged.getLong("LastUniqueID"), source.getLong("LastUniqueID")));
        for (long scoreboardId : targetIdOwners.keySet()) {
            lastUniqueId = Math.max(lastUniqueId, scoreboardId);
        }

        Map<Long, Long> sourceIdRemap = new HashMap<>();
        ListTag<CompoundTag> targetEntries = getCompoundList(merged, "Entries");

        for (CompoundTag sourceEntry : getCompoundList(source, "Entries").getAll()) {
            Identity identity = readIdentity(sourceEntry);
            long sourceId = sourceEntry.getLong("ScoreboardId");
            Long targetId = targetIdentityIds.get(identity);

            if (targetId == null) {
                if (sourceId > 0 && !targetIdOwners.containsKey(sourceId)) {
                    targetId = sourceId;
                } else {
                    do {
                        if (lastUniqueId == Long.MAX_VALUE) {
                            throw new IOException("ScoreboardId allocator exhausted");
                        }

                        lastUniqueId++;
                    } while (targetIdOwners.containsKey(lastUniqueId));

                    targetId = lastUniqueId;
                }

                targetIdentityIds.put(identity, targetId);
                targetIdOwners.put(targetId, identity);
                lastUniqueId = Math.max(lastUniqueId, targetId);

                targetEntries.add(sourceEntry.copy().putLong("ScoreboardId", targetId));
            }

            Long previous = sourceIdRemap.putIfAbsent(sourceId, targetId);
            if (previous != null && previous.longValue() != targetId.longValue()) {
                throw new IOException("BDS scoreboard identity " + sourceId + " maps to conflicting target IDs");
            }
        }

        merged.putList("Entries", targetEntries);

        Map<String, CompoundTag> targetObjectivesByName = new LinkedHashMap<>();
        ListTag<CompoundTag> targetObjectives = getCompoundList(merged, "Objectives");

        for (CompoundTag objective : targetObjectives.getAll()) {
            String name = objective.getString("Name");

            if (targetObjectivesByName.putIfAbsent(name, objective) != null) {
                throw new IOException("Canonical scoreboard contains duplicate objective " + name);
            }

            validateTargetScores(objective, targetIdOwners);
        }

        for (CompoundTag sourceObjective : getCompoundList(source, "Objectives").getAll()) {
            String name = sourceObjective.getString("Name");
            CompoundTag targetObjective = targetObjectivesByName.get(name);

            if (targetObjective == null) {
                CompoundTag imported = sourceObjective.copy();
                imported.putList("Scores", remapScores(sourceObjective, sourceIdRemap));
                targetObjectives.add(imported);
                targetObjectivesByName.put(name, imported);
                continue;
            }

            ListTag<CompoundTag> targetScores = getCompoundList(targetObjective, "Scores");
            Set<Long> existingScores = new HashSet<>();

            for (CompoundTag score : targetScores.getAll()) {
                existingScores.add(score.getLong("ScoreboardId"));
            }

            for (CompoundTag sourceScore : getCompoundList(sourceObjective, "Scores").getAll()) {
                long sourceId = sourceScore.getLong("ScoreboardId");
                Long targetId = sourceIdRemap.get(sourceId);

                if (targetId == null) {
                    throw new IOException("BDS scoreboard score references missing identity " + sourceId);
                }

                if (!existingScores.add(targetId)) {
                    continue;
                }

                targetScores.add(sourceScore.copy().putLong("ScoreboardId", targetId));
            }

            targetObjective.putList("Scores", targetScores);
        }

        merged.putList("Objectives", targetObjectives);
        mergeDisplays(merged, source);
        mergeCriteria(merged, source);
        merged.putLong("LastUniqueID", lastUniqueId);
        return merged;
    }

    private static ListTag<CompoundTag> remapScores(
            CompoundTag objective,
            Map<Long, Long> sourceIdRemap
    ) throws IOException {
        ListTag<CompoundTag> scores = new ListTag<>(Tag.TAG_Compound);

        for (CompoundTag sourceScore : getCompoundList(objective, "Scores").getAll()) {
            long sourceId = sourceScore.getLong("ScoreboardId");
            Long targetId = sourceIdRemap.get(sourceId);

            if (targetId == null) {
                throw new IOException("BDS scoreboard score references missing identity " + sourceId);
            }

            scores.add(sourceScore.copy().putLong("ScoreboardId", targetId));
        }

        return scores;
    }

    private static void mergeDisplays(CompoundTag target, CompoundTag source) throws IOException {
        ListTag<CompoundTag> targetDisplays = getCompoundList(target, "DisplayObjectives");
        Set<String> targetSlots = new HashSet<>();

        for (CompoundTag display : targetDisplays.getAll()) {
            targetSlots.add(display.getString("Name"));
        }

        for (CompoundTag sourceDisplay : getCompoundList(source, "DisplayObjectives").getAll()) {
            if (targetSlots.add(sourceDisplay.getString("Name"))) {
                targetDisplays.add(sourceDisplay.copy());
            }
        }

        target.putList("DisplayObjectives", targetDisplays);
    }

    private static void mergeCriteria(CompoundTag target, CompoundTag source) throws IOException {
        if (!source.contains("Criteria")) {
            return;
        }
        if (!source.containsList("Criteria")) {
            throw new IOException("BDS scoreboard Criteria is not a list");
        }
        if (target.contains("Criteria") && !target.containsList("Criteria")) {
            throw new IOException("Canonical scoreboard Criteria is not a list");
        }

        ListTag<?> sourceCriteria = source.getList("Criteria");
        ListTag<?> targetCriteria = target.containsList("Criteria") ? target.getList("Criteria") : new ListTag<>();

        if (sourceCriteria.size() == 0) {
            if (!target.containsList("Criteria")) {
                target.putList("Criteria", new ListTag<>());
            }
            return;
        }

        if (targetCriteria.size() != 0 && targetCriteria.type != sourceCriteria.type) {
            throw new IOException("BDS and canonical scoreboard Criteria use different list types");
        }

        ListTag<Tag> mergedCriteria = new ListTag<>(targetCriteria.size() != 0 ? targetCriteria.type : sourceCriteria.type);

        for (Tag criterion : targetCriteria.getAll()) {
            mergedCriteria.add(criterion.copy());
        }

        for (Tag sourceCriterion : sourceCriteria.getAll()) {
            boolean exists = false;

            for (Tag targetCriterion : mergedCriteria.getAll()) {
                if (targetCriterion.equals(sourceCriterion)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                mergedCriteria.add(sourceCriterion.copy());
            }
        }

        target.putList("Criteria", mergedCriteria);
    }

    private static void validateTargetScores(CompoundTag objective, Map<Long, Identity> idOwners) throws IOException {
        for (CompoundTag score : getCompoundList(objective, "Scores").getAll()) {
            long scoreboardId = score.getLong("ScoreboardId");

            if (!idOwners.containsKey(scoreboardId)) {
                throw new IOException(
                        "Canonical scoreboard objective " + objective.getString("Name") + " references missing identity " + scoreboardId
                );
            }
        }
    }

    private static void readCanonicalIdentities(
            CompoundTag root,
            Map<Identity, Long> identityIds,
            Map<Long, Identity> idOwners
    ) throws IOException {
        for (CompoundTag entry : getCompoundList(root, "Entries").getAll()) {
            Identity identity = readIdentity(entry);
            long scoreboardId = entry.getLong("ScoreboardId");

            Long existingId = identityIds.putIfAbsent(identity, scoreboardId);
            if (existingId != null && existingId != scoreboardId) {
                throw new IOException("Scoreboard identity has conflicting IDs " + existingId + " and " + scoreboardId);
            }

            Identity existingOwner = idOwners.putIfAbsent(scoreboardId, identity);
            if (existingOwner != null && !existingOwner.equals(identity)) {
                throw new IOException("ScoreboardId " + scoreboardId + " has conflicting identities");
            }
        }
    }

    private static Identity readIdentity(CompoundTag entry) throws IOException {
        return switch (entry.getByte("IdentityType")) {
            case IDENTITY_PLAYER -> new Identity(IDENTITY_PLAYER, entry.getLong("PlayerId"), null);
            case IDENTITY_ENTITY -> new Identity(IDENTITY_ENTITY, entry.getLong("EntityID"), null);
            case IDENTITY_FAKE -> new Identity(IDENTITY_FAKE, 0, entry.getString("FakePlayerName"));
            default -> throw new IOException("Invalid scoreboard IdentityType " + entry.getByte("IdentityType"));
        };
    }

    private static ListTag<CompoundTag> getCompoundList(CompoundTag root, String name) throws IOException {
        if (!root.contains(name)) {
            return new ListTag<>(Tag.TAG_Compound);
        }
        if (!root.containsList(name)) {
            throw new IOException("Scoreboard " + name + " is not a list");
        }

        ListTag<?> list = root.getList(name);

        if (list.size() != 0 && list.type != Tag.TAG_Compound) {
            throw new IOException("Scoreboard " + name + " is not a compound list");
        }

        return root.getList(name, CompoundTag.class);
    }

    private static void remapBdsPlayerUniqueIds(CompoundTag root, Map<Long, Long> playerUniqueIdRemap) throws IOException {
        if (!root.containsList("Entries")) {
            return;
        }

        for (CompoundTag entry : root.getList("Entries", CompoundTag.class).getAll()) {
            if (entry.getByte("IdentityType") != IDENTITY_PLAYER) {
                continue;
            }

            long oldUniqueId = entry.getLong("PlayerId");
            Long newUniqueId = playerUniqueIdRemap.get(oldUniqueId);

            if (newUniqueId == null) {
                throw new IOException("BDS scoreboard references unmigrated player ActorUniqueID " + oldUniqueId);
            }

            entry.putLong("PlayerId", newUniqueId);
        }
    }

    private record Identity(byte type, long uniqueId, String fakeName) {
    }

    private static MigrationVersion readMigrationVersion(DB targetDB, MigrationService migrationService) throws IOException {
        byte[] stored = targetDB.get(ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_STORAGE_VERSION_KEY);
        if (stored == null) return null;
        return migrationService.parseStoredVersion(MigrationFormat.SCOREBOARD, new String(stored, StandardCharsets.UTF_8));
    }

    private static CompoundTag readCurrentRoot(DB targetDB) throws IOException {
        byte[] bytes = targetDB.get(ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_KEY);
        return bytes != null ? readRoot(bytes) : new CompoundTag();
    }

    private static void writeMigration(DB targetDB, CompoundTag root, MigrationVersion version) throws IOException {
        try (WriteBatch batch = targetDB.createWriteBatch()) {
            batch.put(ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_KEY, writeRoot(root));
            batch.put(
                    ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_STORAGE_VERSION_KEY,
                    version.toString().getBytes(StandardCharsets.UTF_8)
            );
            targetDB.write(batch);
        }
    }

    private static void writeMigrationVersion(DB targetDB, MigrationVersion version) {
        targetDB.put(
                ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_STORAGE_VERSION_KEY,
                version.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    private static byte[] writeRoot(CompoundTag root) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(root.toNetwork());
            nbtOutputStream.close();
            return outputStream.toByteArray();
        }
    }

    private static CompoundTag readRoot(byte[] bytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            return CompoundTag.fromNetwork((NbtMap) nbtInputStream.readTag());
        }
    }
}
