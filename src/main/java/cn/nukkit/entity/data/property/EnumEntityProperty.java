package cn.nukkit.entity.data.property;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;
import java.util.List;

/**
 * @author Peng_Lx
 */
public class EnumEntityProperty extends EntityProperty {

    private final String[] enums;
    private final String defaultValue;
    private final boolean clientSync;

    public EnumEntityProperty(String identifier, String[] enums, String defaultValue, boolean clientSync) {
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
        this.clientSync = clientSync;
    }

    public EnumEntityProperty(String identifier, String[] enums, String defaultValue) {
        this(identifier, enums, defaultValue, false);
    }

    public String[] getEnums() {
        return enums;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public NbtMap populateTag(NbtMap tag) {
        NbtMapBuilder builder = tag.toBuilder();
        builder.putInt("type", 3);
        final List<String> enumList = new ObjectArrayList<>(Arrays.stream(this.getEnums()).toList());
        builder.putList("enum", NbtType.STRING, enumList);
        builder.putBoolean("clientSync", isClientSync());
        return builder.build();
    }

    @Override
    public boolean isClientSync() {
        return clientSync;
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
