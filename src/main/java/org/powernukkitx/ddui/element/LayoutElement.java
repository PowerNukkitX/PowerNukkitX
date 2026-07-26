package org.powernukkitx.ddui.element;

import org.powernukkitx.ddui.properties.DataDrivenProperty;
import org.powernukkitx.ddui.properties.LongProperty;
import org.powernukkitx.ddui.properties.ObjectProperty;

/**
 * @author xRookieFight
 * @since 06/03/2026
 */
public class LayoutElement extends ObjectProperty<Object> {

    public LayoutElement(ObjectProperty parent) {
        super("layout", parent);
    }

    @Override
    public void setProperty(DataDrivenProperty<?, ?> property) {
        int count = getChildCount();
        property.setName(String.valueOf(count));

        super.setProperty(property);
        super.setProperty(new LongProperty("length", count + 1L, this));
    }

    private int getChildCount() {
        int size = getValue().size();

        if (getValue().containsKey("length")) size -= 1;
        return size;
    }
}