package org.powernukkitx.scoreboard.storage;

import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.ObjectiveSortOrder;
import org.iq80.leveldb.DB;
import org.powernukkitx.ServerDBStorageFormat;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;
import org.powernukkitx.scoreboard.Scoreboard;
import org.powernukkitx.scoreboard.ScoreboardLine;
import org.powernukkitx.scoreboard.data.DisplaySlot;
import org.powernukkitx.scoreboard.scorer.EntityScorer;
import org.powernukkitx.scoreboard.scorer.FakeScorer;
import org.powernukkitx.scoreboard.scorer.IScorer;
import org.powernukkitx.scoreboard.scorer.PlayerScorer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Persists scoreboard definitions, display slots, and the last scoreboard unique ID in LevelDB.
 *
 * @author Curse
 */
public final class LevelDBScoreboardStorage implements IScoreboardStorage {
    private static final byte IDENTITY_PLAYER = 1;
    private static final byte IDENTITY_ENTITY = 2;
    private static final byte IDENTITY_FAKE = 3;

    private final DB db;

    /**
     * Creates a new LevelDBScoreboardStorage instance.
     *
     * @param db value for this API
     */
    public LevelDBScoreboardStorage(DB db) {
        this.db = db;
    }

    @Override
    public synchronized void saveScoreboard(IScoreboard scoreboard) {
        if (scoreboard == null) return;

        Map<String, IScoreboard> scoreboards = readScoreboard();
        scoreboards.put(scoreboard.getObjectiveName(), scoreboard);

        CompoundTag root = readRoot();
        writeScoreboards(root, scoreboards.values());
        writeRoot(root);
    }

    @Override
    public synchronized void saveScoreboard(Collection<IScoreboard> scoreboards) {
        CompoundTag root = readRoot();
        writeScoreboards(root, scoreboards);
        writeRoot(root);
    }

    @Override
    public synchronized void saveDisplay(Map<DisplaySlot, IScoreboard> display) {
        CompoundTag root = readRoot();
        ListTag<CompoundTag> displays = new ListTag<>(Tag.TAG_Compound);

        for (Map.Entry<DisplaySlot, IScoreboard> entry : display.entrySet()) {
            IScoreboard scoreboard = entry.getValue();
            if (scoreboard == null) continue;

            displays.add(new CompoundTag()
                    .putString("Name", entry.getKey().getSlotName())
                    .putString("ObjectiveName", scoreboard.getObjectiveName())
                    .putByte("SortOrder", sortOrder(scoreboard.getSortOrder())));
        }

        root.putList("DisplayObjectives", displays);
        writeRoot(root);
    }

    @Override
    public synchronized Map<String, IScoreboard> readScoreboard() {
        CompoundTag root = readRoot();
        Map<Long, IScorer> identities = readIdentities(root);
        Map<String, IScoreboard> result = new LinkedHashMap<>();

        if (root.containsList("Objectives")) {
            for (CompoundTag objectiveTag : root.getList("Objectives", CompoundTag.class).getAll()) {
                String name = objectiveTag.getString("Name");
                String displayName = objectiveTag.getString("DisplayName");
                String criteria = objectiveTag.getString("Criteria");

                Scoreboard scoreboard = new Scoreboard(name, displayName, criteria, ObjectiveSortOrder.ASCENDING);

                if (objectiveTag.containsList("Scores")) {
                    for (CompoundTag scoreTag : objectiveTag.getList("Scores", CompoundTag.class).getAll()) {
                        long scoreboardId = scoreTag.getLong("ScoreboardId");
                        IScorer scorer = identities.get(scoreboardId);
                        if (scorer == null) {
                            throw new IllegalStateException("Scoreboard score references missing identity " + scoreboardId);
                        }

                        scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, scoreTag.getInt("Score"), scoreboardId));
                    }
                }

                result.put(name, scoreboard);
            }
        }

        if (root.containsList("DisplayObjectives")) {
            for (CompoundTag displayTag : root.getList("DisplayObjectives", CompoundTag.class).getAll()) {
                IScoreboard scoreboard = result.get(displayTag.getString("ObjectiveName"));
                if (scoreboard instanceof Scoreboard concrete) {
                    concrete.setSortOrder(readSortOrder(displayTag.getByte("SortOrder")));
                }
            }
        }

        return result;
    }

    @Override
    public synchronized IScoreboard readScoreboard(String name) {
        return readScoreboard().get(name);
    }

    @Override
    public synchronized Map<DisplaySlot, String> readDisplay() {
        CompoundTag root = readRoot();
        Map<DisplaySlot, String> result = new HashMap<>();

        if (!root.containsList("DisplayObjectives")) return result;

        for (CompoundTag displayTag : root.getList("DisplayObjectives", CompoundTag.class).getAll()) {
            result.put(displaySlot(displayTag.getString("Name")), displayTag.getString("ObjectiveName"));
        }

        return result;
    }

    @Override
    public synchronized void removeScoreboard(String name) {
        Map<String, IScoreboard> scoreboards = readScoreboard();
        scoreboards.remove(name);

        CompoundTag root = readRoot();
        writeScoreboards(root, scoreboards.values());
        writeRoot(root);
    }

    @Override
    public synchronized void removeAllScoreboard() {
        CompoundTag root = readRoot();
        root.putList("Entries", new ListTag<CompoundTag>(Tag.TAG_Compound));
        root.putList("Objectives", new ListTag<CompoundTag>(Tag.TAG_Compound));
        writeRoot(root);
    }

    @Override
    public synchronized boolean containScoreboard(String name) {
        return readScoreboard().containsKey(name);
    }

    @Override
    public synchronized long readLastUniqueId() {
        return readRoot().getLong("LastUniqueID");
    }

    @Override
    public synchronized void saveLastUniqueId(long lastUniqueId) {
        CompoundTag root = readRoot();
        root.putLong("LastUniqueID", lastUniqueId);
        writeRoot(root);
    }

    private Map<Long, IScorer> readIdentities(CompoundTag root) {
        Map<Long, IScorer> identities = new HashMap<>();

        if (!root.containsList("Entries")) {
            return identities;
        }

        for (CompoundTag entry : root.getList("Entries", CompoundTag.class).getAll()) {
            long scoreboardId = entry.getLong("ScoreboardId");
            IScorer scorer = switch (entry.getByte("IdentityType")) {
                case IDENTITY_PLAYER -> new PlayerScorer(entry.getLong("PlayerId"));
                case IDENTITY_ENTITY -> new EntityScorer(entry.getLong("EntityID"));
                case IDENTITY_FAKE -> new FakeScorer(entry.getString("FakePlayerName"));
                default -> throw new IllegalStateException("Invalid scoreboard IdentityType " + entry.getByte("IdentityType"));
            };

            IScorer previous = identities.putIfAbsent(scoreboardId, scorer);
            if (previous != null && !previous.equals(scorer)) {
                throw new IllegalStateException("ScoreboardId " + scoreboardId + " has conflicting identities");
            }
        }

        return identities;
    }

    private void writeScoreboards(CompoundTag root, Collection<IScoreboard> scoreboards) {
        Map<Long, IScorer> identities = new TreeMap<>();
        Map<IScorer, Long> identityIds = new HashMap<>();
        ListTag<CompoundTag> objectives = new ListTag<>(Tag.TAG_Compound);

        for (IScoreboard scoreboard : scoreboards) {
            ListTag<CompoundTag> scores = new ListTag<>(Tag.TAG_Compound);

            for (IScoreboardLine line : scoreboard.getLines().values()) {
                long scoreboardId = line.getLineId();
                IScorer scorer = line.getScorer();

                IScorer existingIdentity = identities.putIfAbsent(scoreboardId, scorer);
                if (existingIdentity != null && !existingIdentity.equals(scorer)) {
                    throw new IllegalStateException("ScoreboardId " + scoreboardId + " has conflicting identities");
                }

                Long existingId = identityIds.putIfAbsent(scorer, scoreboardId);
                if (existingId != null && existingId != scoreboardId) {
                    throw new IllegalStateException("Scoreboard identity has conflicting IDs " + existingId + " and " + scoreboardId);
                }

                scores.add(new CompoundTag()
                        .putInt("Score", line.getScore())
                        .putLong("ScoreboardId", scoreboardId));
            }

            objectives.add(new CompoundTag()
                    .putString("Criteria", scoreboard.getCriteriaName())
                    .putString("DisplayName", scoreboard.getDisplayName())
                    .putString("Name", scoreboard.getObjectiveName())
                    .putList("Scores", scores));
        }

        ListTag<CompoundTag> entries = new ListTag<>(Tag.TAG_Compound);

        for (Map.Entry<Long, IScorer> identity : identities.entrySet()) {
            long scoreboardId = identity.getKey();
            IScorer scorer = identity.getValue();

            CompoundTag entry = new CompoundTag().putLong("ScoreboardId", scoreboardId);

            switch (scorer.getScorerType()) {
                case CHANGE_PLAYER -> entry
                        .putByte("IdentityType", IDENTITY_PLAYER)
                        .putLong("PlayerId", ((PlayerScorer) scorer).uniqueIdLong());
                case CHANGE_ENTITY -> entry
                        .putByte("IdentityType", IDENTITY_ENTITY)
                        .putLong("EntityID", ((EntityScorer) scorer).uniqueIdLong());
                case CHANGE_FAKE_PLAYER -> entry
                        .putByte("IdentityType", IDENTITY_FAKE)
                        .putString("FakePlayerName", ((FakeScorer) scorer).getFakeName());
                default -> throw new IllegalStateException("Unsupported scoreboard identity " + scorer.getScorerType());
            }

            entries.add(entry);
        }

        root.putList("Entries", entries);
        root.putList("Objectives", objectives);
    }

    private CompoundTag readRoot() {
        byte[] bytes = db.get(ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_KEY);
        if (bytes == null) {
            return emptyRoot();
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            return CompoundTag.fromNetwork((NbtMap) nbtInputStream.readTag());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read scoreboard data", e);
        }
    }

    private void writeRoot(CompoundTag root) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(root.toNetwork());
            nbtOutputStream.close();
            db.put(ServerDBStorageFormat.SERVER_DATA_SCOREBOARD_KEY, outputStream.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to write scoreboard data", e);
        }
    }

    private static CompoundTag emptyRoot() {
        return new CompoundTag()
                .putList("Criteria", new ListTag<>())
                .putList("DisplayObjectives", new ListTag<CompoundTag>(Tag.TAG_Compound))
                .putList("Entries", new ListTag<CompoundTag>(Tag.TAG_Compound))
                .putLong("LastUniqueID", 0)
                .putList("Objectives", new ListTag<CompoundTag>(Tag.TAG_Compound));
    }

    private static byte sortOrder(ObjectiveSortOrder sortOrder) {
        return (byte) (sortOrder == ObjectiveSortOrder.ASCENDING ? 1 : 0);
    }

    private static ObjectiveSortOrder readSortOrder(byte sortOrder) {
        return sortOrder == 1 ? ObjectiveSortOrder.ASCENDING : ObjectiveSortOrder.DESCENDING;
    }

    private static DisplaySlot displaySlot(String name) {
        for (DisplaySlot slot : DisplaySlot.values()) {
            if (slot.getSlotName().equals(name)) {
                return slot;
            }
        }

        throw new IllegalStateException("Invalid scoreboard display slot " + name);
    }
}
