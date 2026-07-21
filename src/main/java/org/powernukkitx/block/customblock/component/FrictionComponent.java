package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;

/**
 * Component defining block friction factor.
 */
public class FrictionComponent implements BlockComponent {
    private float value = 0.4f;

    public FrictionComponent() {
    }

    public FrictionComponent value(float friction) {
        this.value = friction;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.FRICTION;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putFloat("value", value);
    }
}