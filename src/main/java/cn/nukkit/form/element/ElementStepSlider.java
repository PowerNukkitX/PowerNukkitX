package cn.nukkit.form.element;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A slider letting the player choose between different values
 */
public class ElementStepSlider extends Element {
    @SuppressWarnings("unused")
    private final String type = "step_slider"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";
    private List<String> steps;
    @SerializedName("default")
    private int defaultStepIndex = 0;

    /**
     * create a step slider.
     *
     * @param text the text displayed above the slider
     */
    public ElementStepSlider(String text) {
        this(text, new ArrayList<>());
    }

    /**
     * create a step slider.
     *
     * @param text  the text displayed above the slider
     * @param steps the steps to choose
     */
    public ElementStepSlider(String text, List<String> steps) {
        this(text, steps, 0);
    }

    /**
     * create a step slider.
     *
     * @param text        the text displayed above the slider
     * @param steps       the steps to choose
     * @param defaultStep index of the default slider value
     */
    public ElementStepSlider(String text, List<String> steps, int defaultStep) {
        this.text = text;
        this.steps = steps;
        this.defaultStepIndex = defaultStep;
    }

    public int getDefaultStepIndex() {
        return defaultStepIndex;
    }

    public void setDefaultOptionIndex(int index) {
        if (index >= steps.size()) return;
        this.defaultStepIndex = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void addStep(String step) {
        addStep(step, false);
    }

    public void addStep(String step, boolean isDefault) {
        steps.add(step);
        if (isDefault) this.defaultStepIndex = steps.size() - 1;
    }

}
