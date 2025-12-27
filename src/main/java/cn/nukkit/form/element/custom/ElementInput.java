package cn.nukkit.form.element.custom;

import cn.nukkit.form.element.Element;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents an input element for custom forms.
 * Allows the user to enter text, with optional placeholder and default value.
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementInput extends Element implements ElementCustom {
    private String text;
    private String placeholder;
    private String defaultText;

    /**
     * Creates an input element with empty text, placeholder, and default value.
     */
    public ElementInput() {
        this("");
    }

    /**
     * Creates an input element with specified text and empty placeholder/default.
     * @param text The input label
     */
    public ElementInput(String text) {
        this(text, "");
    }

    /**
     * Creates an input element with specified text and placeholder, empty default.
     * @param text The input label
     * @param placeholder The placeholder text
     */
    public ElementInput(String text, String placeholder) {
        this(text, placeholder, "");
    }

    /**
     * Serializes the input element to JSON.
     * @return The input as a JsonObject
     */
    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "input");
        this.object.addProperty("text", this.text);
        this.object.addProperty("placeholder", this.placeholder);
        this.object.addProperty("default", this.defaultText);

        return this.object;
    }
}
