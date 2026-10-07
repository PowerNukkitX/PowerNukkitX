package org.powernukkitx.migration.steps;

import lombok.extern.slf4j.Slf4j;
import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.ScoreboardMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.scoreboard.data.DisplaySlot;
import org.powernukkitx.utils.MapParsingUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToLongFunction;

/**
 * Converts legacy scoreboard objectives, scores and identities to the canonical 3.1.0 representation.
 *
 * @author Curse
 */
@Slf4j
public final class ScoreboardV3_1_0Migration implements MigrationStep<ScoreboardMigrationData> {
    private static final byte IDENTITY_PLAYER = 1;
    private static final byte IDENTITY_ENTITY = 2;
    private static final byte IDENTITY_FAKE = 3;
    private static final Function<String, RuntimeException> SCOREBOARD_ERROR =
            field -> new IllegalArgumentException("Invalid legacy scoreboard data: " + field);

    /**
     * Represents one normalized scoreboard identity during legacy conversion.
     */
    private record Identity(byte type, long uniqueId, String fakeName) {
    }

    @Override
    public MigrationFormat format() {
        return MigrationFormat.SCOREBOARD;
    }

    @Override
    public MigrationVersion targetVersion() {
        return MigrationVersion.V3_1_0;
    }

    @Override
    public ScoreboardMigrationData migrate(MigrationContext context, ScoreboardMigrationData value) throws IOException {
        return migrateRecord(
                value,
                context.getServer()::resolvePlayerUniqueId,
                context.getMigratedActorUniqueIdsByUuid(),
                context.getMigratedLegacyPlayerUniqueIds()
        );
    }

    private static ScoreboardMigrationData migrateRecord(
            ScoreboardMigrationData source,
            ToLongFunction<UUID> playerUniqueIdResolver,
            Map<UUID, Long> entityUniqueIdsByUuid,
            Map<Long, Long> legacyPlayerUniqueIdRemap
    ) throws IOException {
        CompoundTag root = source.scoreboard().copy();
        if (!root.containsList("Criteria")) {
            root.putList("Criteria", new ListTag<>());
        }

        Map<String, Object> rawScoreboards = source.legacyScoreboards();
        long lastUniqueId = Math.max(root.getLong("LastUniqueID"), source.legacyLastUniqueId());

        Map<Identity, Long> identityIds = new LinkedHashMap<>();
        Map<Long, Identity> idOwners = new HashMap<>();
        readCanonicalIdentities(root, identityIds, idOwners);

        Map<String, CompoundTag> objectivesByName = new LinkedHashMap<>();
        if (root.containsList("Objectives")) {
            for (CompoundTag objective : root.getList("Objectives", CompoundTag.class).getAll()) {
                objectivesByName.put(objective.getString("Name"), objective);
            }
        }

        ListTag<CompoundTag> displays = root.containsList("DisplayObjectives")
                ? root.getList("DisplayObjectives", CompoundTag.class)
                : new ListTag<>(Tag.TAG_Compound);
        Set<String> displaySlots = new HashSet<>();
        for (CompoundTag display : displays.getAll()) {
            displaySlots.add(display.getString("Name"));
        }

        Map<String, Byte> objectiveSortOrders = new HashMap<>();

        int addedObjectives = 0;
        int addedIdentities = 0;
        int addedScores = 0;
        int skippedScores = 0;

        if (rawScoreboards != null) {
            for (Map.Entry<String, Object> scoreboardEntry : rawScoreboards.entrySet()) {
                Map<String, Object> scoreboard = MapParsingUtils.stringObjectMap(
                        scoreboardEntry.getValue(),
                        "scoreboard." + scoreboardEntry.getKey(),
                        SCOREBOARD_ERROR
                );

                String objectiveName = Objects.toString(
                        scoreboard.get("objectiveName"),
                        scoreboardEntry.getKey()
                );
                String displayName = Objects.toString(
                        scoreboard.get("displayName"),
                        objectiveName
                );
                String criteriaName = Objects.toString(
                        scoreboard.get("criteriaName"),
                        "dummy"
                );
                byte sortOrder = "DESCENDING".equals(Objects.toString(scoreboard.get("sortOrder"), "ASCENDING"))
                        ? (byte) 0
                        : (byte) 1;

                objectiveSortOrders.put(objectiveName, sortOrder);

                CompoundTag objective = objectivesByName.get(objectiveName);
                if (objective == null) {
                    objective = new CompoundTag()
                            .putString("Criteria", criteriaName)
                            .putString("DisplayName", displayName)
                            .putString("Name", objectiveName)
                            .putList("Scores", new ListTag<CompoundTag>(Tag.TAG_Compound));
                    objectivesByName.put(objectiveName, objective);
                    addedObjectives++;
                }

                ListTag<CompoundTag> scores = objective.containsList("Scores")
                        ? objective.getList("Scores", CompoundTag.class)
                        : new ListTag<>(Tag.TAG_Compound);

                Object rawLines = scoreboard.get("lines");

                if (rawLines instanceof List<?> lines) {
                    for (Object rawLine : lines) {
                        Map<String, Object> line = MapParsingUtils.stringObjectMap(
                                rawLine,
                                "scoreboard." + objectiveName + ".line",
                                SCOREBOARD_ERROR
                        );

                        if (!(line.get("score") instanceof Number scoreNumber)) {
                            throw new IOException("Legacy scoreboard line in " + objectiveName + " is missing score");
                        }

                        String scorerType = Objects.toString(line.get("scorerType"), null);
                        String identityValue = Objects.toString(line.get("name"), null);

                        if (scorerType == null || identityValue == null) {
                            throw new IOException("Legacy scoreboard line in " + objectiveName + " is missing scorer identity");
                        }

                        Identity identity = switch (scorerType) {
                            case "PLAYER", "CHANGE_PLAYER" -> new Identity(
                                    IDENTITY_PLAYER,
                                    resolvePlayerUniqueId(identityValue, playerUniqueIdResolver, legacyPlayerUniqueIdRemap),
                                    null
                            );
                            case "ENTITY", "CHANGE_ENTITY" -> new Identity(
                                    IDENTITY_ENTITY,
                                    resolveEntityUniqueId(identityValue, entityUniqueIdsByUuid),
                                    null
                            );
                            case "FAKE", "CHANGE_FAKE_PLAYER" -> new Identity(
                                    IDENTITY_FAKE,
                                    0,
                                    identityValue
                            );
                            default -> throw new IOException("Unsupported legacy scoreboard scorer type " + scorerType);
                        };

                        Long scoreboardId = identityIds.get(identity);

                        if (scoreboardId == null) {
                            long candidate = line.get("scoreboardId") instanceof Number number
                                    ? number.longValue()
                                    : 0;

                            if (candidate > 0 && !idOwners.containsKey(candidate)) {
                                scoreboardId = candidate;
                            } else {
                                do {
                                    lastUniqueId++;
                                } while (idOwners.containsKey(lastUniqueId));

                                scoreboardId = lastUniqueId;
                            }

                            identityIds.put(identity, scoreboardId);
                            idOwners.put(scoreboardId, identity);
                            lastUniqueId = Math.max(lastUniqueId, scoreboardId);
                            addedIdentities++;
                        }

                        if (containsScore(scores, scoreboardId)) {
                            skippedScores++;
                            continue;
                        }

                        scores.add(new CompoundTag()
                                .putInt("Score", scoreNumber.intValue())
                                .putLong("ScoreboardId", scoreboardId));
                        addedScores++;
                    }
                }

                objective.putList("Scores", scores);
            }
        }

        Map<String, String> rawDisplay = source.legacyDisplay();

        if (rawDisplay != null) {
            for (Map.Entry<String, String> displayEntry : rawDisplay.entrySet()) {
                if (displayEntry.getValue() == null) continue;

                DisplaySlot slot = legacyDisplaySlot(displayEntry.getKey());
                if (displaySlots.contains(slot.getSlotName())) {
                    continue;
                }

                displays.add(new CompoundTag()
                        .putString("Name", slot.getSlotName())
                        .putString("ObjectiveName", displayEntry.getValue())
                        .putByte("SortOrder", objectiveSortOrders.getOrDefault(displayEntry.getValue(), (byte) 1)));
                displaySlots.add(slot.getSlotName());
            }
        }

        ListTag<CompoundTag> entries = new ListTag<>(Tag.TAG_Compound);
        for (Map.Entry<Identity, Long> identityEntry : identityIds.entrySet()) {
            Identity identity = identityEntry.getKey();

            CompoundTag entry = new CompoundTag()
                    .putByte("IdentityType", identity.type())
                    .putLong("ScoreboardId", identityEntry.getValue());

            switch (identity.type()) {
                case IDENTITY_PLAYER -> entry.putLong("PlayerId", identity.uniqueId());
                case IDENTITY_ENTITY -> entry.putLong("EntityID", identity.uniqueId());
                case IDENTITY_FAKE -> entry.putString("FakePlayerName", identity.fakeName());
                default -> throw new IOException("Invalid migrated scoreboard identity type " + identity.type());
            }

            entries.add(entry);
        }

        ListTag<CompoundTag> objectives = new ListTag<>(Tag.TAG_Compound);
        objectives.addAll(objectivesByName.values());

        root.putList("DisplayObjectives", displays);
        root.putList("Entries", entries);
        root.putLong("LastUniqueID", lastUniqueId);
        root.putList("Objectives", objectives);

        log.info(
                "[Scoreboard Migration] Added {} objectives, {} identities and {} scores; skipped {} existing canonical scores",
                addedObjectives,
                addedIdentities,
                addedScores,
                skippedScores
        );

        return new ScoreboardMigrationData(root, null, null, 0);
    }

    private static void readCanonicalIdentities(
            CompoundTag root,
            Map<Identity, Long> identityIds,
            Map<Long, Identity> idOwners
    ) throws IOException {
        if (!root.containsList("Entries")) {
            return;
        }

        for (CompoundTag entry : root.getList("Entries", CompoundTag.class).getAll()) {
            Identity identity = switch (entry.getByte("IdentityType")) {
                case IDENTITY_PLAYER -> new Identity(
                        IDENTITY_PLAYER,
                        entry.getLong("PlayerId"),
                        null
                );
                case IDENTITY_ENTITY -> new Identity(
                        IDENTITY_ENTITY,
                        entry.getLong("EntityID"),
                        null
                );
                case IDENTITY_FAKE -> new Identity(
                        IDENTITY_FAKE,
                        0,
                        entry.getString("FakePlayerName")
                );
                default -> throw new IOException(
                        "Invalid canonical scoreboard IdentityType " + entry.getByte("IdentityType")
                );
            };

            long scoreboardId = entry.getLong("ScoreboardId");

            Long existingId = identityIds.putIfAbsent(identity, scoreboardId);
            if (existingId != null && existingId != scoreboardId) {
                throw new IOException(
                        "Canonical scoreboard identity has conflicting IDs " + existingId + " and " + scoreboardId
                );
            }

            Identity existingOwner = idOwners.putIfAbsent(scoreboardId, identity);
            if (existingOwner != null && !existingOwner.equals(identity)) {
                throw new IOException(
                        "Canonical ScoreboardId " + scoreboardId + " has conflicting identities"
                );
            }
        }
    }

    private static boolean containsScore(ListTag<CompoundTag> scores, long scoreboardId) {
        for (CompoundTag score : scores.getAll()) {
            if (score.getLong("ScoreboardId") == scoreboardId) {
                return true;
            }
        }

        return false;
    }

    private static long resolvePlayerUniqueId(
            String identity,
            ToLongFunction<UUID> resolver,
            Map<Long, Long> legacyPlayerUniqueIdRemap
    ) throws IOException {
        try {
            long uniqueId = Long.parseLong(identity);
            return legacyPlayerUniqueIdRemap.getOrDefault(uniqueId, uniqueId);
        } catch (NumberFormatException ignored) {
        }

        try {
            return resolver.applyAsLong(UUID.fromString(identity));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IOException("Unable to resolve legacy player scoreboard UUID " + identity, e);
        }
    }

    private static long resolveEntityUniqueId(String identity, Map<UUID, Long> entityUniqueIdsByUuid) throws IOException {
        try {
            return Long.parseLong(identity);
        } catch (NumberFormatException ignored) {
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(identity);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid legacy entity scoreboard UUID " + identity, e);
        }

        Long uniqueId = entityUniqueIdsByUuid.get(uuid);
        if (uniqueId == null) {
            throw new IOException("Unable to resolve legacy entity scoreboard UUID " + uuid + " to an ActorUniqueID");
        }

        return uniqueId;
    }

    private static DisplaySlot legacyDisplaySlot(String value) throws IOException {
        try {
            return DisplaySlot.valueOf(value);
        } catch (IllegalArgumentException ignored) {
        }

        for (DisplaySlot slot : DisplaySlot.values()) {
            if (slot.getSlotName().equals(value)) {
                return slot;
            }
        }

        throw new IOException("Invalid legacy scoreboard display slot " + value);
    }
}
