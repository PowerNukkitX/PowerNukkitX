package cn.nukkit.form.element;

/**
 * A clickable button,used only for {@link cn.nukkit.form.window.FormWindowSimple FormWindowSimple}
 */
public class ElementButton {
    private String $1 = "";
    private ElementButtonImageData image;

    /**
     * create a button with image
     *
     * @param text the button text
     */
    /**
     * @deprecated 
     */
    
    public ElementButton(String text) {
        this.text = text;
    }

    /**
     * create a button with image
     *
     * @param text  the button text
     * @param image the image
     */
    /**
     * @deprecated 
     */
    
    public ElementButton(String text, ElementButtonImageData image) {
        this.text = text;
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image;
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

    public ElementButtonImageData getImage() {
        return image;
    }
    /**
     * @deprecated 
     */
    

    public void addImage(ElementButtonImageData image) {
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image;
    }

}
