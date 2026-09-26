package org.powernukkitx.level.format;

import com.google.common.base.Preconditions;
import org.cloudburstmc.nbt.NbtMap;

/**
 * Runtime state of a chunk's LevelChunkMetaData dictionary reference.
 *
 * @author Curse
 */
public final class LevelChunkMetaData {
    public static final String NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION = "NeighborAwareBlockUpgradeVersion";
    public static final int NEIGHBOR_AWARE_BLOCK_UPGRADE_NONE = 0;

    /**
     * Milestone after the wall-block neighbor-aware upgrade.
     */
    public static final int WALL_BLOCK_UPGRADE_VERSION = 1;

    /**
     * Milestone after the fence neighbor-aware upgrade.
     */
    public static final int FENCE_BLOCK_UPGRADE_VERSION = 2;

    /**
     * Milestone after the stair neighbor-aware upgrade.
     */
    public static final int STAIR_BLOCK_UPGRADE_VERSION = 3;

    /**
     * Current neighbor-aware block upgrade version.
     */
    public static final int CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION = 4;

    /**
     * Historical baseline reconstructed for pre-1.26.50 PNX chunks.
     */
    public static final int LEGACY_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION = WALL_BLOCK_UPGRADE_VERSION;

    public enum State {
        UNINITIALIZED,
        MISSING,
        INVALID,
        UNRESOLVED,
        RESOLVED
    }

    private static final LevelChunkMetaData UNINITIALIZED = new LevelChunkMetaData(State.UNINITIALIZED, 0, null);
    private static final LevelChunkMetaData MISSING = new LevelChunkMetaData(State.MISSING, 0, null);
    private static final LevelChunkMetaData INVALID = new LevelChunkMetaData(State.INVALID, 0, null);

    private final State state;
    private final long persistedHash;
    private final NbtMap metadata;

    private LevelChunkMetaData(State state, long persistedHash, NbtMap metadata) {
        this.state = state;
        this.persistedHash = persistedHash;
        this.metadata = metadata;
    }

    /**
     * Returns metadata state for a newly created chunk which has not received storage metadata yet.
     */
    public static LevelChunkMetaData uninitialized() {
        return UNINITIALIZED;
    }

    /**
     * Returns metadata state for a loaded chunk without a 0x3F key.
     */
    public static LevelChunkMetaData missing() {
        return MISSING;
    }

    /**
     * Returns metadata state for a malformed 0x3F value.
     */
    public static LevelChunkMetaData invalid() {
        return INVALID;
    }

    /**
     * Returns metadata state for a valid hash whose dictionary entry is unavailable.
     */
    public static LevelChunkMetaData unresolved(long persistedHash) {
        return new LevelChunkMetaData(State.UNRESOLVED, persistedHash, null);
    }

    /**
     * Returns metadata state for a hash resolved through the global dictionary.
     */
    public static LevelChunkMetaData resolved(long persistedHash, NbtMap metadata) {
        return new LevelChunkMetaData(State.RESOLVED, persistedHash, Preconditions.checkNotNull(metadata));
    }

    /**
     * Returns the metadata reference state.
     */
    public State getState() {
        return state;
    }

    /**
     * Returns whether this state contains a persisted dictionary hash.
     */
    public boolean hasPersistedHash() {
        return state == State.UNRESOLVED || state == State.RESOLVED;
    }

    /**
     * Returns the persisted dictionary hash.
     */
    public long getPersistedHash() {
        Preconditions.checkState(hasPersistedHash(), "LevelChunkMetaData state has no persisted hash");
        return persistedHash;
    }

    /**
     * Returns the resolved metadata compound.
     */
    public NbtMap getMetadata() {
        Preconditions.checkState(state == State.RESOLVED, "LevelChunkMetaData is not resolved");
        return metadata;
    }
}
