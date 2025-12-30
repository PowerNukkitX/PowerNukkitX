package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Peng_Lx
 */
public class FloatEntityProperty extends EntityProperty {

    private final float defaultValue;
    private final float maxValue;
    private final float minValue;
    private final boolean clientSync;

    public FloatEntityProperty(String identifier, float defaultValue, float minValue, float maxValue, boolean clientSync) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.clientSync = clientSync;
    }

    public FloatEntityProperty(String identifier, float defaultValue, float minValue, float maxValue) {
        this(identifier, defaultValue, minValue, maxValue, false);
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
    public boolean isClientSync() {
        return clientSync;
    }

    @Override
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 1);
        tag.putFloat("default", getDefaultValue());
        tag.putFloat("min", getMinValue());
        tag.putFloat("max", getMaxValue());
        tag.putBoolean("clientSync", isClientSync());
    }
}