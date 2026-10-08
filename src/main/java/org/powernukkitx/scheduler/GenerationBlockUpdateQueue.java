package org.powernukkitx.scheduler;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.LiquidUpdateAccess;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;

import com.google.common.base.Preconditions;
import java.util.ArrayList;

/**
 * Holds generation-owned scheduled block updates until their generation lifecycle is resolved.
 *
 * @author Curse
 */
public final class GenerationBlockUpdateQueue {
    private static final int MAX_GENERATION_SETTLEMENT_PASSES = 128;

    private final IChunk chunk;
    private ArrayList<Entry> entries = new ArrayList<>();

    public GenerationBlockUpdateQueue(IChunk chunk) {
        this.chunk = Preconditions.checkNotNull(chunk);
    }

    /**
     * Adds one layer-0 generation-owned scheduled update without insertion-time deduplication.
     */
    public synchronized void add(BlockState blockState, int x, int y, int z, long targetTick, int priority) {
        add(blockState, x, y, z, 0, targetTick, priority);
    }

    /**
     * Adds one generation-owned scheduled update without insertion-time deduplication.
     */
    public synchronized void add(
            BlockState blockState,
            int x,
            int y,
            int z,
            int layer,
            long targetTick,
            int priority
    ) {
        entries.add(new Entry(x, y, z, layer, priority, Preconditions.checkNotNull(blockState), targetTick));
    }

    /**
     * Settles generation-owned liquid updates using the native generation-pass bound.
     *
     * @param access generation-local block access
     */
    public synchronized void settleLiquids(LiquidUpdateAccess access) {
        Preconditions.checkNotNull(access);

        ArrayList<Entry> batch = entries;
        entries = new ArrayList<>();
        ArrayList<Entry> deferred = new ArrayList<>();

        for (int pass = 0; pass < MAX_GENERATION_SETTLEMENT_PASSES && batch.size() != 0; pass++) {
            for (Entry entry : batch) {
                String scheduledId = entry.blockState().getIdentifier();
                if (BlockID.AIR.equals(scheduledId)) {
                    continue;
                }

                if (!access.isAreaAvailable(entry.x(), entry.z(), 1)) {
                    if (access.isAreaAvailable(entry.x(), entry.z(), 0)) {
                        entries.add(entry.requeueNow());
                    }
                    continue;
                }

                Block current = access.getBlock(entry.x(), entry.y(), entry.z(), entry.layer());
                if (!scheduledId.equals(current.getId())) {
                    continue;
                }

                if (current instanceof BlockLiquid liquid) {
                    liquid.onScheduledUpdate(access);
                } else {
                    deferred.add(entry);
                }
            }

            batch.clear();
            if (pass + 1 < MAX_GENERATION_SETTLEMENT_PASSES && entries.size() != 0) {
                ArrayList<Entry> next = batch;
                batch = entries;
                entries = next;
            }
        }

        entries.addAll(deferred);
    }

    /**
     * Temporarily forwards generation records to the current runtime scheduler.
     */
    public synchronized void drainToRuntime(Level level) {
        BlockUpdateScheduler scheduler = chunk.getBlockUpdateScheduler();

        for (Entry entry : entries) {
            if (BlockID.AIR.equals(entry.blockState().getIdentifier())) {
                continue;
            }

            scheduler.add(
                    entry.blockState(),
                    entry.x(),
                    entry.y(),
                    entry.z(),
                    entry.layer(),
                    entry.targetTick(),
                    entry.priority(),
                    true
            );
        }

        entries.clear();
    }

    private record Entry(int x, int y, int z, int layer, int priority, BlockState blockState, long targetTick) {

        private Entry requeueNow() {
            return new Entry(x, y, z, layer, priority, blockState, -1L);
        }
    }
}
