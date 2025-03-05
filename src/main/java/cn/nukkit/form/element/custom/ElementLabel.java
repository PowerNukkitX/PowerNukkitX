package cn.nukkit.form.element.custom;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementLabel extends ElementCustom {
    private String text;

    public ElementLabel() {
        this("");
    }

    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "label");
        this.object.addProperty("text", this.text);

        return this.object;
    }
}
