package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for all item components.
 * Components define the behavior and properties of custom items.
 */
public interface ItemComponent {
    /**
     * Get the unique identifier for this component type.
     *
     * @return the component identifier
     */
    @NotNull ItemComponentIds getId();

    /**
     * Serialize this component to NBT.
     *
     * @return the NBT representation of this component
     */
    @NotNull CompoundTag toNBT();

    /**
     * Get the component identifier as a string.
     *
     * @return the component identifier string
     */
    default @NotNull String getStringId() {
        return getId().getId();
    }
}