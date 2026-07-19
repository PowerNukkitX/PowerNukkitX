package org.powernukkitx.entity;

/**
 * This method is invoked asynchronously in parallel, used for entity operations that are tick-independent.
 */


public interface EntityAsyncPrepare {
    /**
     * This method is executed in parallel, once every tick, and is guaranteed to finish before each onUpdate.
     *
     * @param currentTick the current game tick
     */
    void asyncPrepare(int currentTick);
}
