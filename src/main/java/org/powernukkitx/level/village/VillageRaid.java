package org.powernukkitx.level.village;

import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.LongTag;

import java.util.List;

public record VillageRaid(long gameTick, byte groupNumber, byte numberOfGroups, byte numberOfRaiders,
                          List<Long> raiders, byte spawnFails, Vector3 spawnPosition, int state, int status,
                          long ticks, float totalMaxHealth) {
    public VillageRaid {
        raiders = List.copyOf(raiders);
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
