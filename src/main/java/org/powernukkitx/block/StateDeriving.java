package org.powernukkitx.block;

/**
 * Implemented by blocks that keep part of their state in step with the blocks around them, such as
 * the connections of a fence or the corner of a staircase.
 * <p>
 * Worlds saved before those states existed hold the default in their place, so the level recomputes
 * them once the chunk and all of its neighbours are loaded.
 *
 * @author xRookieFight
 * @since 17/09/2026
 */
public interface StateDeriving {

    /**
     * Rewrites the derived part of the block state from its surroundings.
     *
     * @return whether anything changed, so the caller only writes the block back when it has to
     */
    boolean autoConfigureState();
}
