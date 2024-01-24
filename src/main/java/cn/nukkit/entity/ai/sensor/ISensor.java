package cn.nukkit.entity.ai.sensor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemoryStorage;

/**
 * 此接口抽象了一个传感器 <br/>
 * 传感器用于搜集环境信息并向记忆存储器{@link IMemoryStorage}写入一个记忆{@link cn.nukkit.entity.ai.memory.MemoryType}
 * <p>
 * This interface abstracts a sensor<br>
 * The sensor is used to collect environmental information and write a memory {@link cn.nukkit.entity.ai.memory.MemoryType} to the memory storage {@link IMemoryStorage}
 */


public interface ISensor {

    /**
     * @param entity 目标实体
     */
    void sense(EntityIntelligent entity);

    /**
     * 返回此传感器的刷新周期，小的刷新周期会使得传感器被更频繁的调用
     * <p>
     * Returns the refresh period of this sensor, a small refresh period will make the sensor be called more frequently
     *
     * @return 刷新周期
     */
    default int getPeriod() {
        return 1;
    }
}
