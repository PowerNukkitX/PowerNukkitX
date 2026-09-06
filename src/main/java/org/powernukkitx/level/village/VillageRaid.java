package org.powernukkitx.level.village;

import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.LongTag;

import java.util.List;

public record VillageRaid(long gameTick, byte groupNumber, byte numberOfGroups, byte numberOfRaiders,
                          List<Long> raiders, byte spawnFails, Vector3 spawnPosition, int state, int status,
                          long ticks, float totalMaxHealth) {
    public static final int STATE_PREPARATION = 0;
    public static final int STATE_PICKING_SPAWN_POINT = 1;
    public static final int STATE_SPAWNING_GROUP = 2;
    public static final int STATE_GROUP_IN_PLAY = 3;
    public static final int STATE_AWARDING_REWARDS = 4;
    public static final int STATE_FINISHED = 5;

    public static final int STATUS_ONGOING = 0;
    public static final int STATUS_VICTORY = 1;
    public static final int STATUS_LOSS = 2;
    public static final int STATUS_STOPPED = 3;

    public VillageRaid {
        raiders = List.copyOf(raiders);
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
        return switch (state) {
            case STATE_PREPARATION -> Math.clamp(ticks / (float) VillageManager.RAID_PREPARATION_TIME, 0f, 1f);
            case STATE_PICKING_SPAWN_POINT, STATE_SPAWNING_GROUP -> 1f;
            case STATE_GROUP_IN_PLAY -> numberOfRaiders <= 0
                    ? 0f
                    : Math.clamp(raiders.size() / (float) numberOfRaiders, 0f, 1f);
            default -> 0f;
        };
    }

    public VillageRaid withState(int state) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails,
                spawnPosition, state, status, 0L, totalMaxHealth);
    }

    public VillageRaid withStatus(int status) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails,
                spawnPosition, STATE_FINISHED, status, ticks, totalMaxHealth);
    }

    public VillageRaid withTicks(long ticks) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails,
                spawnPosition, state, status, ticks, totalMaxHealth);
    }

    public VillageRaid withRaiders(List<Long> raiders) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, spawnFails,
                spawnPosition, state, status, ticks, totalMaxHealth);
    }

    /**
     * @param spawnFails how many times in a row no room was found for the current group
     */
    public VillageRaid withSpawnPoint(Vector3 spawnPosition, int spawnFails) {
        return new VillageRaid(gameTick, groupNumber, numberOfGroups, numberOfRaiders, raiders, (byte) spawnFails,
                spawnPosition, state, status, ticks, totalMaxHealth);
    }

    /**
     * Records a group that has just been placed, and moves the raid on to watching it.
     */
    public VillageRaid withGroup(int groupNumber, List<Long> raiders, float totalMaxHealth) {
        return new VillageRaid(gameTick, (byte) groupNumber, numberOfGroups, (byte) raiders.size(), raiders,
                (byte) 0, spawnPosition, STATE_GROUP_IN_PLAY, status, 0L, totalMaxHealth);
    }

    public static VillageRaid fromCompound(CompoundTag root) {
        CompoundTag tag = root.getCompound("Raid");
        return new VillageRaid(tag.getLong("GameTick"), tag.getByte("GroupNum"), tag.getByte("NumGroups"),
                tag.getByte("NumRaiders"), tag.getList("Raiders", LongTag.class).getAll().stream()
                .map(LongTag::getData).toList(), tag.getByte("SpawnFails"),
                new Vector3(tag.getFloat("SpawnX"), tag.getFloat("SpawnY"), tag.getFloat("SpawnZ")),
                tag.getInt("State"), tag.getInt("Status"), tag.getLong("Ticks"), tag.getFloat("TotalMaxHealth"));
    }

    public CompoundTag toCompound() {
        ListTag<LongTag> raidersTag = new ListTag<>();
        raiders.forEach(id -> raidersTag.add(new LongTag(id)));
        CompoundTag raid = new CompoundTag().putLong("GameTick", gameTick).putByte("GroupNum", groupNumber)
                .putByte("NumGroups", numberOfGroups).putByte("NumRaiders", numberOfRaiders)
                .putList("Raiders", raidersTag).putByte("SpawnFails", spawnFails)
                .putFloat("SpawnX", (float) spawnPosition.x).putFloat("SpawnY", (float) spawnPosition.y)
                .putFloat("SpawnZ", (float) spawnPosition.z).putInt("State", state).putInt("Status", status)
                .putLong("Ticks", ticks).putFloat("TotalMaxHealth", totalMaxHealth);
        return new CompoundTag().putCompound("Raid", raid);
    }
}
