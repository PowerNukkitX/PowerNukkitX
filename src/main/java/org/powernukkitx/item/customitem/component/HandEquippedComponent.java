package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Hand equipped component for custom items.
 * When true, the item renders like a tool in hand.
 */
public class HandEquippedComponent implements ItemComponent {
    private boolean value = false;

    public HandEquippedComponent() {
    }

    public HandEquippedComponent(boolean value) {
        this.value = value;
    }

    public HandEquippedComponent equipped(boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.HAND_EQUIPPED;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("value", value);
    }
}