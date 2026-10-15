package org.powernukkitx.scheduler;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.BlockUpdateEntry;

/**
 * Compact retained representation of one scheduled block update.
 *
 * @author Curse
 */
final class ScheduledBlockUpdate implements Comparable<ScheduledBlockUpdate> {
    final int x;
    final int y;
    final int z;
    final int layer;
    final int order;
    final long targetTick;
    final BlockState blockState;
    final boolean checkBlockWhenUpdate;

    ScheduledBlockUpdate(
            int x,
            int y,
            int z,
            int layer,
            int order,
            long targetTick,
            BlockState blockState,
            boolean checkBlockWhenUpdate
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.layer = layer;
        this.order = order;
        this.targetTick = targetTick;
        this.blockState = blockState;
        this.checkBlockWhenUpdate = checkBlockWhenUpdate;
    }

    static ScheduledBlockUpdate from(
            Block block,
            Vector3 pos,
            long targetTick,
            int order,
            boolean checkBlockWhenUpdate
    ) {
        return new ScheduledBlockUpdate(
                pos.getFloorX(),
                pos.getFloorY(),
                pos.getFloorZ(),
                block.layer,
                order,
                targetTick,
                block.getBlockState(),
                checkBlockWhenUpdate
        );
    }

    static ScheduledBlockUpdate from(BlockUpdateEntry entry, long targetTick) {
        return from(entry.block, entry.pos, targetTick, entry.priority, entry.checkBlockWhenUpdate);
    }

    boolean matches(Vector3 pos, Block block) {
        return layer == block.layer
                && x == pos.getFloorX()
                && y == pos.getFloorY()
                && z == pos.getFloorZ()
                && blockState.getIdentifier().equals(block.getId());
    }

    BlockUpdateEntry toLegacyEntry(Level level) {
        Block block = Block.get(blockState, level, x, y, z, layer);
        return new BlockUpdateEntry(new Vector3(x, y, z), block, targetTick, order, checkBlockWhenUpdate);
    }

    @Override
    public int compareTo(ScheduledBlockUpdate other) {
        int tickComparison = Long.compareUnsigned(targetTick, other.targetTick);
        return tickComparison != 0 ? tickComparison : Integer.compare(order, other.order);
    }
}
