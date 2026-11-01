package org.powernukkitx.ddui.element;

import org.powernukkitx.ddui.Observable;
import org.powernukkitx.Player;
import org.powernukkitx.ddui.element.options.ButtonOptions;
import org.powernukkitx.ddui.element.options.ImageDetails;
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

        ImageDetails image = options.getImageDetails();
        if (image != null) {
            if (image.getImageSrc() instanceof Observable<?> obs) {
                setImageSrc((Observable<String>) obs);
            } else {
                setImageSrc((String) image.getImageSrc());
            }

            if (image.getImagePackId() instanceof Observable<?> obs) {
                setImagePackId((Observable<String>) obs);
            } else {
                setImagePackId((String) image.getImagePackId());
            }
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

    // yeah, vanilla writes the icon flat on the button as image_src / image_pack_id, no nested object
    public String getImageSrc() {
        var prop = getProperty("image_src");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    /**
     * Sets the icon texture path, e.g. {@code textures/items/diamond}. Pair it with
     * {@link #setImagePackId(String)} so the client knows which pack to load it from.
     */
    public ButtonElement setImageSrc(String imageSrc) {
        setProperty(new StringProperty("image_src", imageSrc, this));
        return this;
    }

    public ButtonElement setImageSrc(Observable<String> imageSrc) {
        var property = new StringProperty("image_src", imageSrc.getValue(), this);
        imageSrc.subscribe(value -> {
            setImageSrc(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public String getImagePackId() {
        var prop = getProperty("image_pack_id");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public ButtonElement setImagePackId(String imagePackId) {
        setProperty(new StringProperty("image_pack_id", imagePackId, this));
        return this;
    }

    public ButtonElement setImagePackId(Observable<String> imagePackId) {
        var property = new StringProperty("image_pack_id", imagePackId.getValue(), this);
        imagePackId.subscribe(value -> {
            setImagePackId(value);
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