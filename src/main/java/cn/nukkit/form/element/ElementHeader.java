package cn.nukkit.form.element;

import cn.nukkit.form.element.custom.ElementCustom;
import cn.nukkit.form.element.simple.ElementSimple;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementHeader extends Element implements ElementCustom, ElementSimple {
    private String text;

    public ElementHeader() {
        this("");
    }

    @Override
    public JsonObject toJson() {
        this.object.addProperty("type", "header");
        this.object.addProperty("text", this.text);

        return this.object;
    }

    @Override
    public ElementSimple updateWith(ElementSimple element) {
        if (!(element instanceof ElementHeader header)) {
            return this;
        }

        return this.text(header.text());
    }

    @Override
    public boolean hasResponse() {
        return false;
    }
}
