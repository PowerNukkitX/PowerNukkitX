package org.powernukkitx.ddui.element;

import org.powernukkitx.ddui.Observable;
import org.powernukkitx.Player;
import org.powernukkitx.ddui.element.options.CloseButtonOptions;
import org.powernukkitx.ddui.properties.BooleanProperty;
import org.powernukkitx.ddui.properties.ObjectProperty;

import java.util.function.Consumer;

public class CloseButtonElement extends Element<Long> {

    @Override
    public String getName() {
        return "closeButton";
    }

    public CloseButtonElement(ObjectProperty parent) {
        this(CloseButtonOptions.builder().build(), parent);
    }

    @SuppressWarnings("unchecked")
    public CloseButtonElement(CloseButtonOptions options, ObjectProperty parent) {
        super("closeButton", parent);

        if (options.getLabel() instanceof Observable<?> obs) {
            setLabel((Observable<String>) obs);
        } else {
            setLabel((String) options.getLabel());
        }

        if (options.getVisible() instanceof Observable<?> obs) {
            setVisibility((Observable<Boolean>) obs);
        } else {
            setVisibility((Boolean) options.getVisible());
        }

        ButtonClickElement clickElement = new ButtonClickElement(this);
        setProperty(clickElement);
        clickElement.addListener(this::triggerListeners);
    }

    @Override
    public CloseButtonElement setVisibility(boolean visible) {
        super.setVisibility(visible);
        setProperty(new BooleanProperty("button_visible", visible, this));
        return this;
    }

    @Override
    public CloseButtonElement setVisibility(Observable<Boolean> visible) {
        super.setVisibility(visible);
        var property = new BooleanProperty("button_visible", visible.getValue(), this);
        visible.subscribe(value -> {
            setVisibility(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public void addListener(Consumer<Player> listener) {
        addListener((player, data) -> listener.accept(player));
    }
}