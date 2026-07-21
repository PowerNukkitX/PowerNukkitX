package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Component defining light dampening (how much light passes through).
 */
public class LightDampeningComponent implements BlockComponent {
    private int lightLevel = 15;

    public LightDampeningComponent() {
    }

    public LightDampeningComponent lightLevel(int level) {
        this.lightLevel = Math.max(0, Math.min(15, level));
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.LIGHT_DAMPENING;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putByte("lightLevel", (byte) lightLevel);
    }
}