package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Hide durability component for custom items.
 * When true, hides the durability bar in the UI.
 */
public class HideDurabilityComponent implements ItemComponent {
    private boolean value = false;

    public HideDurabilityComponent() {
    }

    public HideDurabilityComponent(boolean value) {
        this.value = value;
    }

    public HideDurabilityComponent hide(boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.HIDE_DURABILITY;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("value", value);
    }
}