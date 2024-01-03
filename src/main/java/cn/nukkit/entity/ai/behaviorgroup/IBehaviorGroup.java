package cn.nukkit.entity.ai.behaviorgroup;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.IBehavior;
import cn.nukkit.entity.ai.controller.IController;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.route.finder.IRouteFinder;
import cn.nukkit.entity.ai.sensor.ISensor;

import java.util.Set;

/**
 * 行为组是一个基本的、独立的AI单元<br>
 * 它由若干个（核心）行为{@link IBehavior}、控制器{@link IController}、传感器{@link ISensor}以及一个寻路器{@link IRouteFinder}和记忆存储器{@link IMemoryStorage}组成br>
 * 注：核心行为指的是不会被行为优先级影响的行为，其激活状态只取决于其自身的评估器br>
 * <p>
 * A Behavior Group is a basic, self-contained unit of AI<br>
 * that consists of several (core) Behaviors{@link IBehavior}, Controllers{@link IController}, Sensors{@link ISensor} and a Pathfinder{@link IRouteFinder} and memory {@link IMemoryStorage} are composed<br>
 * Note: Core behavior refers to the behavior that will not be affected by the priority of the behavior, and its activation status only depends on its own evaluator
 */


public interface IBehaviorGroup {

    /**
     * 调用行为组内部的所有行为{@link IBehavior}的评估器{@link cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator}
     * <p>
     * Call the evaluator {@link cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator} of all behavior {@link IBehavior} inside the behavior group
     *
     * @param entity 目标实体对象
     */
    void evaluateBehaviors(EntityIntelligent entity);

    /**
     * 调用行为组内部的所有核心行为{@link IBehavior}的评估器{@link cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator}
     * <p>
     * Call the evaluator {@link cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator} of all core behavior {@link IBehavior} inside the behavior group
     *
     * @param entity 目标实体对象
     */
    void evaluateCoreBehaviors(EntityIntelligent entity);

    /**
     * 调用行为组内部的所有传感器{@link ISensor}，并将传感器返回的记忆数据写入到记忆存储器中{@link IMemoryStorage}
     * <p>
     * Call all sensors {@link ISensor} inside the behavior group, and write the memory data returned by the sensor to the memory storage {@link IMemoryStorage}
     *
     * @param entity 目标实体对象
     */
    void collectSensorData(EntityIntelligent entity);

    /**
     * 调用行为组内部所有被激活的行为{@link IBehavior}的执行器{@link cn.nukkit.entity.ai.executor.IBehaviorExecutor}
     * <p>
     * Call the executor {@link cn.nukkit.entity.ai.executor.IBehaviorExecutor} of all activated behavior {@link IBehavior} inside the behavior group
     *
     * @param entity 目标实体对象
     */
    void tickRunningBehaviors(EntityIntelligent entity);

    /**
     * 调用行为组内部所有被激活的核心行为{@link IBehavior}的执行器{@link cn.nukkit.entity.ai.executor.IBehaviorExecutor}
     * <p>
     * Call the executor {@link cn.nukkit.entity.ai.executor.IBehaviorExecutor} of all activated core behavior {@link IBehavior} inside the behavior group
     *
     * @param entity 目标实体对象
     */
    void tickRunningCoreBehaviors(EntityIntelligent entity);

    /**
     * 应用行为内部所有的控制器{@link IController}
     * <p>
     * All controllers inside the application behavior{@link IController}
     *
     * @param entity 目标实体对象
     */
    void applyController(EntityIntelligent entity);

    /**
     * @return 行为组包含的行为 {@link IBehavior}<br>Behaviors contained in Behavior Groups {@link IBehavior}
     */
    Set<IBehavior> getBehaviors();

    /**
     * @return 行为组包含的核心行为 {@link IBehavior}<br>Core Behaviors Contained by Behavior Groups {@link IBehavior}
     */
    Set<IBehavior> getCoreBehaviors();

    /**
     * @return 被激活的行为 {@link IBehavior}<br>Activated Behavior {@link IBehavior}
     */
    Set<IBehavior> getRunningBehaviors();

    /**
     * @return 被激活的核心行为 {@link IBehavior}<br>Activated Core Behavior {@link IBehavior}
     */
    Set<IBehavior> getRunningCoreBehaviors();

    /**
     * @return 行为组包含的传感器 {@link ISensor}<br>Behavior group includes sensors {@link ISensor}
     */
    Set<ISensor> getSensors();

    /**
     * @return 行为组包含的控制器 {@link IController}<br>Behavior group contains the controller {@link IController}
     */
    Set<IController> getControllers();

    /**
     * @return 行为组使用的寻路器 {@link IRouteFinder}<br>Routefinder used by behavior groups {@link IRouteFinder}
     */
    IRouteFinder getRouteFinder();

    /**
     * 通过行为组使用的寻路器更新当前位置到目标位置路径
     * <p>
     * Update the path from the current position to the target position through the pathfinder used by the behavior group
     *
     * @param entity 目标实体
     */
    void updateRoute(EntityIntelligent entity);

    /**
     * @return 行为组的记忆存储器 {@link IMemoryStorage}<br>Behavior Group Memory Storage {@link IMemoryStorage}
     */
    IMemoryStorage getMemoryStorage();

    /**
     * @return 下一gt是否强制更新路径<br>Whether the next gt is forced to update the path
     */
    boolean isForceUpdateRoute();

    /**
     * 要求下一gt立即更新路径
     * <p>
     * Ask the next gt to update the path immediately
     *
     * @param forceUpdateRoute 立即更新路径
     */
    void setForceUpdateRoute(boolean forceUpdateRoute);

    /**
     * 当 EntityAI.checkDebugOption(BEHAVIOR) == true 时此方法每1gt调用一次，用于debug模式相关内容的刷新
     * <p>
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
