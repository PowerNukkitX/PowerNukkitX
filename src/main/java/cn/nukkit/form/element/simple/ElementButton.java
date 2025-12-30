package cn.nukkit.form.element.simple;

import cn.nukkit.form.element.Element;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a button element for use in {@link cn.nukkit.form.window.SimpleForm}.
 * Supports optional images and text for display in simple forms.
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementButton extends Element implements ElementSimple {
    /**
     * An empty array of ElementButton, for convenience.
     */
    public static ElementButton[] EMPTY_LIST = new ElementButton[0];

    /**
     * The button text shown to the user.
     */
    protected String text;
    /**
     * An optional image to display with the button.
     */
    protected ButtonImage image;

    /**
     * Creates a button with empty text and no image.
     */
    public ElementButton() {
        this("");
    }

    /**
     * Creates a button with the specified text and no image.
     * @param text The button text
     */
    public ElementButton(String text) {
        this(text, null);
    }

    /**
     * Serializes the button element to JSON for the Minecraft client.
     * @return The button as a JsonObject
     */
    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "button");
        this.object.addProperty("text", this.text);
        if (this.image != null) {
            this.object.add("image", this.image.toJson());
        }
        return this.object;
    }

    /**
     * Updates this button with another button's properties (text and image).
     * @param element The element to update from
     * @return The updated button
     */
    @Override
    public ElementSimple updateWith(ElementSimple element) {
        if (!(element instanceof ElementButton button)) {
            return this;
        }
        return this.text(button.text())
                .image(button.image());
    }
}
