package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 记忆存储器的标准实现，每种记忆类型只能存一个记忆。
 * <p>
 * The standard implementation of {@link IMemoryStorage}, where only one memory can be stored for each memory class type.
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
@Log4j2
public class MemoryStorage implements IMemoryStorage {

    protected ConcurrentHashMap<Class<? extends IMemory<?>>, IMemory<?>> memoryMap = new ConcurrentHashMap<>();

    /**
     * 写入记忆到{@link #memoryMap}.
     * <p>
     * Write memory to {@link #memoryMap}.
     *
     * @param memory 记忆
     */
    @Override
    public void put(IMemory<?> memory) {
        this.memoryMap.put((Class<? extends IMemory<?>>) memory.getClass(), memory);
    }


    @Override
    public <T extends IMemory<?>> void clear(Class<T> memoryClazz) {
        get(memoryClazz).setData(null);
    }

    /**
     * 从记忆存储器中得到记忆，注意如果{@link #memoryMap}中不存在该记忆，那么会通过反射创建一个.
     * <p>
     * Get the memory from the memory store, note that if the memory does not exist in {@link #memoryMap}, then a reflection will create a instance.
     *
     * @param memoryClazz 记忆Class对象<br>memory Class objects
     */
    @Override
    public <T extends IMemory<?>, R extends Class<T>> T get(R memoryClazz) {
        T memory = (T) memoryMap.get(memoryClazz);
        if (memory == null) {
            try {
                memory = memoryClazz.getDeclaredConstructor().newInstance();
            } catch (Throwable e) {
                log.error("Failed to create memory instance, declared constructor not found!", e);
            }
            put(memory);
        }
        return memory;
    }

    @Override
    public <T extends IMemory<?>> boolean isEmpty(Class<T> memoryClazz) {
        return !get(memoryClazz).hasData();
    }

    @Override
    public <T extends IMemory<?>> boolean notEmpty(Class<T> memoryClazz) {
        return get(memoryClazz).hasData();
    }

    @Override
    public <R, T extends IMemory<R>> boolean checkData(Class<T> memoryClazz, R data) {
        T memory;
        return (memory = get(memoryClazz)).hasData() && memory.getData().equals(data);
    }

    public <R, T extends IMemory<R>> void setData(Class<T> memoryClazz, R data) {
        get(memoryClazz).setData(data);
    }

    @Override
    public <R, T extends IMemory<R>> R getData(Class<T> memoryClazz) {
        return get(memoryClazz).getData();
    }
}
