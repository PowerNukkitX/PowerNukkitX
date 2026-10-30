package org.powernukkitx.level.structure;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFence;
import org.powernukkitx.block.BlockStairs;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockThin;
import org.powernukkitx.block.StateDeriving;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.IntArrayTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.Registries;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies contextual structure states after destination blocks and neighboring chunks are available.
 *
 * @author Curse
 */
public final class StructureNeighborUpdates {
    private static final String PENDING = "pendingStructureNeighborUpdates";

    private StructureNeighborUpdates() {
    }

    /**
     * Returns whether the block uses supported contextual structure states.
     */
    public static boolean isEligible(BlockState state) {
        Block block = state.toBlock();
        return block instanceof BlockStairs || block instanceof BlockFence || block instanceof BlockThin;
    }

    /**
     * Applies one committed update or retains it until its neighborhood becomes available.
     */
    public static void processOrQueue(Level level, IChunk chunk, int[] update) {
        if (!tryApply(level, update)) defer(chunk, List.of(update));
    }

    /**
     * Retains contextual updates in the target chunk.
     */
    public static void defer(IChunk chunk, List<int[]> updates) {
        if (updates.size() == 0) return;

        synchronized (chunk) {
            var extraData = chunk.getExtraData();
            ListTag<IntArrayTag> pending = extraData.containsList(PENDING, Tag.TAG_Int_Array)
                    ? extraData.getList(PENDING, IntArrayTag.class)
                    : new ListTag<>(Tag.TAG_Int_Array);

            for (int[] update : updates) {
                pending.add(new IntArrayTag(update.clone()));
            }

            extraData.putList(PENDING, pending);
            chunk.setChanged();
        }
    }

    /**
     * Retries contextual updates in the neighborhood of an available chunk.
     */
    public static void retryAround(Level level, int chunkX, int chunkZ) {
        for (int cx = chunkX - 1; cx <= chunkX + 1; cx++) {
            for (int cz = chunkZ - 1; cz <= chunkZ + 1; cz++) {
                IChunk chunk = level.getChunkIfLoaded(cx, cz);
                if (chunk == null) continue;

                List<IntArrayTag> snapshot;
                synchronized (chunk) {
                    var extraData = chunk.getExtraData();
                    if (!extraData.containsList(PENDING, Tag.TAG_Int_Array)) continue;
                    snapshot = extraData.getList(PENDING, IntArrayTag.class).getAll();
                }

                List<IntArrayTag> completed = new ArrayList<>();
                for (IntArrayTag entry : snapshot) {
                    int[] update = entry.getData();
                    if (update.length != 4 || tryApply(level, update)) completed.add(entry);
                }

                if (completed.size() == 0) continue;

                synchronized (chunk) {
                    var extraData = chunk.getExtraData();
                    if (!extraData.containsList(PENDING, Tag.TAG_Int_Array)) continue;

                    ListTag<IntArrayTag> pending = extraData.getList(PENDING, IntArrayTag.class);
                    for (IntArrayTag entry : completed) pending.remove(entry);

                    if (pending.size() == 0) extraData.remove(PENDING);
                    chunk.setChanged();
                }
            }
        }
    }

    private static boolean tryApply(Level level, int[] update) {
        int x = update[0], y = update[1], z = update[2];
        if (!level.isYInRange(y)) return true;

        int chunkX = x >> 4, chunkZ = z >> 4;
        IChunk target = level.getChunkIfLoaded(chunkX, chunkZ);
        if (target == null || !target.isInitiated()
                || target.getFinalizationState() != ChunkFinalizationState.DONE) return false;

        int localX = x & 15, localZ = z & 15;
        if (localX == 0 && !isReady(level, chunkX - 1, chunkZ)) return false;
        if (localX == 15 && !isReady(level, chunkX + 1, chunkZ)) return false;
        if (localZ == 0 && !isReady(level, chunkX, chunkZ - 1)) return false;
        if (localZ == 15 && !isReady(level, chunkX, chunkZ + 1)) return false;

        BlockState expected = Registries.BLOCKSTATE.get(update[3]);
        if (expected == null) return true;

        BlockState current = target.getBlockState(x & 15, y, z & 15, 0);
        if (current == null || !current.getIdentifier().equals(expected.getIdentifier())) return true;

        Block block = Block.get(current, level, x, y, z, 0);
        if (!(block instanceof StateDeriving deriving) || !isEligible(current)) return true;
        if (!deriving.autoConfigureState()) return true;

        return level.setBlock(x, y, z, 0, block, false, false);
    }

    private static boolean isReady(Level level, int chunkX, int chunkZ) {
        IChunk chunk = level.getChunkIfLoaded(chunkX, chunkZ);
        return chunk != null && chunk.isInitiated()
                && chunk.getFinalizationState() == ChunkFinalizationState.DONE;
    }
}
