package cn.nukkit.form.element;

/**
 * A clickable button,used only for {@link cn.nukkit.form.window.FormWindowSimple FormWindowSimple}
 */
public class ElementButton {
    private String text = "";
    private ElementButtonImageData image;

    /**
     * create a button with image
     *
     * @param text the button text
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
    public ElementButton(String text, ElementButtonImageData image) {
        this.text = text;
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ElementButtonImageData getImage() {
        return image;
    }

    public void addImage(ElementButtonImageData image) {
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image;
    }

}
