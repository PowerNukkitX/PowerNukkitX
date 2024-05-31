package cn.nukkit.form.element;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A slider letting the player choose between different values
 */
public class ElementStepSlider extends Element {
    @SuppressWarnings("unused")
    private final String $1 = "step_slider"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String $2 = "";
    private List<String> steps;
    @SerializedName("default")
    private int $3 = 0;

    /**
     * create a step slider.
     *
     * @param text the text displayed above the slider
     */
    /**
     * @deprecated 
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
    /**
     * @deprecated 
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
    /**
     * @deprecated 
     */
    
    public ElementStepSlider(String text, List<String> steps, int defaultStep) {
        this.text = text;
        this.steps = steps;
        this.defaultStepIndex = defaultStep;
    }
    /**
     * @deprecated 
     */
    

    public int getDefaultStepIndex() {
        return defaultStepIndex;
    }
    /**
     * @deprecated 
     */
    

    public void setDefaultOptionIndex(int index) {
        if (index >= steps.size()) return;
        this.defaultStepIndex = index;
    }
    /**
     * @deprecated 
     */
    

    public String getText() {
        return text;
    }
    /**
     * @deprecated 
     */
    

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getSteps() {
        return steps;
    }
    /**
     * @deprecated 
     */
    

    public void addStep(String step) {
        addStep(step, false);
    }
    /**
     * @deprecated 
     */
    

    public void addStep(String step, boolean isDefault) {
        steps.add(step);
        if (isDefault) this.defaultStepIndex = steps.size() - 1;
    }

}
