package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 实体记忆对象，表示单个实体记忆数据
 * 可被存储到记忆存储器{@link IMemoryStorage}中
 *
 * @param <T> 包含的数据类型
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IMemory<T> {
    /**
     * @return 此记忆中包含的数据
     */
    T getData();
}
