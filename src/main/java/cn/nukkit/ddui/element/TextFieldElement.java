package cn.nukkit.ddui.element;

import cn.nukkit.ddui.Observable;
import cn.nukkit.Player;
import cn.nukkit.ddui.element.options.TextFieldOptions;
import cn.nukkit.ddui.properties.BooleanProperty;
import cn.nukkit.ddui.properties.ObjectProperty;
import cn.nukkit.ddui.properties.StringProperty;

public class TextFieldElement extends Element<String> {

    private final Observable<String> text;

    public TextFieldElement(String label, Observable<String> text, ObjectProperty parent) {
        this(label, text, TextFieldOptions.builder().build(), parent);
    }

    @SuppressWarnings("unchecked")
    public TextFieldElement(String label, Observable<String> text, TextFieldOptions options, ObjectProperty parent) {
        super("textField", parent);
        this.text = text;

        setLabel(label);

        setText(text);

        if (options.getVisible() instanceof Observable<?> obs) {
            setVisibility((Observable<Boolean>) obs);
            setTextFieldVisible((Observable<Boolean>) obs);
        } else {
            boolean v = (Boolean) options.getVisible();
            setVisibility(v);
            setTextFieldVisible(v);
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

    public boolean getTextFieldVisible() {
        var prop = getProperty("textfield_visible");
        if (prop instanceof BooleanProperty bp) return bp.getValue();
        return true;
    }

    public TextFieldElement setTextFieldVisible(boolean visible) {
        var existing = getProperty("textfield_visible");
        BooleanProperty property = (existing instanceof BooleanProperty bp)
                ? bp
                : new BooleanProperty("textfield_visible", true, this);
        property.setValue(visible);
        setProperty(property);
        return this;
    }

    public TextFieldElement setTextFieldVisible(Observable<Boolean> visible) {
        var existing = getProperty("textfield_visible");
        BooleanProperty property = (existing instanceof BooleanProperty bp)
                ? bp
                : new BooleanProperty("textfield_visible", true, this);
        property.setValue(visible.getValue());
        visible.subscribe(value -> {
            setTextFieldVisible(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public String getText() {
        var prop = getProperty("text");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public TextFieldElement setText(String text) {
        var existing = getProperty("text");
        StringProperty property = (existing instanceof StringProperty sp)
                ? sp
                : createTextProperty();
        property.setValue(text);
        setProperty(property);
        return this;
    }

    public TextFieldElement setText(Observable<String> text) {
        var existing = getProperty("text");
        StringProperty property = (existing instanceof StringProperty sp)
                ? sp
                : createTextProperty();
        property.setValue(text.getValue());
        text.subscribe(value -> {
            setText(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    private StringProperty createTextProperty() {
        StringProperty property = new StringProperty("text", "", this);
        property.addListener(this::triggerListeners);
        return property;
    }

    public String getDescription() {
        var prop = getProperty("description");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public TextFieldElement setDescription(String description) {
        var existing = getProperty("description");
        StringProperty property = (existing instanceof StringProperty sp)
                ? sp
                : new StringProperty("description", "", this);
        property.setValue(description);
        setProperty(property);
        return this;
    }

    public TextFieldElement setDescription(Observable<String> description) {
        var existing = getProperty("description");
        StringProperty property = (existing instanceof StringProperty sp)
                ? sp
                : new StringProperty("description", "", this);
        property.setValue(description.getValue());
        description.subscribe(value -> {
            setDescription(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    @Override
    public void triggerListeners(Player player, Object data) {
        super.triggerListeners(player, data);

        if (data instanceof String s) {
            setText(s);
            text.setValue(s);
        }
    }
}