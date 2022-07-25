package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 记忆存储器的标准实现
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
@Log4j2
public class MemoryStorage implements IMemoryStorage {

    protected ConcurrentHashMap<Class<? extends IMemory<?>>, IMemory<?>> memoryMap = new ConcurrentHashMap<>();

    @Override
    public void put(IMemory<?> memory) {
        this.memoryMap.put((Class<? extends IMemory<?>>) memory.getClass(), memory);
    }

    @Override
    public void clear(Class<? extends IMemory<?>> memoryClazz) {
        get(memoryClazz).setData(null);
    }

    @Override
    public <T extends IMemory<?>, R extends Class<T>> T get(R memoryClazz) {
        T memory = (T) memoryMap.get(memoryClazz);
        if (memory == null){
            try {
                memory = memoryClazz.getDeclaredConstructor().newInstance();
            }  catch (Throwable e) {
                log.error("Failed to create memory instance, declared constructor not found!", e);
            }
            put(memory);
        }
        return memory;
    }

    @Override
    public boolean isEmpty(Class<? extends IMemory<?>> memoryClazz) {
        return !get(memoryClazz).hasData();
    }

    @Override
    public boolean notEmpty(Class<? extends IMemory<?>> memoryClazz) {
        return get(memoryClazz).hasData();
    }

    @Override
    public <R, T extends IMemory<R>> boolean checkData(Class<T> memoryClazz, R data) {
        return get(memoryClazz).getData().equals(data);
    }

    public <R, T extends IMemory<R>> void setData(Class<T> memoryClazz, R data) {
        get(memoryClazz).setData(data);
    }

    @Override
    public <R, T extends IMemory<R>> R getData(Class<T> memoryClazz) {
        return get(memoryClazz).getData();
    }
}
