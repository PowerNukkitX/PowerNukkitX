package org.powernukkitx.entity.ai.memory;

import org.powernukkitx.entity.Entity;

import java.util.Map;

/**
 * memory storage
 */


public interface IMemoryStorage {

    /**
     * Write data to MemoryType
     *
     * @param type the memory type
     * @param data the data
     * @param <D>  the data type
     */
    <D> void put(MemoryType<D> type, D data);

    /**
     * Get data from the specified MemoryType
     *
     * @param type the memory type
     * @param <D>  the data type
     * @return the data
     */
    <D> D get(MemoryType<D> type);

    /**
     * get all memories
     *
     * @return all memories
     */
    Map<MemoryType<?>, ?> getAll();

    /**
     * Clear the specified MemoryType data to null
     *
     * @param type the memory type
     */
    void clear(MemoryType<?> type);

    /**
     * Get the entity that the memory store belongs to
     *
     * @return the entity
     */

    Entity getEntity();

    /**
     * Check if the specified memory type data is empty (null)
     *
     * @param type the memory type
     * @return whether it is empty
     */
    default boolean isEmpty(MemoryType<?> type) {
        return get(type) == null;
    }

    /**
     * Check if the specified memory type data is not empty (null)
     *
     * @param type the memory type
     * @return whether it is not empty
     */
    default boolean notEmpty(MemoryType<?> type) {
        return get(type) != null;
    }

    /**
     * Use the specified data compare the data of memory type
     *
     * @param type the memory type
     * @param to   the specified data
     * @return whether they are equal
     */
    default <D> boolean compareDataTo(MemoryType<D> type, Object to) {
        D value;
        return (value = get(type)) != null ? value.equals(to) : to == null;
    }

    /**
     * Encode the data of this memory storage into the entity NBT (if there is a codec attached to the Memory Type)
     */
    default void encode() {
        var entity = getEntity();
        for (var memoryType : getAll().entrySet()) {
            memoryType.getKey().forceEncode(entity, memoryType.getValue());
        }
    }
}
