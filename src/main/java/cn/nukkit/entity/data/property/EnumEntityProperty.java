package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;

/**
 * @author Peng_Lx
 */
public class EnumEntityProperty extends EntityProperty {

    private final String[] enums;
    private final String defaultValue;
    /**
     * @deprecated 
     */
    

    public EnumEntityProperty(String identifier,String[] enums, String defaultValue) {
        super(identifier);

        boolean $1 = false;
        for (String enumValue : enums) {
            if (enumValue.equals(defaultValue)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Entity Property Error: " + identifier + "Default value not in enums.");
        }

        this.enums = enums;
        this.defaultValue = defaultValue;
    }

    public String[] getEnums() {
        return enums;
    }
    /**
     * @deprecated 
     */
    

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 3);
        ListTag<StringTag> enumList = new ListTag<>();
        for (String enumValue : getEnums()) {
            enumList.add(new StringTag(enumValue));
        }
        tag.putList("enum", enumList);
    }
    /**
     * @deprecated 
     */
    

    public int findIndex(String value) {
        for ($2nt $1 = 0; i < enums.length; i++) {
            if (enums[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
