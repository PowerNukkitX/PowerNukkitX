package cn.nukkit.form.element.simple;

import cn.nukkit.form.element.Element;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The button object used to add buttons within {@link cn.nukkit.form.window.SimpleForm}
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementButton extends Element implements ElementSimple {
    public static ElementButton[] EMPTY_LIST = new ElementButton[0];

    /**
     * The button text shown
     */
    protected String text;
    /**
     * An optional image to send with the button
     */
    protected ButtonImage image;

    public ElementButton() {
        this("");
    }

    public ElementButton(String text) {
        this(text, null);
    }

    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "button");
        this.object.addProperty("text", this.text);

        if (this.image != null) {
            this.object.add("image", this.image.toJson());
        }

        return this.object;
    }

    @Override
    public ElementSimple updateWith(ElementSimple element) {
        if (!(element instanceof ElementButton button)) {
            return this;
        }

        return this.text(button.text())
                .image(button.image());
    }
}
