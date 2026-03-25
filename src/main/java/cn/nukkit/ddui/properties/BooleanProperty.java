package cn.nukkit.ddui.properties;

import cn.nukkit.network.protocol.types.ddui.DataStorePropertyType;

/**
 * A property that holds a {@code boolean} value.
 *
 * @author xRookieFight
 * @since 06/03/2026
 */
public class BooleanProperty extends DataDrivenProperty<Boolean, Boolean> {

    @Override
    public DataStorePropertyType getType() {
        return DataStorePropertyType.BOOLEAN;
    }

    public BooleanProperty(String name, boolean value) {
        this(name, value, null);
    }

    public BooleanProperty(String name, boolean value, ObjectProperty parent) {
        super(name, value, parent);
    }
}