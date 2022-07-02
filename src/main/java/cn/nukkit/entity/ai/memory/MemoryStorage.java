package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class MemoryStorage implements IMemoryStorage{

    protected Map<String,IMemory<?>> memoryMap = new HashMap<>();

    @Override
    public void put(IMemory<?> memory) {
        memoryMap.put(memory.getName(),memory);
    }

    @Override
    public void remove(String memoryName) {
        memoryMap.remove(memoryName);
    }

    @Override
    public IMemory<?> get(String memoryName) {
        return memoryMap.get(memoryName);
    }
}
