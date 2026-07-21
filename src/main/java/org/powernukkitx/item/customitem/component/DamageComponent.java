package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Damage component for custom items.
 * Defines the attack damage dealt by the item.
 */
public class DamageComponent implements ItemComponent {
    private float value = 1.0f;

    public DamageComponent() {
    }

    public DamageComponent(float value) {
        this.value = value;
    }

    public DamageComponent damage(float value) {
        this.value = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.DAMAGE;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putFloat("value", value);
    }
}