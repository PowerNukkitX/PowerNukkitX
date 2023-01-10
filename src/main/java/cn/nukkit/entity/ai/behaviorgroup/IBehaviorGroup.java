package cn.nukkit.entity.ai.behaviorgroup;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.IBehavior;
import cn.nukkit.entity.ai.controller.IController;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.route.finder.IRouteFinder;
import cn.nukkit.entity.ai.sensor.ISensor;

import java.util.Set;

/**
 * 行为组是一个基本的、独立的AI单元
 * 它由若干个（核心）行为{@link IBehavior}、控制器{@link IController}、传感器{@link ISensor}以及一个寻路器{@link IRouteFinder}和记忆存储器{@link IMemoryStorage}组成
 * 注：核心行为指的是不会被行为优先级影响的行为，其激活状态只取决于其自身的评估器
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehaviorGroup {

    /**
     * 调用行为组内部的所有行为{@link IBehavior}的评估器{@link cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator}
     *
     * @param entity 目标实体对象
     */
    void evaluateBehaviors(EntityIntelligent entity);

    /**
     * 调用行为组内部的所有核心行为{@link IBehavior}的评估器{@link cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator}
     *
     * @param entity 目标实体对象
     */
    void evaluateCoreBehaviors(EntityIntelligent entity);

    /**
     * 调用行为组内部的所有传感器{@link ISensor}，并将传感器返回的记忆数据写入到记忆存储器中{@link IMemoryStorage}
     *
     * @param entity 目标实体对象
     */
    void collectSensorData(EntityIntelligent entity);

    /**
     * 调用行为组内部所有被激活的行为{@link IBehavior}的执行器{@link cn.nukkit.entity.ai.executor.IBehaviorExecutor}
     *
     * @param entity 目标实体对象
     */
    void tickRunningBehaviors(EntityIntelligent entity);

    /**
     * 调用行为组内部所有被激活的核心行为{@link IBehavior}的执行器{@link cn.nukkit.entity.ai.executor.IBehaviorExecutor}
     *
     * @param entity 目标实体对象
     */
    void tickRunningCoreBehaviors(EntityIntelligent entity);

    /**
     * 应用行为内部所有的控制器{@link IController}
     *
     * @param entity 目标实体对象
     */
    void applyController(EntityIntelligent entity);

    /**
     * @return 行为组包含的行为 {@link IBehavior}
     */
    Set<IBehavior> getBehaviors();

    /**
     * @return 行为组包含的核心行为 {@link IBehavior}
     */
    Set<IBehavior> getCoreBehaviors();

    /**
     * @return 被激活的行为 {@link IBehavior}
     */
    Set<IBehavior> getRunningBehaviors();

    /**
     * @return 被激活的核心行为 {@link IBehavior}
     */
    Set<IBehavior> getRunningCoreBehaviors();

    /**
     * @return 行为组包含的传感器 {@link ISensor}
     */
    Set<ISensor> getSensors();

    /**
     * @return 行为组包含的控制器 {@link IController}
     */
    Set<IController> getControllers();

    /**
     * @return 行为组使用的寻路器 {@link IRouteFinder}
     */
    IRouteFinder getRouteFinder();

    /**
     * 通过行为组使用的寻路器更新当前位置到目标位置路径
     *
     * @param entity 目标实体
     */
    void updateRoute(EntityIntelligent entity);

    /**
     * @return 行为组的记忆存储器 {@link IMemoryStorage}
     */
    IMemoryStorage getMemoryStorage();

    /**
     * @return 下一gt是否强制更新路径
     */
    boolean isForceUpdateRoute();

    /**
     * 要求下一gt立即更新路径
     *
     * @param forceUpdateRoute 立即更新路径
     */
    void setForceUpdateRoute(boolean forceUpdateRoute);

    /**
     * 当 {@link cn.nukkit.entity.ai.EntityAI}.DEBUG == true 时此方法每1gt调用一次，用于debug模式相关内容的刷新
     */
    default void debugTick(EntityIntelligent entity){};
}
