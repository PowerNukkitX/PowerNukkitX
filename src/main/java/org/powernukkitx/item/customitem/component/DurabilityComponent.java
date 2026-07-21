package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Durability component for custom items.
 * Controls the item's max damage, repair cost, and other durability settings.
 */
public class DurabilityComponent implements ItemComponent {
    private int maxDurability = 0;
    private boolean damageByRandom = false;

    public DurabilityComponent() {
    }

    public DurabilityComponent(int maxDurability) {
        this.maxDurability = maxDurability;
    }

    public DurabilityComponent maxDurability(int value) {
        this.maxDurability = value;
        return this;
    }

    public DurabilityComponent damageByRandom(boolean value) {
        this.damageByRandom = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.DURABILITY;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putInt("max_durability", maxDurability)
                .putBoolean("damage_by_random", damageByRandom);
    }
}