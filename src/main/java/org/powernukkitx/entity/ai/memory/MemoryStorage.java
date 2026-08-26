package org.powernukkitx.entity.ai.memory;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.codec.IMemoryCodec;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard implementation of the memory storage
 */


public class MemoryStorage implements IMemoryStorage {

    //represents an empty value (null) - this is needed because ConcurrentHashMap does not allow null values
    public static final Object EMPTY_VALUE = new Object();

    protected Map<MemoryType<?>, Object> memoryMap = new ConcurrentHashMap<>();
    @Getter
    protected EntityIntelligent entity;

    public MemoryStorage(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public <D> void put(MemoryType<D> type, D data) {
        IMemoryCodec<D> codec = type.getCodec();
        if (codec != null) {
            codec.init(data, entity);
        }
        memoryMap.put(type, data != null ? data : EMPTY_VALUE);
    }

    @Override
    public <D> D get(MemoryType<D> type) {
        Object stored = memoryMap.get(type);
        if (stored == null) {
            //the memory was never written, so fall back to the persisted value or the default one
            D data = type.decode(getEntity());
            if (data == null) data = type.getDefaultData();
            put(type, data);
            return data;
        }
        @SuppressWarnings("unchecked")
        var value = stored != EMPTY_VALUE ? (D) stored : null;
        return value;
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
