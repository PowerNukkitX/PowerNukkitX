package cn.nukkit.entity.data.property;


import org.cloudburstmc.nbt.NbtMap;

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
    public NbtMap populateTag(NbtMap tag) {
        return tag.toBuilder()
                .putInt("type", 0)
                .putInt("default", getDefaultValue())
                .putInt("min", getMinValue())
                .putInt("max", getMaxValue())
                .putBoolean("clientSync", isClientSync())
                .build();
    }
}
