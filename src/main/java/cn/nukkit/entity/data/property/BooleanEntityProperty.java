package cn.nukkit.entity.data.property;

import org.cloudburstmc.nbt.NbtMap;

/**
 * @author Peng_Lx
 */
public class BooleanEntityProperty extends EntityProperty {

    private final boolean defaultValue;
    private final boolean clientSync;

    public BooleanEntityProperty(String identifier, boolean defaultValue, boolean clientSync) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.clientSync = clientSync;
    }

    public BooleanEntityProperty(String identifier, boolean defaultValue) {
        this(identifier, defaultValue, false);
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isClientSync() {
        return clientSync;
    }

    @Override
    public NbtMap populateTag(NbtMap tag) {
        return tag.toBuilder().putInt("type", 2)
                .putBoolean("clientSync", isClientSync())
                .build();
    }
}
