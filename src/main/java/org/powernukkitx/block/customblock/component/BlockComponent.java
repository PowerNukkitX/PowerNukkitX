package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for all block components. Components define the behavior,
 * appearance, and properties of custom blocks.
 */
public interface BlockComponent {
    /**
     * Get the unique identifier for this component type.
     * This should match the Minecraft Bedrock component namespace (e.g., "minecraft:collision_box").
     *
     * @return the component identifier
     */
    @NotNull BlockComponentIds getId();

    /**
     * Serialize this component to NBT for network transmission.
     *
     * @return the NBT representation of this component
     */
    @NotNull CompoundTag toNBT();

    /**
     * Get the component name as a string (for map keys).
     *
     * @return the component identifier string
     */
    default @NotNull String getName() {
        return getId().getStringId();
    }
}