package org.powernukkitx.item.customitem;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all custom item definitions.
 * <p>
 * This class is the common ancestor for both:
 * <ul>
 *   <li>{@link ItemDataDrivenDefinition} - Modern component-based (version 2)</li>
 *   <li>{@link ItemLegacyDefinition} - Flat key-value components (version 1)</li>
 * </ul>
 */
public abstract class ItemCustomDefinition {
    private final ItemCustomVersion version;
    protected final CompoundTag components = new CompoundTag();

    protected ItemCustomDefinition(@NotNull ItemCustomVersion version) {
        this.version = version;
    }

    /**
     * Get the item version.
     */
    public @NotNull ItemCustomVersion getVersion() {
        return version;
    }

    /**
     * Get the serialized components NBT.
     */
    public @NotNull CompoundTag getComponents() {
        return components.copy();
    }

    /**
     * Get the network representation of this item.
     */
    public abstract @NotNull CompoundTag toNetwork();
}