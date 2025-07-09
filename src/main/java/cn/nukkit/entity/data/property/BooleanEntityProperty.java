package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;

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
    public void populateTag(CompoundTag tag) {
        tag.putInt("type", 2);
        tag.putBoolean("clientSync", isClientSync());
    }
}
