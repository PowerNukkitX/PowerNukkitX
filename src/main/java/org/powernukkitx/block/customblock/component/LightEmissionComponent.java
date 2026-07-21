package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Component defining light emission level.
 */
public class LightEmissionComponent implements BlockComponent {
    private int emission = 0;

    public LightEmissionComponent() {
    }

    public LightEmissionComponent emission(int level) {
        this.emission = Math.max(0, Math.min(15, level));
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.LIGHT_EMISSION;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putByte("emission", (byte) emission);
    }
}