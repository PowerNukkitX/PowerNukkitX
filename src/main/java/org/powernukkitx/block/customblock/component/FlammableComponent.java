package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Flammable component for custom blocks.
 */
public class FlammableComponent implements BlockComponent {
    private int flameSource = 1;
    private int flameEncouragement = 1;

    public FlammableComponent() {
    }

    public FlammableComponent flameSource(int value) {
        this.flameSource = value;
        return this;
    }

    public FlammableComponent flameEncouragement(int value) {
        this.flameEncouragement = value;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.SHEARABLE;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putInt("flame_source", flameSource)
                .putInt("flame_encouragement", flameEncouragement);
    }
}