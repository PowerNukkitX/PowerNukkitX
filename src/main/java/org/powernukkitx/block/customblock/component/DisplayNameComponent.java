package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.StringTag;

/**
 * Display name component for custom blocks.
 */
public class DisplayNameComponent implements BlockComponent {
    private String value = "";

    public DisplayNameComponent() {
    }

    public DisplayNameComponent(String value) {
        this.value = value != null ? value : "";
    }

    public DisplayNameComponent value(String value) {
        this.value = value != null ? value : "";
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.DISPLAY_NAME;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putString("value", value);
    }
}