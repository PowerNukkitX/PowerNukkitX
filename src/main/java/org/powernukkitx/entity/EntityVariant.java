package org.powernukkitx.entity;

import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.utils.Utils;

/**
 * Entities that implement this interface have a variant attribute.
 */


public interface EntityVariant extends EntityComponent {
    default int getVariant() {
        return getMemoryStorage().get(CoreMemoryTypes.VARIANT);
    }

    default void setVariant(int variant) {
        getMemoryStorage().put(CoreMemoryTypes.VARIANT, variant);
    }

    default boolean hasVariant() {
        return getMemoryStorage().notEmpty(CoreMemoryTypes.VARIANT);
    }

    /**
     * Picks a random variant value.
     */
    default int randomVariant() {
        return getAllVariant()[Utils.rand(0, getAllVariant().length - 1)];
    }

    /**
     * Defines all possible variants.
     */
    int[] getAllVariant();
}
