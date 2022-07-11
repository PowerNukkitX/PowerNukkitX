package cn.nukkit.entity.ai;

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
 * 行为组接口
 * 行为组是由记忆(MemoryStorage),传感器(Sensor)和行为(Behavior)组成的
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehaviorGroup {

    /**
     * 评估所有行为
     */
    void evaluateBehaviors(EntityIntelligent entity);

    /**
     * 从传感器获取数据
     */
    void collectSensorData(EntityIntelligent entity);

    /**
     * 激活需要被执行的行为
     */
    void tickRunningBehaviors(EntityIntelligent entity);

    /**
     * 应用控制器
     */
    void applyController(EntityIntelligent entity);

    void addBehavior(IBehavior behavior);

    Set<IBehavior> getBehaviors();

    Set<IBehavior> getRunningBehaviors();

    void addSensor(ISensor sensor);

    Set<ISensor> getSensors();

    void addController(IController controller);

    Set<IController> getControllers();

    IRouteFinder getRouteFinder();

    void updateRoute(EntityIntelligent entity);

    IMemoryStorage getMemory();
}
