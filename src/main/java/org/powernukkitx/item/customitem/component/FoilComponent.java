package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Foil component for custom items.
 * When true, adds the enchantment glint effect.
 */
public class FoilComponent implements ItemComponent {
    private boolean value = false;

    public FoilComponent() {
    }

    public FoilComponent(boolean value) {
        this.value = value;
    }

    public FoilComponent foil(boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.FOIL;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("value", value);
    }
}