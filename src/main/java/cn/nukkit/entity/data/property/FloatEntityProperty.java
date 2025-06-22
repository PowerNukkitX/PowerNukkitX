package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Peng_Lx
 */
public class FloatEntityProperty extends EntityProperty{

    private final float defaultValue;
    private final float maxValue;
    private final float minValue;

    public FloatEntityProperty(String identifier, float defaultValue, float minValue, float maxValue) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }


    @Override
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 1);
        tag.putFloat("default", getDefaultValue());
        tag.putFloat("min", getMinValue());
        tag.putFloat("max", getMaxValue());
    }
}