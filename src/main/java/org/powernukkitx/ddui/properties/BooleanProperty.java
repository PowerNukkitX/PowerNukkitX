package org.powernukkitx.ddui.properties;

import org.cloudburstmc.protocol.bedrock.data.ddui.DynamicValueType;

/**
 * A property that holds a {@code boolean} value.
 *
 * @author xRookieFight
 * @since 06/03/2026
 */
public class BooleanProperty extends DataDrivenProperty<Boolean, Boolean> {

    @Override
    public DynamicValueType getType() {
        return DynamicValueType.BOOLEAN;
    }

    public BooleanProperty(String name, boolean value) {
        this(name, value, null);
    }

    public BooleanProperty(String name, boolean value, ObjectProperty parent) {
        super(name, value, parent);
    }
}
