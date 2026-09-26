package org.powernukkitx.level.lighting;

/**
 * Represents chunk lighting-engine progress.
 *
 * @author Curse
 */
public enum ChunkLightingState {
    NEEDS_LIGHTING(10),
    LIGHTING(11),
    LIGHTING_FINISHED(12),
    LOADED(13);

    private final int nativeId;

    ChunkLightingState(int nativeId) {
        this.nativeId = nativeId;
    }

    /**
     * Returns the lighting-state ID.
     *
     * @return lighting-state ID
     */
    public int getNativeId() {
        return nativeId;
    }

    /**
     * Resolves a lighting state from its ID.
     *
     * @param nativeId lighting-state ID
     * @return lighting state
     */
    public static ChunkLightingState fromNativeId(int nativeId) {
        return switch (nativeId) {
            case 10 -> NEEDS_LIGHTING;
            case 11 -> LIGHTING;
            case 12 -> LIGHTING_FINISHED;
            case 13 -> LOADED;
            default -> throw new IllegalArgumentException("Unknown chunk lighting state: " + nativeId);
        };
    }
}
