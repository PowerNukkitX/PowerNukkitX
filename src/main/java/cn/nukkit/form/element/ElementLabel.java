package cn.nukkit.form.element;

/**
 * Pure text content element
 */
public class ElementLabel extends Element {
    @SuppressWarnings("unused")
    private final String $1 = "label"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String $2 = "";

    /**
     * create a label
     *
     * @param text to be displayed
     */
    /**
     * @deprecated 
     */
    
    public ElementLabel(String text) {
        this.text = text;
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
}
