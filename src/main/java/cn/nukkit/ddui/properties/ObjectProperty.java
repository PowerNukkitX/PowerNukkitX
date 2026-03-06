package cn.nukkit.ddui.properties;

import cn.nukkit.network.protocol.types.ddui.DataStorePropertyType;
import cn.nukkit.network.protocol.types.ddui.DataStorePropertyValue;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xRookieFight
 * @since 06/03/2026
 */
public class ObjectProperty<T> extends DataDrivenProperty<Map<String, DataDrivenProperty<?, ?>>, T> {

    @Override
    public DataStorePropertyType getType() {
        return DataStorePropertyType.OBJECT;
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

        return DataStorePropertyValue.ofObject(children);
    }

    private static DataStorePropertyValue convertProperty(DataDrivenProperty<?, ?> prop) {
        if (prop instanceof ObjectProperty<?> obj) {
            return obj.toPropertyValue();
        } else if (prop instanceof BooleanProperty bp) {
            return DataStorePropertyValue.ofBoolean(bp.getValue());
        } else if (prop instanceof LongProperty lp) {
            return DataStorePropertyValue.ofLong(lp.getValue());
        } else if (prop instanceof StringProperty sp) {
            return DataStorePropertyValue.ofString(sp.getValue());
        }
        return DataStorePropertyValue.ofString(String.valueOf(prop.getValue()));
    }
}