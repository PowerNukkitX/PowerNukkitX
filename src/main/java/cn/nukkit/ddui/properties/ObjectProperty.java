package cn.nukkit.ddui.properties;


import org.cloudburstmc.protocol.bedrock.data.ddui.DataStorePropertyValue;
import org.cloudburstmc.protocol.bedrock.data.ddui.DataStorePropertyValueType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xRookieFight
 * @since 06/03/2026
 */
public class ObjectProperty<T> extends DataDrivenProperty<Map<String, DataDrivenProperty<?, ?>>, T> {

    @Override
    public DataStorePropertyValueType getType() {
        return DataStorePropertyValueType.TYPE;
    }

    public ObjectProperty(String name) {
        this(name, null);
    }

    public ObjectProperty(String name, ObjectProperty parent) {
        super(name, new LinkedHashMap<>(), parent);
    }

    public DataDrivenProperty<?, ?> getProperty(String name) {
        return this.value.get(name);
    }

    public void setProperty(DataDrivenProperty<?, ?> property) {
        this.value.put(property.getName(), property);
    }

    public DataStorePropertyValue toPropertyValue() {
        Map<String, DataStorePropertyValue> children = new LinkedHashMap<>();

        for (Map.Entry<String, DataDrivenProperty<?, ?>> entry : this.value.entrySet()) {
            String key = entry.getKey();
            DataDrivenProperty<?, ?> prop = entry.getValue();

            DataStorePropertyValue child = convertProperty(prop);
            children.put(key, child);
        }

        return new DataStorePropertyValue(DataStorePropertyValueType.TYPE, children);
    }

    private static DataStorePropertyValue convertProperty(DataDrivenProperty<?, ?> prop) {
        return switch (prop) {
            case ObjectProperty<?> obj -> obj.toPropertyValue();
            case BooleanProperty bp -> new DataStorePropertyValue(bp.getType(), bp.getValue());
            case LongProperty lp -> new DataStorePropertyValue(lp.getType(), lp.getValue());
            case StringProperty sp -> new DataStorePropertyValue(sp.getType(), sp.getValue());
            default -> new DataStorePropertyValue(prop.getType(), String.valueOf(prop.getValue()));
        };
    }
}