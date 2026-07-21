package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Unsupported component for custom blocks.
 * Marks a block as unsupported (e.g., broken or removed).
 */
public class UnsupportedComponent implements BlockComponent {
    private boolean enabled = true;

    public UnsupportedComponent() {
    }

    public UnsupportedComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public UnsupportedComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.UNSUPPORTED;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("enabled", enabled);
    }
}