package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Use duration component for custom items.
 * Controls how long the item is used when held.
 */
public class UseDurationComponent implements ItemComponent {
    private float duration = 0.0f;

    public UseDurationComponent() {
    }

    public UseDurationComponent(float duration) {
        this.duration = duration;
    }

    public UseDurationComponent duration(float value) {
        this.duration = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.USE_DURATION;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putFloat("use_duration", duration);
    }
}