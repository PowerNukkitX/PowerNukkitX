package cn.nukkit.ddui.element;

import cn.nukkit.ddui.Observable;
import cn.nukkit.ddui.element.options.SpacerOptions;
import cn.nukkit.ddui.properties.BooleanProperty;
import cn.nukkit.ddui.properties.ObjectProperty;

/**
 * @author xRookieFight
 * @since 11/03/2026
 */
public class SpacerElement extends Element<Boolean> {

    public SpacerElement(ObjectProperty parent) {
        this(SpacerOptions.builder().build(), parent);
    }

    public SpacerElement(SpacerOptions options, ObjectProperty parent) {
        super("spacer", parent);
        applyVisibility(options);
    }

    @Override
    public SpacerElement setVisibility(boolean visible) {
        super.setVisibility(visible);
        setProperty(new BooleanProperty("spacer_visible", visible, this));
        return this;
    }

    @Override
    public SpacerElement setVisibility(Observable<Boolean> visible) {
        super.setVisibility(visible);
        var property = new BooleanProperty("spacer_visible", visible.getValue(), this);
        visible.subscribe(value -> {
            setVisibility(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    @SuppressWarnings("unchecked")
    private void applyVisibility(SpacerOptions options) {
        if (options.getVisible() instanceof Observable<?> obs) {
            setVisibility((Observable<Boolean>) obs);
        } else {
            setVisibility((Boolean) options.getVisible());
        }
    }
}