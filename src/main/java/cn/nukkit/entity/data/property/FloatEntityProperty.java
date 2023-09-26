package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Peng_Lx
 */
public class FloatEntityProperty extends EntityProperty{

    private final float defaultValue;
    private final float maxValue;
    private final float minValue;

    public FloatEntityProperty(String identifier, float defaultValue, float maxValue, float minValue) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    @Override
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 1);
        tag.putFloat("max", getMaxValue());
        tag.putFloat("min", getMinValue());
    }
}