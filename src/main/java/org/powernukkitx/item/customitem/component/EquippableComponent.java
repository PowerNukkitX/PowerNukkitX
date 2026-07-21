package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Equippable component for custom items.
 * Controls where the item can be equipped (head, chest, legs, feet, etc).
 */
public class EquippableComponent implements ItemComponent {
    private String slot = "head";
    private String acceptedItems = "";
    private String interactSound = "";
    private boolean dispensable = true;
    private boolean swapEquipment = true;

    public EquippableComponent() {
    }

    public EquippableComponent(String slot) {
        this.slot = slot != null ? slot : "head";
    }

    public EquippableComponent slot(String value) {
        this.slot = value != null ? value : "head";
        return this;
    }

    public EquippableComponent acceptedItems(String value) {
        this.acceptedItems = value != null ? value : "";
        return this;
    }

    public EquippableComponent interactSound(String value) {
        this.interactSound = value != null ? value : "";
        return this;
    }

    public EquippableComponent dispensable(boolean value) {
        this.dispensable = value;
        return this;
    }

    public EquippableComponent swapEquipment(boolean value) {
        this.swapEquipment = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.EQUIPPABLE;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putString("slot", slot)
                .putString("accepted_items", acceptedItems)
                .putString("interact_sound", interactSound)
                .putBoolean("dispensable", dispensable)
                .putBoolean("swap_equipment", swapEquipment);
    }
}