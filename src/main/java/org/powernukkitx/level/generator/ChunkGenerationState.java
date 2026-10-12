package org.powernukkitx.level.generator;

/**
 * Represents transient chunk generation progress and its runtime ID.
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

    NEEDS_LIGHTING(8),
    LIGHTING(9),

    NEEDS_COMPLETION(10),
    COMPLETE(11);

    private final int nativeId;

    ChunkGenerationState(int nativeId) {
        this.nativeId = nativeId;
    }

    /**
     * Returns the runtime ID.
     *
     * @return runtime ID
     */
    public int getNativeId() {
        return nativeId;
    }

    /**
     * Returns whether generation work is active for this state.
     *
     * @return whether the state is active
     */
    public boolean isActive() {
        return switch (this) {
            case GENERATING, STRUCTURE_PP, POPULATING, CFRD, LIGHTING -> true;
            default -> false;
        };
    }
}
