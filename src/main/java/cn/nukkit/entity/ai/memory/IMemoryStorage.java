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
    void put(IMemory<?> memory);

    <T extends IMemory<?>, R extends Class<T>> T get(R memoryClazz);

    void clear(Class<? extends IMemory<?>> memoryClazz);

    boolean isEmpty(Class<? extends IMemory<?>> memoryClazz);

    boolean notEmpty(Class<? extends IMemory<?>> memoryClazz);

    <R,T extends IMemory<R>> boolean checkData(Class<T> memoryClazz,R data);

    <R, T extends IMemory<R>> void setData(Class<T> memoryClazz, R data);

    <R, T extends IMemory<R>> R getData(Class<T> memoryClazz);
}
