package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记忆存储器标准实现
 */
@PowerNukkitXOnly
@Since("1.19.40-r4")
public class MemoryStorage implements IMemoryStorage {

    //表示一个空值(null)，这样做是因为在ConcurrentHashMap中不允许放入null值
    public static final Object EMPTY_VALUE = new Object();

    protected Map<MemoryType<?>, Object> memoryMap = new ConcurrentHashMap<>();

    @Override
    public <D> void put(MemoryType<D> type, D data) {
        memoryMap.put(type, data != null ? data : EMPTY_VALUE);
    }

    @Override
    public <D> D get(MemoryType<D> type) {
        if (!memoryMap.containsKey(type)) put(type, type.getDefaultData());
        D value;
        return (value = (D) memoryMap.get(type)) != EMPTY_VALUE ? value : null;
    }

    @Override
    public Map<MemoryType<?>, ?> getAll() {
        var hashMap = new HashMap<MemoryType<?>, Object>();
        memoryMap.forEach((k, v) -> {
            if (v != EMPTY_VALUE) hashMap.put(k, v);
        });
        return hashMap;
    }

    @Override
    public void clear(MemoryType<?> type) {
        memoryMap.remove(type);
    }
}
