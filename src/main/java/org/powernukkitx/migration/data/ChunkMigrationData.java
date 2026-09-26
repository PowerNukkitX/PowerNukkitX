package org.powernukkitx.migration.data;

import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.List;

/**
 * Carries chunk storage data through versioned migration steps, including legacy tick and BiomeState storage.
 *
 * @author Curse
 */
public record ChunkMigrationData(
        int chunkX,
        int chunkZ,
        CompoundTag extraData,
        List<ScheduledTick> scheduledTicks,
        boolean pendingTicksPresent,
        CompoundTag pendingTicks,
        byte[] biomeState
) {
    /**
     * Represents one scheduled block tick carried through chunk migration.
     *
     * @author Curse
     */
    public record ScheduledTick(int x, int y, int z, long delay, CompoundTag blockState) {
    }
}
