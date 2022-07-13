package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;

import javax.annotation.Nullable;

/**
 * 传感器接口<br>
 * 传感器用于搜集环境信息并写入MemoryStorage
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface ISensor {

    /**
     * 要求传感器返回一个数据(Memory),若Memory的Data为Null则表示没有数据,MemoryStorage将会删除对应的键值对
     *
     * @param entity
     * @return IMemory
     */
    @Nullable
    IMemory<?> sense(EntityIntelligent entity);

    /**
     * 返回此传感器的名称,默认返回类名称
     *
     * @return String
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    default boolean equals(ISensor sensor) {
        return getName().equals(sensor.getName());
    }

}
