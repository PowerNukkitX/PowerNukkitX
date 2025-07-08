package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Peng_Lx
 */
public class IntEntityProperty extends EntityProperty {

    private final int defaultValue;
    private final int maxValue;
    private final int minValue;
    private final boolean clientSync;

    public IntEntityProperty(String identifier, int defaultValue, int minValue, int maxValue, boolean clientSync) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.clientSync = clientSync;
    }

    public IntEntityProperty(String identifier, int defaultValue, int minValue, int maxValue) {
        this(identifier, defaultValue, minValue, maxValue, false);
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public boolean isClientSync() {
        return clientSync;
    }

    @Override
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 0);
        tag.putInt("default", getDefaultValue());
        tag.putInt("min", getMinValue());
        tag.putInt("max", getMaxValue());
        tag.putBoolean("clientSync", isClientSync());
    }
}
