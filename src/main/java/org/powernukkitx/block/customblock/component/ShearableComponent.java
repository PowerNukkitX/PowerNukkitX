package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Shearable component for custom blocks.
 */
public class ShearableComponent implements BlockComponent {
    private boolean isShearable = false;
    private int chance = 100;
    private String item = "stick";

    public ShearableComponent() {
    }

    public ShearableComponent shearable(boolean value) {
        this.isShearable = value;
        return this;
    }

    public ShearableComponent chance(int value) {
        this.chance = value;
        return this;
    }

    public ShearableComponent item(String value) {
        this.item = value;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.SHEARABLE;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putBoolean("is_shearable", isShearable)
                .putInt("chance", chance)
                .putString("item", item);
    }
}