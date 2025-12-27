package cn.nukkit.form.element;

import cn.nukkit.form.element.custom.ElementCustom;
import cn.nukkit.form.element.simple.ElementSimple;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a header element in a form, used to display section titles.
 * Implements both custom and simple element interfaces.
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementHeader extends Element implements ElementCustom, ElementSimple {
    /**
     * The text displayed in the header.
     */
    private String text;

    /**
     * Creates an empty header element.
     */
    public ElementHeader() {
        this("");
    }

    /**
     * Serializes the header element to JSON.
     *
     * @return The header as a JsonObject
     */
    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "header");
        this.object.addProperty("text", this.text);

        return this.object;
    }

    /**
     * Updates this header with another header's text.
     *
     * @param element The element to update from
     * @return The updated header
     */
    @Override
    public ElementSimple updateWith(ElementSimple element) {
        if (!(element instanceof ElementHeader header)) {
            return this;
        }

        return this.text(header.text());
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
