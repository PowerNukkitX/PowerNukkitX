package cn.nukkit.ddui.element;

import cn.nukkit.ddui.Observable;
import cn.nukkit.Player;
import cn.nukkit.ddui.element.options.SliderElementOptions;
import cn.nukkit.ddui.properties.BooleanProperty;
import cn.nukkit.ddui.properties.LongProperty;
import cn.nukkit.ddui.properties.ObjectProperty;
import cn.nukkit.ddui.properties.StringProperty;

public class SliderElement extends Element<Long> {

    private final Observable<Long> currentValue;

    public SliderElement(String label,
                         Observable<Long> currentValue,
                         long minValue,
                         long maxValue,
                         ObjectProperty parent) {
        this(label, currentValue, minValue, maxValue,
                SliderElementOptions.builder().build(), parent);
    }

    @SuppressWarnings("unchecked")
    public SliderElement(String label,
                         Observable<Long> currentValue,
                         long minValue,
                         long maxValue,
                         SliderElementOptions options,
                         ObjectProperty parent) {
        super("slider", parent);
        this.currentValue = currentValue;

        setLabel(label);

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

        if (options.getStep() instanceof Observable<?> obs) {
            setStep((Observable<Long>) obs);
        } else {
            setStep(((Number) options.getStep()).longValue());
        }

        setMinValue(minValue);
        setMaxValue(maxValue);

        setValue(currentValue);

        if (options.getDescription() instanceof Observable<?> obs) {
            setDescription((Observable<String>) obs);
        } else {
            setDescription((String) options.getDescription());
        }
    }

    public long getMaxValue() {
        var prop = getProperty("maxValue");
        if (prop instanceof LongProperty lp) return lp.getValue();
        return 100L;
    }

    public SliderElement setMaxValue(long maxValue) {
        var property = new LongProperty("maxValue", maxValue, this);
        setProperty(property);
        return this;
    }

    public SliderElement setMaxValue(Observable<Long> maxValue) {
        var property = new LongProperty("maxValue", maxValue.getValue(), this);
        maxValue.subscribe(value -> {
            setMaxValue(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public long getMinValue() {
        var prop = getProperty("minValue");
        if (prop instanceof LongProperty lp) return lp.getValue();
        return 0L;
    }

    public SliderElement setMinValue(long minValue) {
        var property = new LongProperty("minValue", minValue, this);
        setProperty(property);
        return this;
    }

    public SliderElement setMinValue(Observable<Long> minValue) {
        var property = new LongProperty("minValue", minValue.getValue(), this);
        minValue.subscribe(value -> {
            setMinValue(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public long getStep() {
        var prop = getProperty("step");
        if (prop instanceof LongProperty lp) return lp.getValue();
        return 1L;
    }

    public SliderElement setStep(long step) {
        var property = new LongProperty("step", step, this);
        setProperty(property);
        return this;
    }

    public SliderElement setStep(Observable<Long> step) {
        var property = new LongProperty("step", step.getValue(), this);
        step.subscribe(value -> {
            setStep(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public long getSliderValue() {
        var prop = getProperty("value");
        if (prop instanceof LongProperty lp) return lp.getValue();
        return 0L;
    }

    public SliderElement setValue(long value) {
        var existing = getProperty("value");
        LongProperty property = (existing instanceof LongProperty lp)
                ? lp
                : createValueProperty();
        property.setValue(value);
        setProperty(property);
        return this;
    }

    public SliderElement setValue(Observable<Long> value) {
        var existing = getProperty("value");
        LongProperty property = (existing instanceof LongProperty lp)
                ? lp
                : createValueProperty();
        property.setValue(value.getValue());
        value.subscribe(v -> {
            setValue(v);
            return property;
        });
        setProperty(property);
        return this;
    }

    private LongProperty createValueProperty() {
        LongProperty property = new LongProperty("value", 0L, this);
        property.addListener((player, data) -> {
            if (data instanceof Long l) {
                triggerListeners(player, l);
            }
        });
        return property;
    }

    public String getDescription() {
        var prop = getProperty("description");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public SliderElement setDescription(String description) {
        var existing = getProperty("description");
        StringProperty property = (existing instanceof StringProperty sp)
                ? sp
                : new StringProperty("description", "", this);
        property.setValue(description);
        setProperty(property);
        return this;
    }

    public SliderElement setDescription(Observable<String> description) {
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
    public SliderElement setVisibility(boolean visible) {
        super.setVisibility(visible);
        setProperty(new BooleanProperty("slider_visible", visible, this));
        return this;
    }

    @Override
    public SliderElement setVisibility(Observable<Boolean> visible) {
        super.setVisibility(visible);
        var property = new BooleanProperty("slider_visible", visible.getValue(), this);
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

        if (data instanceof Long l) {
            setValue(l);
            currentValue.setValue(l);
        }
    }
}