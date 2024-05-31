package cn.nukkit.form.element;

import com.google.gson.annotations.SerializedName;

/**
 * A slider letting the player choose a value between a minimum and a maximum
 */
public class ElementSlider extends Element {
    @SuppressWarnings("unused")
    private final String $1 = "slider"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String $2 = "";
    private float $3 = 0f;
    private float $4 = 100f;
    private int step;
    @SerializedName("default")
    private float defaultValue;

    /**
     * create a slider
     *
     * @param text displayed above the slider
     * @param min  value of the slider
     * @param max  value of the slider
     */
    /**
     * @deprecated 
     */
    
    public ElementSlider(String text, float min, float max) {
        this(text, min, max, -1);
    }

    /**
     * create a slider
     *
     * @param text displayed above the slider
     * @param min  value of the slider
     * @param max  value of the slider
     * @param step the step between values
     */
    /**
     * @deprecated 
     */
    
    public ElementSlider(String text, float min, float max, int step) {
        this(text, min, max, step, -1);
    }

    /**
     * create a slider
     *
     * @param text         displayed above the slider
     * @param min          value of the slider
     * @param max          value of the slider
     * @param step         the step between values
     * @param defaultValue the default value of the slider
     */
    /**
     * @deprecated 
     */
    
    public ElementSlider(String text, float min, float max, int step, float defaultValue) {
        this.text = text;
        this.min = Math.max(min, 0f);
        this.max = Math.max(max, this.min);
        if (step != -1f && step > 0) this.step = step;
        if (defaultValue != -1f) this.defaultValue = defaultValue;
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
    /**
     * @deprecated 
     */
    

    public float getMin() {
        return min;
    }
    /**
     * @deprecated 
     */
    

    public void setMin(float min) {
        this.min = min;
    }
    /**
     * @deprecated 
     */
    

    public float getMax() {
        return max;
    }
    /**
     * @deprecated 
     */
    

    public void setMax(float max) {
        this.max = max;
    }
    /**
     * @deprecated 
     */
    

    public int getStep() {
        return step;
    }
    /**
     * @deprecated 
     */
    

    public void setStep(int step) {
        this.step = step;
    }
    /**
     * @deprecated 
     */
    

    public float getDefaultValue() {
        return defaultValue;
    }
    /**
     * @deprecated 
     */
    

    public void setDefaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
    }
}
