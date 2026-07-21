package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Redstone conductor component for custom blocks.
 */
public class RedstoneConductorComponent implements BlockComponent {
    private boolean enabled = true;

    public RedstoneConductorComponent() {
    }

    public RedstoneConductorComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public RedstoneConductorComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.REDSTONE_CONDUCTOR;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("enabled", enabled);
    }
}