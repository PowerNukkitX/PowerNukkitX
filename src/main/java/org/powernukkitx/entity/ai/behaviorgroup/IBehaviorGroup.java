package org.powernukkitx.entity.ai.behaviorgroup;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behavior.IBehavior;
import org.powernukkitx.entity.ai.controller.IController;
import org.powernukkitx.entity.ai.memory.IMemoryStorage;
import org.powernukkitx.entity.ai.route.finder.IRouteFinder;
import org.powernukkitx.entity.ai.sensor.ISensor;

import java.util.Set;

/**
 * A Behavior Group is a basic, self-contained unit of AI<br>
 * that consists of several (core) Behaviors{@link IBehavior}, Controllers{@link IController}, Sensors{@link ISensor} and a Pathfinder{@link IRouteFinder} and memory {@link IMemoryStorage} are composed<br>
 * Note: Core behavior refers to the behavior that will not be affected by the priority of the behavior, and its activation status only depends on its own evaluator
 */


public interface IBehaviorGroup {

    /**
     * Call the evaluator {@link org.powernukkitx.entity.ai.evaluator.IBehaviorEvaluator} of all behavior {@link IBehavior} inside the behavior group
     *
     * @param entity the target entity object
     */
    void evaluateBehaviors(EntityIntelligent entity);

    /**
     * Call the evaluator {@link org.powernukkitx.entity.ai.evaluator.IBehaviorEvaluator} of all core behavior {@link IBehavior} inside the behavior group
     *
     * @param entity the target entity object
     */
    void evaluateCoreBehaviors(EntityIntelligent entity);

    /**
     * Call all sensors {@link ISensor} inside the behavior group, and write the memory data returned by the sensor to the memory storage {@link IMemoryStorage}
     *
     * @param entity the target entity object
     */
    void collectSensorData(EntityIntelligent entity);

    /**
     * Call the executor {@link org.powernukkitx.entity.ai.executor.IBehaviorExecutor} of all activated behavior {@link IBehavior} inside the behavior group
     *
     * @param entity the target entity object
     */
    void tickRunningBehaviors(EntityIntelligent entity);

    /**
     * Call the executor {@link org.powernukkitx.entity.ai.executor.IBehaviorExecutor} of all activated core behavior {@link IBehavior} inside the behavior group
     *
     * @param entity the target entity object
     */
    void tickRunningCoreBehaviors(EntityIntelligent entity);

    /**
     * Apply all controllers {@link IController} inside the behavior
     *
     * @param entity the target entity object
     */
    void applyController(EntityIntelligent entity);

    /**
     * @return Behaviors contained in Behavior Groups {@link IBehavior}
     */
    Set<IBehavior> getBehaviors();

    /**
     * @return Core Behaviors Contained by Behavior Groups {@link IBehavior}
     */
    Set<IBehavior> getCoreBehaviors();

    /**
     * @return Activated Behavior {@link IBehavior}
     */
    Set<IBehavior> getRunningBehaviors();

    /**
     * @return Activated Core Behavior {@link IBehavior}
     */
    Set<IBehavior> getRunningCoreBehaviors();

    /**
     * @return Behavior group includes sensors {@link ISensor}
     */
    Set<ISensor> getSensors();

    /**
     * @return Behavior group contains the controller {@link IController}
     */
    Set<IController> getControllers();

    /**
     * @return Routefinder used by behavior groups {@link IRouteFinder}
     */
    IRouteFinder getRouteFinder();

    /**
     * Update the path from the current position to the target position through the pathfinder used by the behavior group
     *
     * @param entity the target entity
     */
    void updateRoute(EntityIntelligent entity);

    /**
     * @return Behavior Group Memory Storage {@link IMemoryStorage}
     */
    IMemoryStorage getMemoryStorage();

    /**
     * @return Whether the next gt is forced to update the path
     */
    boolean isForceUpdateRoute();

    /**
     * Ask the next gt to update the path immediately
     *
     * @param forceUpdateRoute update the path immediately
     */
    void setForceUpdateRoute(boolean forceUpdateRoute);

    /**
     * When EntityAI.checkDebugOption(BEHAVIOR) == true, this method is called every 1gt to refresh the content related to debug mode
     */
    default void debugTick(EntityIntelligent entity) {
    }

    default void save(EntityIntelligent entity) {
        //EmptyBehaviorGroup will return null
        if (getMemoryStorage() != null)
            getMemoryStorage().encode();
    }
}
