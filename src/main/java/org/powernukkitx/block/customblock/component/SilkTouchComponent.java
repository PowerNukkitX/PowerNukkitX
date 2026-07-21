package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Silk touch component for custom blocks.
 */
public class SilkTouchComponent implements BlockComponent {
    private boolean enabled = false;
    private String item = "";

    public SilkTouchComponent() {
    }

    public SilkTouchComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public SilkTouchComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public SilkTouchComponent item(String item) {
        this.item = item != null ? item : "";
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.SILK_TOUCH;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putBoolean("enabled", enabled)
                .putString("item", item);
    }
}