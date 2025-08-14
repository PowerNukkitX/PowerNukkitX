package cn.nukkit.form.element.custom;

import cn.nukkit.form.element.Element;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class ElementSubmit extends Element implements ElementCustom {
    protected String text;

    public ElementSubmit() {
        this("");
    }

    public ElementSubmit(String text) {
        this.text = text;
    }

    @Override
    public JsonObject toJson() {
        return this.object;
    }
}
