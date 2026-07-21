package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Food component for custom items.
 */
public class FoodComponent implements ItemComponent {
    private float nutrition = 0;
    private float saturationModifier = 0;
    private boolean canAlwaysEat = false;

    public FoodComponent() {
    }

    public FoodComponent(float nutrition) {
        this.nutrition = nutrition;
    }

    public FoodComponent nutrition(float value) {
        this.nutrition = value;
        return this;
    }

    public FoodComponent saturationModifier(float value) {
        this.saturationModifier = value;
        return this;
    }

    public FoodComponent canAlwaysEat(boolean value) {
        this.canAlwaysEat = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.FOOD;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putFloat("nutrition", nutrition)
                .putFloat("saturation_modifier", saturationModifier)
                .putBoolean("can_always_eat", canAlwaysEat);
    }
}