package cn.nukkit.entity.data.property;

import org.cloudburstmc.nbt.NbtMap;

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
    public NbtMap populateTag(NbtMap tag) {
        return tag.toBuilder().
                putInt("type", 1)
                .putFloat("default", getDefaultValue())
                .putFloat("min", getMinValue())
                .putFloat("max", getMaxValue())
                .putBoolean("clientSync", isClientSync())
                .build();
    }
}