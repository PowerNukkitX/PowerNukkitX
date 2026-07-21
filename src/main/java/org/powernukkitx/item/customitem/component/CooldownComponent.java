package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Cooldown component for custom items.
 * Controls the cooldown applied after using the item.
 */
public class CooldownComponent implements ItemComponent {
    private String category = "";
    private float duration = 0.0f;

    public CooldownComponent() {
    }

    public CooldownComponent(String category, float duration) {
        this.category = category != null ? category : "";
        this.duration = duration;
    }

    public CooldownComponent category(String value) {
        this.category = value != null ? value : "";
        return this;
    }

    public CooldownComponent duration(float value) {
        this.duration = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.COOLDOWN;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putString("category", category)
                .putFloat("duration", duration);
    }
}