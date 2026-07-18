package org.powernukkitx.entity.ai.memory.codec;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * memory codec
 */


public interface IMemoryCodec<Data> {
    /**
     * Obtain a memory decoder for reading persistent data from Compound Tag and writing it to entity memory
     *
     * @return the decoder
     */
    Function<CompoundTag, Data> getDecoder();

    /**
     * Get the memory encoder to persist the data in the entity memory into the entity Compound Tag
     *
     * @return the encoder
     */
    BiConsumer<Data, CompoundTag> getEncoder();

    /**
     * Initialize the entity from the entity memory, which can be used to initialize the entity Data Flag.
     *
     * @param data   the data
     * @param entity the entity
     */
    void init(@Nullable Data data, EntityIntelligent entity);

    @Nullable
    default Data decode(CompoundTag tag) {
        return getDecoder().apply(tag);
    }

    default void encode(Data data, CompoundTag tag) {
        getEncoder().accept(data, tag);
    }
}
