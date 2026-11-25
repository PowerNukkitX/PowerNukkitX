package org.powernukkitx.ddui.element;

import org.powernukkitx.ddui.Observable;
import org.powernukkitx.ddui.properties.BooleanProperty;
import org.powernukkitx.ddui.properties.ObjectProperty;
import org.powernukkitx.ddui.properties.StringProperty;

/**
 * @author xRookieFight
 * @since 06/03/2026
 */
public abstract class Element<T> extends ObjectProperty<T> {

    protected Element(String name, ObjectProperty parent) {
        super(name, parent);
    }

    public String getLabel() {
        var prop = getProperty("label");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public Element<T> setLabel(String label) {
        var property = new StringProperty("label", "", this);
        property.setValue(label);
        setProperty(property);
        return this;
    }

    public Element<T> setLabel(Observable<String> label) {
        var property = new StringProperty("label", label.getValue(), this);
        label.subscribe(value -> {
            setLabel(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public boolean getDisabled() {
        var prop = getProperty("disabled");
        if (prop instanceof BooleanProperty bp) return bp.getValue();
        return false;
    }

    public Element<T> setDisabled(boolean disabled) {
        var property = new BooleanProperty("disabled", false, this);
        property.setValue(disabled);
        setProperty(property);
        return this;
    }

    public Element<T> setDisabled(Observable<Boolean> disabled) {
        var property = new BooleanProperty("disabled", disabled.getValue(), this);
        disabled.subscribe(value -> {
            setDisabled(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    /**
     * Hover text shown by the client. Only buttons, toggles, sliders, text fields and
     * dropdowns render it, other elements ignore the property.
     *
     * @return the current tooltip, or an empty string when none is set
     */
    public String getToolTip() {
        var prop = getProperty("tooltip");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public Element<T> setToolTip(String tooltip) {
        setProperty(new StringProperty("tooltip", tooltip, this));
        return this;
    }

    public Element<T> setToolTip(Observable<String> tooltip) {
        var property = new StringProperty("tooltip", tooltip.getValue(), this);
        tooltip.subscribe(value -> {
            setToolTip(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    /**
     * Applies a tooltip option value, which is either a plain {@code String} or an {@link Observable}.
     * An empty string leaves the property out, like vanilla does when no tooltip is given.
     */
    @SuppressWarnings("unchecked")
    protected void applyToolTip(Object tooltip) {
        if (tooltip instanceof Observable<?> obs) {
            setToolTip((Observable<String>) obs);
        } else if (tooltip instanceof String text && !text.isEmpty()) {
            setToolTip(text);
        }
    }

    public boolean getVisibility() {
        var prop = getProperty("visible");
        if (prop instanceof BooleanProperty bp) return bp.getValue();
        return true;
    }

    public Element<T> setVisibility(boolean visible) {
        var property = new BooleanProperty("visible", true, this);
        property.setValue(visible);
        setProperty(property);
        return this;
    }

    public Element<T> setVisibility(Observable<Boolean> visible) {
        var property = new BooleanProperty("visible", visible.getValue(), this);
        visible.subscribe(value -> {
            setVisibility(value);
            return property;
        });
        setProperty(property);
        return this;
    }
}