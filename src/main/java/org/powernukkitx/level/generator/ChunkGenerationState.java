package org.powernukkitx.level.generator;

import org.powernukkitx.level.format.ChunkFinalizationState;

/**
 * Represents the chunk generation state used by the server and native storage IDs.
 *
 * @author Curse
 */
public enum ChunkGenerationState {
    NEEDS_GENERATION(0),
    GENERATING(1),

    NEEDS_STRUCTURE_PP(2),
    STRUCTURE_PP(3),

    NEEDS_POPULATION(4),
    POPULATING(5),

    NEEDS_CFRD(6),
    CFRD(7),

    NEEDS_NEIGHBOR_UPGRADE(8),
    NEIGHBOR_UPGRADE(9),

    COMPLETE(10);

    private final int nativeId;

    ChunkGenerationState(int nativeId) {
        this.nativeId = nativeId;
    }

    /**
     * Returns the native ID.
     * @return the requested value
     */
    public int getNativeId() {
        return nativeId;
    }

    /**
     * Returns whether this state is active.
     * @return the requested value
     */
    public boolean isActive() {
        return switch (this) {
            case GENERATING,
                    STRUCTURE_PP,
                    POPULATING,
                    CFRD,
                    NEIGHBOR_UPGRADE -> true;

            default -> false;
        };
    }

    /**
     * Maps a finalization state to a generation state.
     *
     * @param finalizationState value for this API
     * @return the requested value
     */
    public static ChunkGenerationState fromFinalizationState(ChunkFinalizationState finalizationState) {
        return switch (finalizationState) {
            case NEEDS_INSTATICKING -> NEEDS_GENERATION;
            case NEEDS_POPULATION -> NEEDS_STRUCTURE_PP;
            case DONE -> COMPLETE;
        };
    }
}
