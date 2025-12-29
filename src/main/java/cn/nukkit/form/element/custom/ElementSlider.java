package cn.nukkit.form.element.custom;

import cn.nukkit.form.element.Element;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a slider element for custom forms.
 * Allows the user to select a value within a range.
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementSlider extends Element implements ElementCustom {

    private String text;
    private float min;
    private float max;
    private int step;
    private float defaultValue;
    private String tooltip;

    /**
     * Creates a slider with empty text and default range.
     */
    public ElementSlider() {
        this("", 1, 100, 1, 1, null);
    }

    /**
     * Creates a slider with specified text and default min value.
     * @param text The slider label
     */
    public ElementSlider(String text) {
        this(text, 1, 100, 1, 1, null);
    }

    /**
     * Creates a slider with specified text and min value, default max value.
     * @param text The slider label
     * @param min The minimum value
     */
    public ElementSlider(String text, float min) {
        this(text, min, Math.max(min, 100), 1, min, null);
    }

    /**
     * Creates a slider with specified text, min, and max values, default step.
     * @param text The slider label
     * @param min The minimum value
     * @param max The maximum value
     */
    public ElementSlider(String text, float min, float max) {
        this(text, min, max, 1, min, null);
    }

    /**
     * Creates a slider with specified text, min, max, and step values, default value.
     * @param text The slider label
     * @param min The minimum value
     * @param max The maximum value
     * @param step The step value
     */
    public ElementSlider(String text, float min, float max, int step) {
        this(text, min, max, step, min, null);
    }

    public ElementSlider(String text, float min, float max, int step, float defaultValue) {
        this(text, min, max, step, defaultValue, null);
    }

    /**
     * Serializes the slider element to JSON.
     * @return The slider as a JsonObject
     */
    @Override
    public JsonObject toJson() {
        Preconditions.checkArgument(this.min < this.max,
                "Maximum slider value must exceed the minimum value");
        Preconditions.checkArgument(this.defaultValue >= this.min && this.defaultValue <= this.max,
                "Default value out of range");

        this.object.addProperty("type", "slider");
        this.object.addProperty("text", this.text);
        this.object.addProperty("min", this.min);
        this.object.addProperty("max", this.max);
        this.object.addProperty("step", this.step);
        this.object.addProperty("default", this.defaultValue);

        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            this.object.addProperty("tooltip", this.tooltip);
        }

        return this.object;
    }
}
