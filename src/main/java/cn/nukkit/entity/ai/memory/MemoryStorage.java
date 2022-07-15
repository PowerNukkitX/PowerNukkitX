package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 记忆存储器的标准实现
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class MemoryStorage implements IMemoryStorage {

    protected Map<Class<? extends IMemory<?>>, IMemory<?>> memoryMap = new HashMap<>();

    @Override
    public void put(IMemory<?> memory) {
        this.memoryMap.put((Class<? extends IMemory<?>>) memory.getClass(), memory);
    }

    @Override
    public void remove(Class<? extends IMemory<?>> memoryClazz) {
        memoryMap.remove(memoryClazz);
    }

    @Override
    public <T extends IMemory<?>, R extends Class<T>> T get(R memoryClazz) {
        return (T) memoryMap.get(memoryClazz);
    }

    @Override
    public boolean contains(Class<? extends IMemory<?>> memoryClazz) {
        return memoryMap.containsKey(memoryClazz);
    }
}
