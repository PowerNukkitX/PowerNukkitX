package org.powernukkitx.ddui.properties;

import org.cloudburstmc.protocol.bedrock.data.ddui.DynamicValueType;

/**
 * A property that holds a {@code long} value.
 *
 * @author xRookieFight
 * @since 06/03/2026
 */
public class LongProperty extends DataDrivenProperty<Long, Long> {

    @Override
    public DynamicValueType getType() {
        return DynamicValueType.INTEGER;
    }

    public LongProperty(String name, long value) {
        this(name, value, null);
    }

    public LongProperty(String name, long value, ObjectProperty parent) {
        super(name, value, parent);
    }
}
