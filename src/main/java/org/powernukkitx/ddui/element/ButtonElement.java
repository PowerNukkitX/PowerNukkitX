package org.powernukkitx.ddui.element;

import org.powernukkitx.ddui.Observable;
import org.powernukkitx.Player;
import org.powernukkitx.ddui.element.options.ButtonOptions;
import org.powernukkitx.ddui.properties.BooleanProperty;
import org.powernukkitx.ddui.properties.ObjectProperty;
import org.powernukkitx.ddui.properties.StringProperty;

import java.util.function.Consumer;

public class ButtonElement extends Element<Long> {

    public ButtonElement(String label, ObjectProperty parent) {
        this(label, ButtonOptions.builder().build(), parent);
    }

    @SuppressWarnings("unchecked")
    public ButtonElement(String label, ButtonOptions options, ObjectProperty parent) {
        super("button", parent);

        setLabel(label);

        if (options.getTooltip() instanceof Observable<?> obs) {
            setToolTip((Observable<String>) obs);
        } else {
            setToolTip((String) options.getTooltip());
        }

        if (options.getVisible() instanceof Observable<?> obs) {
            setVisibility((Observable<Boolean>) obs);
        } else {
            setVisibility((Boolean) options.getVisible());
        }

        if (options.getDisabled() instanceof Observable<?> obs) {
            setDisabled((Observable<Boolean>) obs);
        } else {
            setDisabled((Boolean) options.getDisabled());
        }

        ButtonClickElement clickElement = new ButtonClickElement(this);
        setProperty(clickElement);
        clickElement.addListener(this::triggerListeners);
    }

    public String getToolTip() {
        var prop = getProperty("tooltip");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public ButtonElement setToolTip(String tooltip) {
        var property = new StringProperty("tooltip", tooltip, this);
        setProperty(property);
        return this;
    }

    public ButtonElement setToolTip(Observable<String> tooltip) {
        var property = new StringProperty("tooltip", tooltip.getValue(), this);
        tooltip.subscribe(value -> {
            setToolTip(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    @Override
    public ButtonElement setVisibility(boolean visible) {
        super.setVisibility(visible);
        applyButtonVisible(visible);
        return this;
    }

    @Override
    public ButtonElement setVisibility(Observable<Boolean> visible) {
        super.setVisibility(visible);
        var property = new BooleanProperty("button_visible", visible.getValue(), this);
        visible.subscribe(value -> {
            setVisibility(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    private void applyButtonVisible(boolean visible) {
        var property = new BooleanProperty("button_visible", visible, this);
        setProperty(property);
    }

    public void addListener(Consumer<Player> listener) {
        addListener((player, data) -> listener.accept(player));
    }
}