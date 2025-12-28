package cn.nukkit.form.element.custom;

import cn.nukkit.form.element.Element;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementStepSlider extends Element implements ElementCustom {

    protected String text;
    protected List<String> steps;
    protected int defaultStep;
    protected String tooltip;

    public ElementStepSlider() {
        this("", new ArrayList<>(), 0, null);
    }

    public ElementStepSlider(String text) {
        this(text, new ArrayList<>(), 0, null);
    }

    public ElementStepSlider(String text, List<String> steps) {
        this(text, steps, 0, null);
    }

    public ElementStepSlider(String text, List<String> steps, int defaultStep) {
        this(text, steps, defaultStep, null);
    }

    public ElementStepSlider addStep(String step) {
        return this.addStep(step, false);
    }

    public ElementStepSlider addStep(String step, boolean isDefault) {
        if (isDefault) {
            this.defaultStep = this.steps.size();
        }

        this.steps.add(step);
        return this;
    }

    @Override
    public JsonObject toJson() {
        Preconditions.checkArgument(
                this.defaultStep >= 0 && this.defaultStep < this.steps.size(),
                "Default option not within range"
        );

        this.object.addProperty("type", "step_slider");
        this.object.addProperty("text", this.text);
        this.object.addProperty("default", this.defaultStep);

        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            this.object.addProperty("tooltip", this.tooltip);
        }

        JsonArray stepsArray = new JsonArray();
        this.steps.forEach(stepsArray::add);
        this.object.add("steps", stepsArray);

        return this.object;
    }
}
