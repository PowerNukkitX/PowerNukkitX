package cn.nukkit.entity.ai.memory;

import cn.nukkit.entity.Entity;

import java.util.Map;

/**
 * 记忆存储器
 * <p>
 * memory storage
 */


public interface IMemoryStorage {

    /**
     * 写入数据到记忆类型
     * <p>
     * Write data to MemoryType
     *
     * @param type 记忆类型
     * @param data 数据
     * @param <D>  数据类型
     */
    <D> void put(MemoryType<D> type, D data);

    /**
     * 从指定记忆类型获取数据
     * <p>
     * Get data from the specified MemoryType
     *
     * @param type 记忆类型
     * @param <D>  数据类型
     * @return 数据
     */
    <D> D get(MemoryType<D> type);

    /**
     * 获取所有记忆
     * <p>
     * get all memories
     *
     * @return 所有记忆
     */
    Map<MemoryType<?>, ?> getAll();

    /**
     * 清空指定记忆类型数据为null
     * <p>
     * Clear the specified MemoryType data to null
     *
     * @param type 记忆类型
     */
    void clear(MemoryType<?> type);

    /**
     * 获取记忆存储所属的实体
     * <p>
     * Get the entity that the memory store belongs to
     *
     * @return 实体
     */

    Entity getEntity();

    /**
     * 检查指定记忆类型数据是否为空(null)
     * <p>
     * Check if the specified memory type data is empty (null)
     *
     * @param type 记忆类型
     * @return 是否为空
     */
    default boolean isEmpty(MemoryType<?> type) {
        return get(type) == null;
    }

    /**
     * 检查指定记忆类型数据是否不为空(null)
     * <p>
     * Check if the specified memory type data is not empty (null)
     *
     * @param type 记忆类型
     * @return 是否不为空
     */
    default boolean notEmpty(MemoryType<?> type) {
        return get(type) != null;
    }

    /**
     * 使用指定的数据对比记忆类型存储的数据
     * <p>
     * Use the specified data compare the data of memory type
     *
     * @param type 记忆类型
     * @param to   指定的数据
     * @return 是否相同
     */
    default <D> boolean compareDataTo(MemoryType<D> type, Object to) {
        D value;
        return (value = get(type)) != null ? value.equals(to) : to == null;
    }

    /**
     * 将此记忆存储器的数据编码进所属实体NBT(若MemoryType附加有编解码器)
     * <p>
     * Encode the data of this memory storage into the entity NBT (if there is a codec attached to the Memory Type)
     */
    default void encode() {
        var entity = getEntity();
        for (var memoryType : getAll().entrySet()) {
            memoryType.getKey().forceEncode(entity, memoryType.getValue());
        }
    }
}
