package cn.nukkit.ddui.element;

import cn.nukkit.ddui.Observable;
import cn.nukkit.ddui.element.options.HeaderOptions;
import cn.nukkit.ddui.properties.BooleanProperty;
import cn.nukkit.ddui.properties.ObjectProperty;
import cn.nukkit.ddui.properties.StringProperty;

/**
 * @author xRookieFight
 * @since 11/03/2026
 */
public class HeaderElement extends Element<String> {

    public HeaderElement(String text, ObjectProperty parent) {
        this(text, HeaderOptions.builder().build(), parent);
    }

    public HeaderElement(String text, HeaderOptions options, ObjectProperty parent) {
        super("header", parent);
        setText(text);
        applyVisibility(options);
    }

    public HeaderElement(Observable<String> text, ObjectProperty parent) {
        this(text, HeaderOptions.builder().build(), parent);
    }

    public HeaderElement(Observable<String> text, HeaderOptions options, ObjectProperty parent) {
        super("header", parent);
        setText(text);
        applyVisibility(options);
    }

    public String getText() {
        var prop = getProperty("text");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public HeaderElement setText(String text) {
        StringProperty property = resolveTextProperty();
        property.setValue(text);
        setProperty(property);
        return this;
    }

    public HeaderElement setText(Observable<String> text) {
        StringProperty property = resolveTextProperty();
        property.setValue(text.getValue());

        text.subscribe(value -> {
            setText(value);
            return property;
        });

        setProperty(property);
        return this;
    }

    @Override
    public HeaderElement setVisibility(boolean visible) {
        super.setVisibility(visible);
        setProperty(new BooleanProperty("header_visible", visible, this));
        return this;
    }

    @Override
    public HeaderElement setVisibility(Observable<Boolean> visible) {
        super.setVisibility(visible);
        var property = new BooleanProperty("header_visible", visible.getValue(), this);
        visible.subscribe(value -> {
            setVisibility(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    private StringProperty resolveTextProperty() {
        var existing = getProperty("text");
        return (existing instanceof StringProperty sp)
                ? sp
                : new StringProperty("text", "", this);
    }

    @SuppressWarnings("unchecked")
    private void applyVisibility(HeaderOptions options) {
        if (options.getVisible() instanceof Observable<?> obs) {
            setVisibility((Observable<Boolean>) obs);
        } else {
            setVisibility((Boolean) options.getVisible());
        }
    }
}