package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Seed component for custom items.
 * Controls the plant growth behavior.
 */
public class SeedComponent implements ItemComponent {
    private float cropResult = 0;
    private float growth = 0;
    private float maturityValue = 1;

    public SeedComponent() {
    }

    public SeedComponent cropResult(float value) {
        this.cropResult = value;
        return this;
    }

    public SeedComponent growth(float value) {
        this.growth = value;
        return this;
    }

    public SeedComponent maturityValue(float value) {
        this.maturityValue = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.SEED;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putFloat("crop_result", cropResult)
                .putFloat("growth", growth)
                .putFloat("maturity_value", maturityValue);
    }
}