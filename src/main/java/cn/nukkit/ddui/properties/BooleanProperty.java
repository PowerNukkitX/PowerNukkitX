package cn.nukkit.ddui.properties;

import org.cloudburstmc.protocol.bedrock.data.ddui.DataStorePropertyValueType;

/**
 * A property that holds a {@code boolean} value.
 *
 * @author xRookieFight
 * @since 06/03/2026
 */
public class BooleanProperty extends DataDrivenProperty<Boolean, Boolean> {

    @Override
    public DataStorePropertyValueType getType() {
        return DataStorePropertyValueType.BOOL;
    }

    public BooleanProperty(String name, boolean value) {
        this(name, value, null);
    }

    public BooleanProperty(String name, boolean value, ObjectProperty parent) {
        super(name, value, parent);
    }
}