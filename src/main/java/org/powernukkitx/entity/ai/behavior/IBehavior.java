package org.powernukkitx.entity.ai.behavior;

import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.evaluator.IBehaviorEvaluator;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;

/**
 * This interface abstracts a behavior object as part of the behavior group {@link IBehaviorGroup}.
 */


public interface IBehavior extends IBehaviorExecutor, IBehaviorEvaluator {

    /**
     * Returns the priority of this behavior, with higher-priority behaviors overriding lower-priority ones
     *
     * @return the priority
     */
    default int getPriority() {
        return 1;
    }

    /**
     * Returns the weight value of the behavior, with higher weights having a higher chance of being selected
     *
     * @return the weight value
     */
    default int getWeight() {
        return 1;
    }

    /**
     * Returns the refresh period for this behavior, a small refresh period will cause the evaluator to be called more often. Note that this method only affects evaluator calls, not executor calls.
     *
     * @return the refresh period
     */
    default int getPeriod() {
        return 1;
    }

    /**
     * @return the current state of this behavior
     */
    BehaviorState getBehaviorState();

    /**
     * Set the status of this behavior
     *
     * @param state the state
     */
    void setBehaviorState(BehaviorState state);
}
