package org.powernukkitx.block.customblock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Trait interface for converting a custom block to an item.
 * <p>
 * Implement this interface on a custom block to define how it should
 * behave when converted to an item (e.g., when broken or used in crafting).
 */
public interface BlockToItemConverter {

    /**
     * Convert this block to its item representation.
     *
     * @return the item identifier string, or null if no item conversion
     */
    @Nullable String toItemIdentifier();

    /**
     * Get the item count dropped when the block is broken.
     * Override this to control drop behavior.
     *
     * @return the number of items dropped (default: 1)
     */
    default int toItemDropCount() {
        return 1;
    }

    /**
     * Whether this block should drop itself as an item when broken.
     * If false, the block will not drop when broken (e.g., leaves dropping saplings).
     *
     * @return true if the block drops itself
     */
    default boolean dropsAsItem() {
        return true;
    }

    /**
     * Whether silk touch can be used to obtain this block as an item.
     * If true, silk touch will bypass normal drop logic and drop the block itself.
     *
     * @return true if silk touch works on this block
     */
    default boolean silkTouchObtainable() {
        return false;
    }
}