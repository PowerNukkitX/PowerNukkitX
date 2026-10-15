package org.powernukkitx.scheduler;

import org.powernukkitx.block.BlockState;

/**
 * Visits one retained scheduled block update without materializing a legacy update object.
 *
 * @author Curse
 */
@FunctionalInterface
public interface ScheduledBlockUpdateVisitor {

    /**
     * Visits one persisted scheduled block update.
     */
    void accept(int x, int y, int z, BlockState blockState, long targetTick);
}
