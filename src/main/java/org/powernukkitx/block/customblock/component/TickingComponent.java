package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Ticking component for custom blocks.
 * Defines whether and how the block ticks.
 */
public class TickingComponent implements BlockComponent {
    private boolean enabled = true;

    public TickingComponent() {
    }

    public TickingComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public TickingComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.TICKING;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("enabled", enabled);
    }
}