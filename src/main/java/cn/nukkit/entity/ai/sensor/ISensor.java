package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;

/**
 * 传感器接口
 * 传感器用于搜集环境信息并写入MemoryStorage
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface ISensor {
    IMemory<?> sense(EntityIntelligent entity);

    /**
     * @return String
     * 返回此传感器的名称,默认返回类名称
     */
    default String getName() { return this.getClass().getSimpleName(); };

    default boolean equals(ISensor sensor) { return getName().equals(sensor.getName()); };

}
