package cn.nukkit.ddui.properties;

import cn.nukkit.network.protocol.types.ddui.DataStorePropertyType;

/**
 * A property that holds a {@link String} value.
 *
 * @author xRookieFight
 * @since 06/03/2026
 */
public class StringProperty extends DataDrivenProperty<String, String> {

    @Override
    public DataStorePropertyType getType() {
        return DataStorePropertyType.STRING;
    }

    public StringProperty(String name, String value) {
        this(name, value, null);
    }

    public StringProperty(String name, String value, ObjectProperty parent) {
        super(name, value, parent);
    }
}