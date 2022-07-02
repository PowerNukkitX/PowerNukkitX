package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
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
     * 行为组处理循环
     * 调用后，行为组将会从传感器搜集信息，调用评估器等
     */
    void tick(EntityIntelligent entity);

    Set<IBehavior> getBehaviors();

    void addBehavior(IBehavior behavior);

    Set<IBehavior> getRunningBehaviors();

    Set<ISensor> getSensors();

    void addSensor(ISensor sensor);

    IMemoryStorage getMemory();
}
