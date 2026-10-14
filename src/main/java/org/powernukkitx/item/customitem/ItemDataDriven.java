package org.powernukkitx.item.customitem;

import org.powernukkitx.item.Item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An item that exists only as data, with no class of its own.
 * <p>
 * Items loaded from a behavior pack have no dedicated Java class: everything about them
 * lives in the {@link CustomItemDefinition} built from the pack's JSON, which the
 * {@link Item} core already reads at runtime for food, durability, wearability, cooldowns
 * and the rest. One instance of this class therefore backs one pack item, and many
 * instances with different definitions back a whole pack.
 */
public class ItemDataDriven extends Item implements CustomItem {

    /**
     * @param id         the item identifier, which must match the definition's
     * @param definition the definition describing the item; null only for the short-lived
     *                   prototype used while the definition is still being built, since
     *                   the builder needs an instance to read the identifier from
     */
    public ItemDataDriven(@NotNull String id, @Nullable CustomItemDefinition definition) {
        super(id, definition);
    }

    /**
     * @return the definition this item was built with, or null for the prototype that
     * exists only while that definition is being built
     */
    @Override
    public @Nullable CustomItemDefinition getDefinition() {
        return definition() instanceof CustomItemDefinition custom ? custom : null;
    }

    @Override
    public ItemDataDriven clone() {
        return (ItemDataDriven) super.clone();
    }
}
