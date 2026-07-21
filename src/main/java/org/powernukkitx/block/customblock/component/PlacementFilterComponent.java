package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Placement filter component for custom blocks.
 * Controls where the block can be placed.
 */
public class PlacementFilterComponent implements BlockComponent {
    private boolean enabled = true;

    public PlacementFilterComponent() {
    }

    public PlacementFilterComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public PlacementFilterComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.PLACEMENT_FILTER;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("enabled", enabled);
    }
}