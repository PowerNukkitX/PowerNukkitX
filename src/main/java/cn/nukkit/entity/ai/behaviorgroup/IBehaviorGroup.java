package cn.nukkit.entity.ai.behaviorgroup;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.IBehavior;
import cn.nukkit.entity.ai.controller.IController;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.route.IRouteFinder;
import cn.nukkit.entity.ai.sensor.ISensor;

import java.util.Set;

/**
 * 行为组是一个基本的、独立的AI单元
 * 它由若干个行为{@link IBehavior}、控制器{@link IController}、传感器{@link ISensor}以及一个寻路器{@link IRouteFinder}和记忆存储器{@link IMemoryStorage}组成
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
     * 调用行为组内部的所有传感器{@link ISensor}，并将传感器返回的记忆{@link cn.nukkit.entity.ai.memory.IMemory}写入到记忆存储器中{@link IMemoryStorage}
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
     * 应用行为内部所有的控制器{@link IController}
     *
     * @param entity 目标实体对象
     */
    void applyController(EntityIntelligent entity);

    /**
     * 为行为组添加一个行为{@link IBehavior}
     *
     * @param behavior 要添加的行为
     */
    void addBehavior(IBehavior behavior);

    /**
     * @return 行为组包含的行为 {@link IBehavior}
     */
    Set<IBehavior> getBehaviors();

    /**
     * @return 被激活的行为 {@link IBehavior}
     */
    Set<IBehavior> getRunningBehaviors();

    /**
     * 为行为组添加一个传感器{@link ISensor}
     *
     * @param sensor 要添加的传感器
     */
    void addSensor(ISensor sensor);

    /**
     * @return 行为组包含的传感器 {@link ISensor}
     */
    Set<ISensor> getSensors();

    /**
     * 为行为组添加一个控制器{@link IController}
     *
     * @param controller 需要添加的控制器
     */
    void addController(IController controller);

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
}
