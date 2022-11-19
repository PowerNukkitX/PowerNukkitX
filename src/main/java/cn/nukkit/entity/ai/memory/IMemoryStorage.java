package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 记忆存储器
 */
@PowerNukkitXOnly
@Since("1.19.40-r4")
public interface IMemoryStorage {

    /**
     * 写入数据到记忆类型
     * @param type 记忆类型
     * @param data 数据
     * @param <D> 数据类型
     */
    <D> void put(MemoryType<D> type, D data);

    /**
     * 从指定记忆类型获取数据
     * @param type 记忆类型
     * @return 数据
     * @param <D> 数据类型
     *
     */
    <D> D get(MemoryType<D> type);

    /**
     * 清空指定记忆类型数据为null
     * @param type 记忆类型
     */
    void clear(MemoryType<?> type);

    /**
     * 检查指定记忆类型数据是否为空(null)
     * @param type 记忆类型
     * @return 是否为空
     */
    default boolean isEmpty(MemoryType<?> type){return get(type) == null;}

    /**
     * 检查指定记忆类型数据是否不为空(null)
     * @param type 记忆类型
     * @return 是否不为空
     */
    default boolean notEmpty(MemoryType<?> type){return get(type) != null;}

    /**
     * 使用指定的数据对比记忆类型存储的数据
     * @param type 记忆类型
     * @param to 指定的数据
     * @return 是否相同
     */
    default <D> boolean compareDataTo(MemoryType<D> type, Object to){
        D value;
        return (value = get(type)) != null ? value.equals(to) : to == null;
    }
}
