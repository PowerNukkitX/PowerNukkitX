package cn.nukkit.form.element;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A dropdown menu letting the player choose an option between different options
 */
public class ElementDropdown extends Element {
    @SuppressWarnings("unused")
    private final String type = "dropdown"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";
    private List<String> options;
    @SerializedName("default")
    private int defaultOptionIndex = 0;

    /**
     * create a dropdown and options is empty.
     *
     * @param text the dropdown text
     */
    public ElementDropdown(String text) {
        this(text, new ArrayList<>());
    }

    /**
     * create a dropdown.
     *
     * @param text    the dropdown text
     * @param options the options
     */
    public ElementDropdown(String text, List<String> options) {
        this(text, options, 0);
    }

    /**
     * create a dropdown.
     *
     * @param text          the dropdown text
     * @param options       the options
     * @param defaultOption the index of default option(start from 0)
     */
    public ElementDropdown(String text, List<String> options, int defaultOption) {
        this.text = text;
        this.options = options;
        this.defaultOptionIndex = defaultOption;
    }

    public int getDefaultOptionIndex() {
        return defaultOptionIndex;
    }

    public void setDefaultOptionIndex(int index) {
        if (index >= options.size()) return;
        this.defaultOptionIndex = index;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addOption(String option) {
        addOption(option, false);
    }

    public void addOption(String option, boolean isDefault) {
        options.add(option);
        if (isDefault) this.defaultOptionIndex = options.size() - 1;
    }

}
