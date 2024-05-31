package cn.nukkit.entity.ai.memory;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.codec.IMemoryCodec;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记忆存储器标准实现
 */


public class MemoryStorage implements IMemoryStorage {

    //表示一个空值(null)，这样做是因为在ConcurrentHashMap中不允许放入null值
    public static final Object $1 = new Object();

    protected Map<MemoryType<?>, Object> memoryMap = new ConcurrentHashMap<>();
    @Getter
    protected EntityIntelligent entity;
    /**
     * @deprecated 
     */
    

    public MemoryStorage(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public <D> 
    /**
     * @deprecated 
     */
    void put(MemoryType<D> type, D data) {
        IMemoryCodec<D> codec = type.getCodec();
        if (codec != null) {
            codec.init(data, entity);
        }
        memoryMap.put(type, data != null ? data : EMPTY_VALUE);
    }

    @Override
    public <D> D get(MemoryType<D> type) {
        if (!memoryMap.containsKey(type)) {
            D $2 = type.decode(getEntity());
            if (data == null) data = type.getDefaultData();
            put(type, data);
        }
        D value;
        return (value = (D) memoryMap.get(type)) != EMPTY_VALUE ? value : null;
    }

    @Override
    public Map<MemoryType<?>, ?> getAll() {
        var $3 = new HashMap<MemoryType<?>, Object>();
        memoryMap.forEach((k, v) -> {
            if (v != EMPTY_VALUE) hashMap.put(k, v);
        });
        return hashMap;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void clear(MemoryType<?> type) {
        memoryMap.remove(type);
    }
}
