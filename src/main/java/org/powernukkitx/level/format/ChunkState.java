package org.powernukkitx.level.format;

/**
 * Allay Project 2023/9/10
 *
 * @author daoge_cmd
 */
@Deprecated(since = "3.1.0", forRemoval = true)
public enum ChunkState {
    NEW,
    STARTED,
    GENERATED,
    POPULATED,
    FINISHED;

    public ChunkFinalizationState toFinalizationState() {
        return switch (this) {
            case NEW, STARTED -> ChunkFinalizationState.NEEDS_INSTATICKING;
            case GENERATED -> ChunkFinalizationState.NEEDS_POPULATION;
            case POPULATED, FINISHED -> ChunkFinalizationState.DONE;
        };
    }

    public static ChunkState fromFinalizationState(ChunkFinalizationState state) {
        return switch (state) {
            case NEEDS_INSTATICKING -> NEW;
            case NEEDS_POPULATION -> GENERATED;
            case DONE -> FINISHED;
        };
    }

    public boolean canSend() {
        return this.toFinalizationState() == ChunkFinalizationState.DONE;
    }
}
