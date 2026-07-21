package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Loot component for custom blocks.
 * Defines the loot table used when the block is broken.
 */
public class LootComponent implements BlockComponent {
    private String table = "";

    public LootComponent() {
    }

    public LootComponent(String table) {
        this.table = table != null ? table : "";
    }

    public LootComponent table(String table) {
        this.table = table != null ? table : "";
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.LOOT;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putString("table", table);
    }
}