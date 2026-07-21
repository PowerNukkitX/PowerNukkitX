package org.powernukkitx.item.customitem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Trait interface for converting an item to a custom block.
 * <p>
 * Implement this interface on a custom item to define how it should
 * behave when placed as a block.
 */
public interface ItemToBlockConverter {

    /**
     * Convert this item to its block representation.
     *
     * @return the block identifier string, or null if no block conversion
     */
    @Nullable String toBlockIdentifier();

    /**
     * Whether this item should place a block when used.
     *
     * @return true if the item places a block on use
     */
    default boolean placesBlock() {
        return toBlockIdentifier() != null;
    }
}