package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Waterlogged component for custom blocks.
 */
public class WaterloggedComponent implements BlockComponent {
    private boolean waterlogged = false;

    public WaterloggedComponent() {
    }

    public WaterloggedComponent(boolean waterlogged) {
        this.waterlogged = waterlogged;
    }

    public WaterloggedComponent waterlogged(boolean waterlogged) {
        this.waterlogged = waterlogged;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.WATERLOGGED;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("waterlogged", waterlogged);
    }
}