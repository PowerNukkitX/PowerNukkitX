package cn.nukkit.form.element;

import cn.nukkit.form.element.custom.ElementCustom;
import cn.nukkit.form.element.simple.ElementSimple;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a label element in a form, used to display static text.
 * Implements both custom and simple element interfaces.
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementLabel extends Element implements ElementCustom, ElementSimple {
    /**
     * The text displayed in the label.
     */
    private String text;

    /**
     * Creates an empty label element.
     */
    public ElementLabel() {
        this("");
    }

    /**
     * Serializes the label element to JSON.
     *
     * @return The label as a JsonObject
     */
    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "label");
        this.object.addProperty("text", this.text);

        return this.object;
    }

    /**
     * Updates this label with another label's text.
     *
     * @param element The element to update from
     * @return The updated label
     */
    @Override
    public ElementSimple updateWith(ElementSimple element) {
        if (!(element instanceof ElementLabel label)) {
            return this;
        }

        return this.text(label.text());
    }

    /**
     * Indicates that this element does not have a response.
     *
     * @return false
     */
    @Override
    public boolean hasResponse() {
        return false;
    }
}
