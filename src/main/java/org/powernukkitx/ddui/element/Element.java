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