package org.powernukkitx.ddui.properties;


import org.cloudburstmc.protocol.bedrock.data.ddui.DynamicValue;
import org.cloudburstmc.protocol.bedrock.data.ddui.DynamicValueType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xRookieFight
 * @since 06/03/2026
 */
public class ObjectProperty<T> extends DataDrivenProperty<Map<String, DataDrivenProperty<?, ?>>, T> {

    @Override
    public DynamicValueType getType() {
        return DynamicValueType.OBJECT;
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

    public DynamicValue toPropertyValue() {
        Map<String, DynamicValue> children = new LinkedHashMap<>();

        for (Map.Entry<String, DataDrivenProperty<?, ?>> entry : this.value.entrySet()) {
            String key = entry.getKey();
            DataDrivenProperty<?, ?> prop = entry.getValue();

            DynamicValue child = convertProperty(prop);
            children.put(key, child);
        }

        return new DynamicValue(DynamicValueType.OBJECT, children);
    }

    private static DynamicValue convertProperty(DataDrivenProperty<?, ?> prop) {
        return switch (prop) {
            case ObjectProperty<?> obj -> obj.toPropertyValue();
            case BooleanProperty bp -> new DynamicValue(bp.getType(), bp.getValue());
            case LongProperty lp -> new DynamicValue(lp.getType(), lp.getValue());
            case StringProperty sp -> new DynamicValue(sp.getType(), sp.getValue());
            default -> new DynamicValue(prop.getType(), String.valueOf(prop.getValue()));
        };
    }
}
