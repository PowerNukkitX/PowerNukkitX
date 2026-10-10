package org.powernukkitx.level.generator.object;

import com.google.common.base.Preconditions;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.LiquidUpdateAccess;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.generator.ChunkGenerationState;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.scheduler.GenerationBlockUpdateQueue;
import org.jetbrains.annotations.Nullable;

/**
 * Provides generator-local block access for scheduled liquid updates.
 *
 * @author Curse
 */
public final class GenerationLiquidUpdateAccess implements LiquidUpdateAccess {
    private final BlockManager root;
    private final GenerationBlockUpdateQueue updates;

    /**
     * Creates generation-local liquid update access.
     *
     * @param root generator-local block source
     * @param updates generation-owned scheduled update queue
     */
    public GenerationLiquidUpdateAccess(BlockManager root, GenerationBlockUpdateQueue updates) {
        this.root = Preconditions.checkNotNull(root);
        this.updates = Preconditions.checkNotNull(updates);
    }

    @Override
    public Server getServer() {
        return root.getLevel().getServer();
    }

    @Override
    public Block getBlock(int x, int y, int z, int layer) {
        return root.getBlockIfQueuedOrLoaded(x, y, z, layer);
    }

    @Override
    public boolean isAreaAvailable(int x, int z, int radius) {
        int minChunkX = (x - radius) >> 4;
        int maxChunkX = (x + radius) >> 4;
        int minChunkZ = (z - radius) >> 4;
        int maxChunkZ = (z + radius) >> 4;
        int requiredState = ChunkGenerationState.NEEDS_STRUCTURE_PP.getNativeId();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                var chunk = root.getLevel().getChunkIfLoaded(chunkX, chunkZ);
                if (chunk == null || chunk.getGenerationState().getNativeId() < requiredState) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean setBlock(Vector3 pos, int layer, Block block, boolean direct, boolean update) {
        root.setBlockStateAt(
                pos.getFloorX(),
                pos.getFloorY(),
                pos.getFloorZ(),
                layer,
                block.getBlockState()
        );
        return true;
    }

    @Override
    public void scheduleUpdate(Block block, int delay) {
        int x = block.getFloorX();
        int y = block.getFloorY();
        int z = block.getFloorZ();
        Block effective = root.getBlockIfQueuedOrLoaded(x, y, z, block.layer);

        if (effective.isAir()) {
            return;
        }

        // Native generation settlement runs with queue.currentTick == UINT64_MAX.
        long targetTick = Integer.toUnsignedLong(delay) - 1L;
        updates.add(
                effective.getBlockState(),
                x,
                y,
                z,
                effective.layer,
                targetTick,
                0
        );
    }

    @Override
    @Nullable
    public Item useBreakOn(Vector3 vector, @Nullable Item item) {
        return item;
    }

    @Override
    public void addSound(Vector3 pos, Sound sound) {
    }

    @Override
    public Block handleBlockFromTo(Block from, Block to) {
        return to;
    }

    @Override
    public boolean handleLiquidFlow(Block target, BlockLiquid source, int newFlowDecay) {
        return true;
    }
}
