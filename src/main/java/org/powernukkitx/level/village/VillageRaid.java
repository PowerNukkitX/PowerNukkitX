package org.powernukkitx.level.village;

import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.LongTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Raid state with health snapshots shared by successive states and persisted with the raider IDs.
 *
 * @param groupNumber zero-based index of the current or next group; equals the group count after the last group
 * @param raiderHealth last known health by persistent actor ID; must not be null;
 *                     shared across states when backed by a {@link ConcurrentHashMap}, otherwise copied
 */
public record VillageRaid(long gameTick, byte groupNumber, byte numberOfGroups, byte numberOfRaiders,
                          List<Long> raiders, byte spawnFails, Vector3 spawnPosition, int state, int status,
                          long ticks, float totalMaxHealth, @NotNull Map<Long, Float> raiderHealth) {
    public static final int STATE_PREPARATION = 0;
    public static final int STATE_PICKING_SPAWN_POINT = 1;
    public static final int STATE_SPAWNING_GROUP = 2;
    public static final int STATE_GROUP_IN_PLAY = 3;
    public static final int STATE_AWARDING_REWARDS = 4;
    public static final int STATE_CELEBRATING = 5;
    public static final int STATE_FINISHED = 6;

    public static final int STATUS_ONGOING = 0;
    public static final int STATUS_VICTORY = 1;
    public static final int STATUS_LOSS = 2;
    public static final int STATUS_STOPPED = 3;

    public VillageRaid {
        raiders = List.copyOf(raiders);
        if (!(raiderHealth instanceof ConcurrentHashMap)) raiderHealth = new ConcurrentHashMap<>(raiderHealth);
    }

    /**
     * Creates a raid without saved individual health, for callers using the original constructor.
     */
    @SuppressWarnings("PMD.ExcessiveParameterList") // Retains the original public constructor signature.
    public VillageRaid(long gameTick, byte groupNumber, byte numberOfGroups, byte numberOfRaiders,
                       List<Long> raiders, byte spawnFails, Vector3 spawnPosition, int state, int status,
                       long ticks, float totalMaxHealth) {
        this(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails, spawnPosition,
                state, status, ticks, totalMaxHealth, new ConcurrentHashMap<>());
    }

    /**
     * Creates the raid a village starts with, before its first group has been placed.
     *
     * @param gameTick      the tick the raid started on
     * @param groupCount    how many groups the difficulty calls for
     * @param spawnPosition the village center, until a spawn point is picked
     */
    public static VillageRaid starting(long gameTick, int groupCount, Vector3 spawnPosition) {
        return new VillageRaid(gameTick, (byte) 0, (byte) groupCount, (byte) 0, List.of(), (byte) 0,
                spawnPosition, STATE_PREPARATION, STATUS_ONGOING, 0L, 0f);
    }

    public boolean isFinished() {
        return state == STATE_FINISHED;
    }

    /**
     * @return how full the boss bar is, from 0 to 1: the preparation running out, then the share of
     * the current group still standing
     */
    public float bossBarProgress() {
        if (state == STATE_GROUP_IN_PLAY) {
            return numberOfRaiders <= 0 ? 0f : Math.clamp(raiders.size() / (float) numberOfRaiders, 0f, 1f);
        }
        return bossBarProgress(0f);
    }

    /**
     * @param currentHealth total current health of the surviving raiders
     * @return preparation progress or the surviving health divided by the group maximum, clamped to [0, 1]
     */
    public float bossBarProgress(float currentHealth) {
        return switch (state) {
            case STATE_PREPARATION -> Math.clamp(ticks / (float) VillageManager.RAID_PREPARATION_TIME, 0f, 1f);
            case STATE_PICKING_SPAWN_POINT, STATE_SPAWNING_GROUP -> 1f;
            case STATE_GROUP_IN_PLAY -> totalMaxHealth <= 0
                    ? 0f
                    : Math.clamp(currentHealth / totalMaxHealth, 0f, 1f);
            default -> 0f;
        };
    }

    public VillageRaid withState(int state) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails,
                spawnPosition, state, status, 0L, totalMaxHealth, raiderHealth);
    }

    /**
     * Enters the Bedrock result phase for victory or loss, or finishes immediately for other statuses.
     * The new phase starts at tick zero.
     *
     * @param status result of the raid
     * @return the raid in its result or finished phase
     */
    public VillageRaid withStatus(int status) {
        int nextState = status == STATUS_VICTORY || status == STATUS_LOSS ? STATE_CELEBRATING : STATE_FINISHED;
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails,
                spawnPosition, nextState, status, 0L, totalMaxHealth, raiderHealth);
    }

    public VillageRaid withTicks(long ticks) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails,
                spawnPosition, state, status, ticks, totalMaxHealth, raiderHealth);
    }

    public VillageRaid withRaiders(List<Long> raiders) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails,
                spawnPosition, state, status, ticks, totalMaxHealth, raiderHealth);
    }

    /**
     * @param spawnFails how many times in a row no room was found for the current group
     */
    public VillageRaid withSpawnPoint(Vector3 spawnPosition, int spawnFails) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, (byte) spawnFails,
                spawnPosition, state, status, ticks, totalMaxHealth, raiderHealth);
    }

    /**
     * Records a group that has just been placed, and moves the raid on to watching it.
     *
     * @param groupNumber zero-based index of the spawned group
     * @param raiders persistent IDs of the spawned actors
     * @param totalMaxHealth sum of the spawned actors' maximum health
     * @return the raid watching the given group
     */
    public VillageRaid withGroup(int groupNumber, List<Long> raiders, float totalMaxHealth) {
        return new VillageRaid(gameTick, (byte) groupNumber, numberOfGroups, (byte) raiders.size(), raiders,
                (byte) 0, spawnPosition, STATE_GROUP_IN_PLAY, status, 0L, totalMaxHealth);
    }

    /**
     * Advances past a cleared group, using Bedrock's zero-based group numbering.
     * Called after the completion delay on the level's tick thread.
     *
     * @return preparation for the next group, or the reward phase after the final group
     */
    public VillageRaid completeGroup() {
        int nextGroup = groupNumber + 1;
        return new VillageRaid(gameTick, (byte) nextGroup, numberOfGroups, numberOfRaiders, List.of(), spawnFails,
                spawnPosition, nextGroup < numberOfGroups ? STATE_PREPARATION : STATE_AWARDING_REWARDS,
                status, 0L, totalMaxHealth, raiderHealth);
    }

    public static VillageRaid fromCompound(CompoundTag root) {
        CompoundTag tag = root.getCompound("Raid");
        return new VillageRaid(tag.getLong("GameTick"), tag.getByte("GroupNum"), tag.getByte("NumGroups"),
                tag.getByte("NumRaiders"), tag.getList("Raiders", LongTag.class).getAll().stream()
                .map(LongTag::getData).toList(), tag.getByte("SpawnFails"),
                new Vector3(tag.getFloat("SpawnX"), tag.getFloat("SpawnY"), tag.getFloat("SpawnZ")),
                tag.getInt("State"), tag.getInt("Status"), tag.getLong("Ticks"), tag.getFloat("TotalMaxHealth"), readRaiderHealth(tag));
    }

    private static Map<Long, Float> readRaiderHealth(CompoundTag tag) {
        Map<Long, Float> health = new ConcurrentHashMap<>();
        for (CompoundTag entry : tag.getList("RaiderHealth", CompoundTag.class).getAll()) {
            health.put(entry.getLong("ID"), entry.getFloat("Health"));
        }
        return health;
    }

    public CompoundTag toCompound() {
        ListTag<LongTag> raidersTag = new ListTag<>();
        raiders.forEach(id -> raidersTag.add(new LongTag(id)));
        ListTag<CompoundTag> healthTag = new ListTag<>();
        for (long id : raiders) {
            Float health = raiderHealth.get(id);
            if (health != null) healthTag.add(new CompoundTag().putLong("ID", id).putFloat("Health", health));
        }
        CompoundTag raid = new CompoundTag().putLong("GameTick", gameTick).putByte("GroupNum", groupNumber)
                .putByte("NumGroups", numberOfGroups).putByte("NumRaiders", numberOfRaiders)
                .putList("Raiders", raidersTag).putByte("SpawnFails", spawnFails)
                .putFloat("SpawnX", (float) spawnPosition.x).putFloat("SpawnY", (float) spawnPosition.y)
                .putFloat("SpawnZ", (float) spawnPosition.z).putInt("State", state).putInt("Status", status)
                .putLong("Ticks", ticks).putFloat("TotalMaxHealth", totalMaxHealth).putList("RaiderHealth", healthTag);
        return new CompoundTag().putCompound("Raid", raid);
    }
}
