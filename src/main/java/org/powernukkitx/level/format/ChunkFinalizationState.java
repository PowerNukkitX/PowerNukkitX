package org.powernukkitx.level.format;

/**
 * Represents the Bedrock chunk finalization state stored with a chunk. The values map between server names and storage
 * integer values.
 *
 * @author Curse
 */
public enum ChunkFinalizationState {
    NEEDS_INSTATICKING(0),
    NEEDS_POPULATION(1),
    DONE(2);

    private final int storageValue;

    ChunkFinalizationState(int storageValue) {
        this.storageValue = storageValue;
    }

    /**
     * Returns the value used in storage.
     * @return the requested value
     */
    public int getStorageValue() {
        return storageValue;
    }

    /**
     * Reads a state from its storage value.
     *
     * @param value value for this API
     * @return the requested value
     */
    public static ChunkFinalizationState fromStorageValue(int value) {
        return switch (value) {
            case 0 -> NEEDS_INSTATICKING;
            case 1 -> NEEDS_POPULATION;
            case 2 -> DONE;
            default -> DONE;
        };
    }
}
