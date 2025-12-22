package cn.nukkit.form.element.custom;

import cn.nukkit.form.element.Element;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementInput extends Element implements ElementCustom {

    private String text;
    private String placeholder;
    private String defaultText;
    private String tooltip;

    public ElementInput() {
        this("", "", "", "");
    }

    public ElementInput(String text) {
        this(text, "", "", "");
    }

    public ElementInput(String text, String placeholder) {
        this(text, placeholder, "", "");
    }

    public ElementInput(String text, String placeholder, String defaultText) {
        this(text, placeholder, defaultText, "");
    }

    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "input");
        this.object.addProperty("text", this.text);
        this.object.addProperty("placeholder", this.placeholder);
        this.object.addProperty("default", this.defaultText);

        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            this.object.addProperty("tooltip", this.tooltip);
        }

        return this.object;
    }
}
