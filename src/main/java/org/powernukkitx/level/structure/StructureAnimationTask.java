package org.powernukkitx.level.structure;

import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.scheduler.Task;

/**
 * Advances one Structure Block LOAD animation on its owning level's tick scheduler.
 * Work counts include skipped template positions.
 *
 * @author Curse
 */
public final class StructureAnimationTask extends Task {
    private final Structure structure;
    private final Position destination;
    private final Level level;
    private final boolean includeEntities;
    private final int mode;
    private final int durationTicks;
    private final int plane;
    private final int total;
    private final long startTick;
    private final String sourceName;
    private final long sourceRevision;
    private int cursor;

    /** Creates a tick job for a valid nonzero-duration animation. */
    public StructureAnimationTask(Structure structure, Position destination, boolean includeEntities, int mode,
                                  int durationTicks, String sourceName, long sourceRevision) {
        this.structure = structure;
        this.destination = destination;
        this.level = destination.getLevel();
        this.includeEntities = includeEntities;
        this.mode = mode;
        this.durationTicks = durationTicks;
        this.plane = Math.multiplyExact(structure.getSizeX(), structure.getSizeZ());
        this.total = Math.multiplyExact(plane, structure.getSizeY());
        this.startTick = level.getCurrentTick();
        this.sourceName = sourceName;
        this.sourceRevision = sourceRevision;
    }

    @Override
    public void onRun(int currentTick) {
        if (level.isClosing() || level.getProvider() == null || StructureAPI.getTemplateRevision(sourceName) != sourceRevision) {
            cancel();
            return;
        }
        if (total == 0) {
            complete();
            return;
        }

        long elapsed = Math.max(0, level.getCurrentTick() - startTick);
        int target = (int) (Math.min(elapsed, (long) durationTicks) * total / durationTicks);
        if (mode == 1) target -= target % plane;
        if (target <= cursor) return;

        BlockState[][][][] states = structure.getBlockStates();
        for (int index = cursor; index < target; index++) {
            int ly = index / plane;
            int lx = (index % plane) / structure.getSizeZ();
            int lz = index % structure.getSizeZ();
            BlockState state = states[0][lx][ly][lz];
            if (state == null || state == AbstractStructure.STATE_STRUCTURE_VOID) continue;

            int chunkX = (destination.getFloorX() + lx) >> 4;
            int chunkZ = (destination.getFloorZ() + lz) >> 4;
            IChunk chunk = level.getChunkIfLoaded(chunkX, chunkZ);
            if (!(chunk instanceof Chunk concreteChunk)
                    || !concreteChunk.isAvailable() || !concreteChunk.isGenerationComplete()) {
                if (!level.isChunkGenerating(chunkX, chunkZ)) level.generateChunk(chunkX, chunkZ);
                target = index;
                break;
            }
        }

        if (target > cursor) {
            try {
                structure.placeSegment(destination, cursor, target);
            } catch (RuntimeException e) {
                cancel();
                throw e;
            }
            cursor = target;
        }
        if (cursor == total) complete();
    }

    private void complete() {
        try {
            if (includeEntities) structure.placeEntities(destination);
        } finally {
            cancel();
        }
    }
}
