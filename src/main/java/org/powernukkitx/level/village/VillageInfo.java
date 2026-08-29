package org.powernukkitx.level.village;

import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;

public record VillageInfo(long breedingCooldownTime, long golemSpawnCooldownTime, boolean initialized,
                          long mergeTick, long playerDetectionTick, BlockVector3 raidBoundsMin,
                          BlockVector3 raidBoundsMax, long tick, byte version, BlockVector3 boundsMin,
                          BlockVector3 boundsMax) {

    public static VillageInfo fromCompound(CompoundTag tag) {
        return new VillageInfo(tag.getLong("BDTime"), tag.getLong("GDTime"), tag.getBoolean("Initialized"),
                tag.getLong("MTick"), tag.getLong("PDTick"),
                new BlockVector3(tag.getInt("RX0"), tag.getInt("RY0"), tag.getInt("RZ0")),
                new BlockVector3(tag.getInt("RX1"), tag.getInt("RY1"), tag.getInt("RZ1")),
                tag.getLong("Tick"), tag.getByte("Version"),
                new BlockVector3(tag.getInt("X0"), tag.getInt("Y0"), tag.getInt("Z0")),
                new BlockVector3(tag.getInt("X1"), tag.getInt("Y1"), tag.getInt("Z1")));
    }

    public CompoundTag toCompound() {
        return new CompoundTag()
                .putLong("BDTime", breedingCooldownTime).putLong("GDTime", golemSpawnCooldownTime)
                .putBoolean("Initialized", initialized).putLong("MTick", mergeTick)
                .putLong("PDTick", playerDetectionTick)
                .putInt("RX0", raidBoundsMin.x).putInt("RY0", raidBoundsMin.y).putInt("RZ0", raidBoundsMin.z)
                .putInt("RX1", raidBoundsMax.x).putInt("RY1", raidBoundsMax.y).putInt("RZ1", raidBoundsMax.z)
                .putLong("Tick", tick).putByte("Version", version)
                .putInt("X0", boundsMin.x).putInt("Y0", boundsMin.y).putInt("Z0", boundsMin.z)
                .putInt("X1", boundsMax.x).putInt("Y1", boundsMax.y).putInt("Z1", boundsMax.z);
    }
}
