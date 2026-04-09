package cn.nukkit.entity.ai.memory.codec;

import cn.nukkit.entity.EntityIntelligent;
import org.cloudburstmc.nbt.NbtMap;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 记忆编解码器
 */


public interface IMemoryCodec<Data> {
    /**
     * 获取记忆解码器，用于从CompoundTag读取持久化数据写入到实体记忆中
     * <p>
     * Obtain a memory decoder for reading persistent data from Compound Tag and writing it to entity memory
     *
     * @return the decoder
     */
    Function<NbtMap, Data> getDecoder();

    /**
     * 获取记忆编码器，将实体记忆中的数据持久化进实体CompoundTag
     * <p>
     * Get the memory encoder to persist the data in the entity memory into the entity Compound Tag
     *
     * @return the encoder
     */
    BiConsumer<Data, NbtMap> getEncoder();

    /**
     * 从实体记忆初始化实体，可以用于初始化实体DataFlag.
     * <p>
     * Initialize the entity from the entity memory, which can be used to initialize the entity Data Flag.
     *
     * @param data   the data
     * @param entity the entity
     */
    void init(@Nullable Data data, EntityIntelligent entity);

    @Nullable
    default Data decode(NbtMap tag) {
        return getDecoder().apply(tag);
    }

    default void encode(Data data, NbtMap tag) {
        getEncoder().accept(data, tag);
    }
}
