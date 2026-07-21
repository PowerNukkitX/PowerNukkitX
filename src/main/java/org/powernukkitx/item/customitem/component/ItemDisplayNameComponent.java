package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Display name component for custom items.
 */
public class ItemDisplayNameComponent implements ItemComponent {
    private String value = "";

    public ItemDisplayNameComponent() {
    }

    public ItemDisplayNameComponent(String value) {
        this.value = value != null ? value : "";
    }

    public ItemDisplayNameComponent value(String value) {
        this.value = value != null ? value : "";
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.DISPLAY_NAME;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putString("value", value);
    }
}