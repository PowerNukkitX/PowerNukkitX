package cn.nukkit.ddui.properties;

import org.cloudburstmc.protocol.bedrock.data.ddui.DataStorePropertyValueType;

/**
 * A property that holds a {@link String} value.
 *
 * @author xRookieFight
 * @since 06/03/2026
 */
public class StringProperty extends DataDrivenProperty<String, String> {

    @Override
    public DataStorePropertyValueType getType() {
        return DataStorePropertyValueType.STRING;
    }

    public StringProperty(String name, String value) {
        this(name, value, null);
    }

    public StringProperty(String name, String value, ObjectProperty parent) {
        super(name, value, parent);
    }
}