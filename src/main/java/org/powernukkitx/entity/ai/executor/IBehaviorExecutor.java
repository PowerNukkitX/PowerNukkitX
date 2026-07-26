package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;

/**
 * Behavior executor<br>
 * Executes specific behavior on the entity<br>
 * For each instantiated entity, this object should only be instantiated once, and the entity will not change all the time
 */


public interface IBehaviorExecutor {

    /**
     * The scheduler will continue to execute this executor until it returns false or the executor is interrupted<br>
     * This method will be called every gt
     *
     * @param entity the target entity to execute on
     * @return boolean
     */
    boolean execute(EntityIntelligent entity);

    /**
     * Called when behavior breaks abnormally (e.g. overridden by higher-level behavior)
     *
     * @param entity the target entity
     */
    default void onInterrupt(EntityIntelligent entity) {
    }

    /**
     * After the behavior evaluation is successful, it is called before entering the active state
     *
     * @param entity the target entity
     */
    default void onStart(EntityIntelligent entity) {
    }

    /**
     * Called when the behavior ends normally (the execute() method returns false)
     *
     * @param entity the target entity
     */
    default void onStop(EntityIntelligent entity) {
    }
}
