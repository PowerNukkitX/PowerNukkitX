package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.memory.codec.IMemoryCodec;
import cn.nukkit.utils.Identifier;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 表示一个特定的Memory类型
 */
@PowerNukkitXOnly
@Since("1.19.50-r1")
public final class MemoryType<Data> {

    /**
     * 可持久化的记忆类型
     */
    @Since("1.19.63-r1")
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
     * @param identifier  此记忆类型的命名空间标识符
     * @param defaultData 记忆未在实体记忆存储器中找到时返回的默认值
     */
    public MemoryType(Identifier identifier, Supplier<Data> defaultData) {
        this.identifier = identifier;
        this.defaultData = defaultData;
    }

    @Since("1.19.63-r1")
    public static Set<MemoryType<?>> getPersistentMemories() {
        return PERSISTENT_MEMORIES;
    }

    public Data getDefaultData() {
        return defaultData.get();
    }

    @Since("1.19.63-r1")
    public MemoryType<Data> withCodec(IMemoryCodec<Data> codec) {
        this.codec = codec;
        PERSISTENT_MEMORIES.add(this);
        return this;
    }

    @Since("1.19.63-r1")
    public void encode(Entity entity, Data data) {
        if (codec != null) {
            codec.encode(data, entity.namedTag);
        }
    }

    /**
     * 强制编码一个记忆<p/>
     * 会将给定的data值强转到Data类型
     *
     * @param entity 目标实体
     * @param data   数据
     */
    @Since("1.19.63-r1")
    public void forceEncode(Entity entity, Object data) {
        if (codec != null) {
            codec.encode((Data) data, entity.namedTag);
        }
    }

    @Since("1.19.63-r1")
    @Nullable
    public Data decode(Entity entity) {
        if (codec != null) {
            var tag = entity.namedTag;
            return codec.decode(tag);
        }
        return null;
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
