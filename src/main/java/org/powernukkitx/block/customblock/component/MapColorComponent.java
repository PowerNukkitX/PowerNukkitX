package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.StringTag;

/**
 * Map color component for custom blocks.
 */
public class MapColorComponent implements BlockComponent {
    private String color = "#ffffff";

    public MapColorComponent() {
    }

    public MapColorComponent(String hexColor) {
        this.color = (hexColor != null && !hexColor.isBlank()) ? hexColor : "#ffffff";
    }

    public MapColorComponent color(String hexColor) {
        this.color = (hexColor != null && !hexColor.isBlank()) ? hexColor : "#ffffff";
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.MAP_COLOR;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putString("color", color);
    }
}