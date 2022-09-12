package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 此接口抽象了一个记忆存储器 <br/>
 * 记忆存储器用于存储多个记忆单元{@link IMemory}
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IMemoryStorage {

    /**
     * 写入记忆
     *
     * @param memory 内存
     */
    void put(IMemory<?> memory);


    /**
     * 读取指定类型记忆
     *
     * @param memoryClazz 记忆类型
     * @return {@link T}
     */
    <T extends IMemory<?>, R extends Class<T>> T get(R memoryClazz);

    /**
     * 删除指定类型记忆
     *
     * @param memoryClazz 记忆类型
     */
    <T extends IMemory<?>> void clear(Class<T> memoryClazz);

    /**
     * @param memoryClazz 记忆类型
     * @return boolean 该记忆类型是否为空
     */
    <T extends IMemory<?>> boolean isEmpty(Class<T> memoryClazz);

    /**
     * @param memoryClazz 记忆类型
     * @return boolean 该记忆类型非空
     */
    <T extends IMemory<?>> boolean notEmpty(Class<T> memoryClazz);

    /**
     * 检查数据
     * 检查记忆中的数据是否与data相同
     * <p>
     * Check if the data in memoryClazz is the same as data
     *
     * @param memoryClazz 记忆类型
     * @param data        数据
     */
    <R, T extends IMemory<R>> boolean checkData(Class<T> memoryClazz, R data);

    /**
     * 设置指定记忆类型的记忆数据
     *
     * @param memoryClazz 记忆类型
     * @param data        数据
     */
    <R, T extends IMemory<R>> void setData(Class<T> memoryClazz, R data);

    /**
     * 获取指定记忆类型的记忆数据
     *
     * @param memoryClazz 记忆类型
     */
    <R, T extends IMemory<R>> R getData(Class<T> memoryClazz);
}
