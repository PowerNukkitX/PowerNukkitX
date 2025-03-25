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

    public ElementInput() {
        this("");
    }

    public ElementInput(String text) {
        this(text, "");
    }

    public ElementInput(String text, String placeholder) {
        this(text, placeholder, "");
    }

    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "input");
        this.object.addProperty("text", this.text);
        this.object.addProperty("placeholder", this.placeholder);
        this.object.addProperty("default", this.defaultText);

        return this.object;
    }
}
