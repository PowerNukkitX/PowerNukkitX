package cn.nukkit.form.element.custom;

import cn.nukkit.form.element.Element;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ElementDropdown extends Element implements ElementCustom {
    protected String text;
    protected List<String> options;
    protected int defaultOption;

    public ElementDropdown() {
        this("");
    }

    public ElementDropdown(String text) {
        this(text, new ArrayList<>());
    }

    public ElementDropdown(String text, List<String> options) {
        this(text, options, 0);
    }

    public ElementDropdown addOption(String option) {
        return this.addOption(option, false);
    }

    public ElementDropdown addOption(String option, boolean isDefault) {
        if (isDefault) {
            this.defaultOption = this.options.size();
        }

        this.options.add(option);

        return this;
    }

    @Override
    public JsonObject toJson() {
        Preconditions.checkArgument(0 > this.defaultOption || this.defaultOption < this.options.size(),
                "Default option not an index");

        this.object.addProperty("type", "dropdown");
        this.object.addProperty("text", this.text);
        this.object.addProperty("default", this.defaultOption);

        JsonArray optionsArray = new JsonArray();
        this.options.forEach(optionsArray::add);
        this.object.add("options", optionsArray);

        return this.object;
    }
}
