package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Peng_Lx
 */
public class FloatEntityProperty extends EntityProperty{

    private final float defaultValue;
    private final float maxValue;
    private final float minValue;
    /**
     * @deprecated 
     */
    

    public FloatEntityProperty(String identifier, float defaultValue, float maxValue, float minValue) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }
    /**
     * @deprecated 
     */
    

    public float getDefaultValue() {
        return defaultValue;
    }
    /**
     * @deprecated 
     */
    

    public float getMaxValue() {
        return maxValue;
    }
    /**
     * @deprecated 
     */
    

    public float getMinValue() {
        return minValue;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 1);
        tag.putFloat("max", getMaxValue());
        tag.putFloat("min", getMinValue());
    }
}