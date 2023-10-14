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

    public EnumEntityProperty(String identifier,String[] enums, String defaultValue) {
        super(identifier);

        boolean found = false;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 3);
        ListTag<StringTag> enumList = new ListTag<>();
        for (String enumValue : getEnums()) {
            enumList.add(new StringTag(enumValue));
        }
        tag.putList("enum", enumList);
    }

    public int findIndex(String value) {
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
