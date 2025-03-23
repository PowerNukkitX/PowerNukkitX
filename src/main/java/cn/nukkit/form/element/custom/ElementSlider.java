package cn.nukkit.form.element.custom;

import cn.nukkit.form.element.Element;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    public ElementSlider() {
        this("");
    }

    public ElementSlider(String text) {
        this(text, 1);
    }

    public ElementSlider(String text, float min) {
        this(text, min, Math.max(min, 100));
    }

    public ElementSlider(String text, float min, float max) {
        this(text, min, max, 1);
    }

    public ElementSlider(String text, float min, float max, int step) {
        this(text, min, max, step, 1);
    }

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

        return this.object;
    }
}
