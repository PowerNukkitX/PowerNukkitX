package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Peng_Lx
 */
public class BooleanEntityProperty extends EntityProperty {

    private final boolean defaultValue;
    /**
     * @deprecated 
     */
    

    public BooleanEntityProperty(String identifier, boolean defaultValue) {
        super(identifier);
        this.defaultValue = defaultValue;
    }
    /**
     * @deprecated 
     */
    

    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 2);
    }
}
