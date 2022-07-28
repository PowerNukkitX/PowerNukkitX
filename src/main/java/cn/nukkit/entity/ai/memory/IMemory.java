package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nullable;

/**
 * 实体记忆对象，表示单个实体记忆数据
 * 可被存储到记忆存储器{@link IMemoryStorage}中
 * <p>
 * 注意，对于此接口的所有实现类，都必须有一个无参构造函数，返回一个代表此记忆的空记忆
 *
 * @param <T> 包含的数据类型
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IMemory<T> {

    /**
     * @return 此记忆中包含的数据
     */
    @Nullable
    T getData();

    void setData(@Nullable T data);

    default boolean hasData() {
        return getData() != null;
    }
}
