package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;

/**
 * Component defining mining time/destructibility by mining.
 */
public class DestructibleByMiningComponent implements BlockComponent {
    private float seconds = 0.0f;

    public DestructibleByMiningComponent() {
    }

    /**
     * Set mining time in seconds. Use -1 for unbreakable.
     */
    public DestructibleByMiningComponent seconds(float seconds) {
        this.seconds = seconds;
        return this;
    }

    /**
     * Convenience: true = breakable instantly, false = unbreakable.
     */
    public DestructibleByMiningComponent breakable(boolean breakable) {
        this.seconds = breakable ? 0.0f : -1.0f;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.DESTRUCTIBLE_BY_MINING;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putFloat("value", seconds);
    }
}