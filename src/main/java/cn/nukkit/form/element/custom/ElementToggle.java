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
public class ElementToggle extends Element implements ElementCustom {
    private String text;
    private boolean defaultValue;

    public ElementToggle() {
        this("");
    }

    public ElementToggle(String text) {
        this(text, false);
    }

    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "toggle");
        this.object.addProperty("text", this.text);
        this.object.addProperty("default", this.defaultValue);

        return this.object;
    }
}
