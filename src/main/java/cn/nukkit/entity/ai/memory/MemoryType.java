package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.Identifier;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * 表示一个特定的Memory类型
 */
@PowerNukkitXOnly
@Since("1.19.50-r1")
@Getter
public final class MemoryType<Data> {

    private final Identifier identifier;
    private final Supplier<Data> defaultData;

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

    public Data getDefaultData() {
        return defaultData.get();
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
