package cn.nukkit.form.element;

import cn.nukkit.form.element.custom.ElementCustom;
import cn.nukkit.form.element.simple.ElementSimple;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a divider element in a form, used to visually separate sections.
 * Implements both custom and simple element interfaces.
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementDivider extends Element implements ElementCustom, ElementSimple {
    /**
     * The text displayed in the divider.
     */
    private String text;

    /**
     * Creates an empty divider element.
     */
    public ElementDivider() {
        this("");
    }

    /**
     * Serializes the divider element to JSON.
     *
     * @return The divider as a JsonObject
     */
    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "divider");
        this.object.addProperty("text", this.text);

        return this.object;
    }

    /**
     * Updates this divider with another divider's text.
     *
     * @param element The element to update from
     * @return The updated divider
     */
    @Override
    public ElementSimple updateWith(ElementSimple element) {
        if (!(element instanceof ElementDivider divider)) {
            return this;
        }

        return this.text(divider.text());
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
