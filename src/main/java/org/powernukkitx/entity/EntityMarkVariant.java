package org.powernukkitx.entity;

import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.utils.Utils;

/**
 * Entities that implement this interface have a mark variant attribute.
 */


public interface EntityMarkVariant extends EntityComponent {
    default int getMarkVariant() {
        return getMemoryStorage().get(CoreMemoryTypes.MARK_VARIANT);
    }

    default void setMarkVariant(int variant) {
        getMemoryStorage().put(CoreMemoryTypes.MARK_VARIANT, variant);
    }

    default boolean hasMarkVariant() {
        return getMemoryStorage().notEmpty(CoreMemoryTypes.MARK_VARIANT);
    }

    /**
     * Picks a random mark variant value.
     */
    default int randomMarkVariant() {
        return getAllMarkVariant()[Utils.rand(0, getAllMarkVariant().length - 1)];
    }

    /**
     * Defines all possible variants.
     */
    int[] getAllMarkVariant();
}
