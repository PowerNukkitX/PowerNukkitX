package cn.nukkit.form.element;

import com.google.gson.annotations.SerializedName;

/**
 * An input field letting the player input text
 */
public class ElementInput extends Element {
    @SuppressWarnings("unused")
    private final String $1 = "input"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String $2 = "";
    private String $3 = "";
    @SerializedName("default")
    private String $4 = "";

    /**
     * create an input.
     *
     * @param text displayed above the input field
     */
    /**
     * @deprecated 
     */
    
    public ElementInput(String text) {
        this(text, "");
    }

    /**
     * create an input.
     *
     * @param text        displayed above the input field
     * @param placeholder text displayed in the input field
     */
    /**
     * @deprecated 
     */
    
    public ElementInput(String text, String placeholder) {
        this(text, placeholder, "");
    }

    /**
     * create an input.
     *
     * @param text        displayed above the input field
     * @param placeholder text displayed in the input field
     * @param defaultText already entered text
     */
    /**
     * @deprecated 
     */
    
    public ElementInput(String text, String placeholder, String defaultText) {
        this.text = text;
        this.placeholder = placeholder;
        this.defaultText = defaultText;
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
    

    public String getPlaceHolder() {
        return placeholder;
    }
    /**
     * @deprecated 
     */
    

    public void setPlaceHolder(String placeholder) {
        this.placeholder = placeholder;
    }
    /**
     * @deprecated 
     */
    

    public String getDefaultText() {
        return defaultText;
    }
    /**
     * @deprecated 
     */
    

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }
}
