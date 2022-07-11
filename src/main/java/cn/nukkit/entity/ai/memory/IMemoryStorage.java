package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 实体记忆存储器接口
 * 存储从传感器搜集来的数据
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IMemoryStorage {
    void put(IMemory<?> memory);
    IMemory<?> get(Class<?> memoryClazz);
    void remove(Class<?> memoryClazz);

    boolean contains(Class<?> memoryClazz);
}
