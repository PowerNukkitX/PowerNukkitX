package cn.nukkit.entity.ai.memory.codec;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 记忆编解码器
 */
@PowerNukkitXOnly
@Since("1.19.62-r2")
public interface IMemoryCodec<Data> {
    Function<CompoundTag, Data> getDecoder();

    BiConsumer<Data, CompoundTag> getEncoder();

    void init(Data data, EntityIntelligent entity);

    @Nullable
    default Data decode(CompoundTag tag) {
        return getDecoder().apply(tag);
    }

    default void encode(Data data, CompoundTag tag) {
        getEncoder().accept(data, tag);
    }
}
