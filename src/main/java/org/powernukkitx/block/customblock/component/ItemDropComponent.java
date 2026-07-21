package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Item drop component for custom blocks.
 * Controls what item drops when the block is broken.
 */
public class ItemDropComponent implements BlockComponent {
    private boolean enabled = true;
    private String item = "";

    public ItemDropComponent() {
    }

    public ItemDropComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public ItemDropComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ItemDropComponent item(String item) {
        this.item = item != null ? item : "";
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.ITEM_DROP;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putBoolean("enabled", enabled)
                .putString("item", item);
    }
}