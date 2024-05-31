package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Peng_Lx
 */
public class IntEntityProperty extends EntityProperty {

    private final int defaultValue;
    private final int maxValue;
    private final int minValue;
    /**
     * @deprecated 
     */
    

    public IntEntityProperty(String identifier, int defaultValue, int maxValue, int minValue) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }
    /**
     * @deprecated 
     */
    

    public int getDefaultValue() {
        return defaultValue;
    }
    /**
     * @deprecated 
     */
    

    public int getMaxValue() {
        return maxValue;
    }
    /**
     * @deprecated 
     */
    

    public int getMinValue() {
        return minValue;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 0);
        tag.putInt("max", getMaxValue());
        tag.putInt("min", getMinValue());
    }
}
