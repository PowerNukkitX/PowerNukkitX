package cn.nukkit.ddui.element;

import cn.nukkit.Player;
import cn.nukkit.ddui.Observable;
import cn.nukkit.ddui.element.options.ToggleOptions;
import cn.nukkit.ddui.properties.BooleanProperty;
import cn.nukkit.ddui.properties.ObjectProperty;
import cn.nukkit.ddui.properties.StringProperty;

/**
 * @author xRookieFight
 * @since 11/03/2026
 */
public class ToggleElement extends Element<Boolean> {

    private final Observable<Boolean> toggled;

    public ToggleElement(String label, Observable<Boolean> toggled, ObjectProperty parent) {
        this(label, toggled, ToggleOptions.builder().build(), parent);
    }

    @SuppressWarnings("unchecked")
    public ToggleElement(String label, Observable<Boolean> toggled,
                         ToggleOptions options, ObjectProperty parent) {
        super("toggle", parent);
        this.toggled = toggled;

        setLabel(label);

        setToggled(toggled);

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

        if (options.getDescription() instanceof Observable<?> obs) {
            setDescription((Observable<String>) obs);
        } else {
            setDescription((String) options.getDescription());
        }
    }

    public boolean isToggled() {
        var prop = getProperty("toggled");
        if (prop instanceof BooleanProperty bp) return bp.getValue();
        return false;
    }

    public ToggleElement setToggled(boolean value) {
        BooleanProperty property = resolveToggledProperty();
        property.setValue(value);
        setProperty(property);
        return this;
    }

    public ToggleElement setToggled(Observable<Boolean> value) {
        BooleanProperty property = resolveToggledProperty();
        property.setValue(value.getValue());

        value.subscribe(v -> {
            setToggled(v);
            return property;
        });

        setProperty(property);
        return this;
    }

    public String getDescription() {
        var prop = getProperty("description");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public ToggleElement setDescription(String description) {
        StringProperty property = resolveDescriptionProperty();
        property.setValue(description);
        setProperty(property);
        return this;
    }

    public ToggleElement setDescription(Observable<String> description) {
        StringProperty property = resolveDescriptionProperty();
        property.setValue(description.getValue());

        description.subscribe(value -> {
            setDescription(value);
            return property;
        });

        setProperty(property);
        return this;
    }

    @Override
    public ToggleElement setVisibility(boolean visible) {
        super.setVisibility(visible);
        setProperty(new BooleanProperty("toggle_visible", visible, this));
        return this;
    }

    @Override
    public ToggleElement setVisibility(Observable<Boolean> visible) {
        super.setVisibility(visible);
        var property = new BooleanProperty("toggle_visible", visible.getValue(), this);
        visible.subscribe(value -> {
            setVisibility(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    @Override
    public void triggerListeners(Player player, Object data) {
        super.triggerListeners(player, data);

        if (data instanceof Boolean b) {
            setToggled(b);
            toggled.setValue(b);
        }
    }

    private BooleanProperty resolveToggledProperty() {
        var existing = getProperty("toggled");
        if (existing instanceof BooleanProperty bp) return bp;
        BooleanProperty property = new BooleanProperty("toggled", false, this);
        property.addListener(this::triggerListeners);
        return property;
    }

    private StringProperty resolveDescriptionProperty() {
        var existing = getProperty("description");
        return (existing instanceof StringProperty sp)
                ? sp
                : new StringProperty("description", "", this);
    }
}
