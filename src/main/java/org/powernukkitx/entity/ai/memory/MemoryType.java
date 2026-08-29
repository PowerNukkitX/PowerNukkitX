package org.powernukkitx.entity.ai.memory;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.ai.memory.codec.IMemoryCodec;
import org.powernukkitx.utils.Identifier;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Entity memory is a class that stores entity data, and if {@link IMemoryCodec} is implemented, entity memory can also be persistently stored and linked entity metadata
 */


public final class MemoryType<Data> {
    /**
     * Persistable memory types
     */

    private static final Set<MemoryType<?>> PERSISTENT_MEMORIES = new HashSet<>();

    @Getter
    private final Identifier identifier;
    private final Supplier<Data> defaultData;
    @Getter
    @Nullable
    private IMemoryCodec<Data> codec;

    public MemoryType(Identifier identifier) {
        this(identifier, () -> null);
    }

    public MemoryType(Identifier identifier, Data defaultData) {
        this(identifier, () -> defaultData);
    }

    public MemoryType(String identifier) {
        this(new Identifier(identifier), () -> null);
    }

    public MemoryType(String identifier, Data defaultData) {
        this(new Identifier(identifier), () -> defaultData);
    }

    public MemoryType(String identifier, Supplier<Data> defaultData) {
        this(new Identifier(identifier), defaultData);
    }

    /**
     * @param identifier  the namespaced identifier of this memory type
     * @param defaultData the default value returned when the memory is not found in the entity memory storage
     */
    public MemoryType(Identifier identifier, Supplier<Data> defaultData) {
        this.identifier = identifier;
        this.defaultData = defaultData;
    }

    public static Set<MemoryType<?>> getPersistentMemories() {
        return PERSISTENT_MEMORIES;
    }

    public Data getDefaultData() {
        return defaultData.get();
    }

    public MemoryType<Data> withCodec(IMemoryCodec<Data> codec) {
        this.codec = codec;
        PERSISTENT_MEMORIES.add(this);
        return this;
    }

    public void encode(Entity entity, Data data) {
        if (codec != null) {
            codec.encode(data, entity.getNbt());
        }
    }

    /**
     * Forcibly encodes a memory<p/>
     * Casts the given data value to the Data type
     *
     * @param entity the target entity
     * @param data   the data
     */

    public void forceEncode(Entity entity, Object data) {
        if (codec != null) {
            @SuppressWarnings("unchecked")
            var castedData = (Data) data;
            codec.encode(castedData, entity.getNbt());
        }
    }

    public @Nullable Data decode(Entity entity) {
        return codec != null ? codec.decode(entity.getNbt()) : null;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof MemoryType<?> anotherType) {
            return identifier.equals(anotherType.identifier);
        }
        return false;
    }
}
