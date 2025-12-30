package cn.nukkit.form.element.custom;

import cn.nukkit.form.element.Element;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a toggle (checkbox) element for custom forms.
 * Allows the user to select true/false.
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementToggle extends Element implements ElementCustom {

    private String text;
    private boolean defaultValue;
    private String tooltip;

    /**
     * Creates a toggle with empty text and default value false.
     */
    public ElementToggle() {
        this("", false, null);
    }

    /**
     * Creates a toggle with specified text and default value false.
     * @param text The toggle label
     */
    public ElementToggle(String text) {
        this(text, false, null);
    }

    public ElementToggle(String text, boolean defaultValue) {
        this(text, defaultValue, null);
    }

    /**
     * Serializes the toggle element to JSON.
     * @return The toggle as a JsonObject
     */
    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "toggle");
        this.object.addProperty("text", this.text);
        this.object.addProperty("default", this.defaultValue);

        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            this.object.addProperty("tooltip", this.tooltip);
        }

        return this.object;
    }
}
