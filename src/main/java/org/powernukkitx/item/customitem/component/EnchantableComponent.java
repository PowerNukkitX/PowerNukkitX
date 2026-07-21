package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Enchantable component for custom items.
 * Defines enchantment properties for the item.
 */
public class EnchantableComponent implements ItemComponent {
    private int value = 0;
    private String slot = "";

    public EnchantableComponent() {
    }

    public EnchantableComponent(int value, String slot) {
        this.value = value;
        this.slot = slot != null ? slot : "";
    }

    public EnchantableComponent value(int value) {
        this.value = value;
        return this;
    }

    public EnchantableComponent slot(String value) {
        this.slot = value != null ? value : "";
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.ENCHANTABLE;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putInt("value", value)
                .putString("slot", slot);
    }
}