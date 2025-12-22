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
    protected String tooltip;
    protected int defaultOption;

    public ElementDropdown() {
        this("", new ArrayList<>(), null, 0);
    }

    public ElementDropdown(String text) {
        this(text, new ArrayList<>(), null, 0);
    }

    public ElementDropdown(String text, String tooltip) {
        this(text, new ArrayList<>(), tooltip, 0);
    }

    public ElementDropdown(String text, List<String> options) {
        this(text, options, null, 0);
    }

    public ElementDropdown(String text, List<String> options, String tooltip) {
        this(text, options, tooltip, 0);
    }

    public ElementDropdown addOption(String option) {
        return this.addOption(option, false, null);
    }

    public ElementDropdown addOption(String option, String tooltip) {
        return this.addOption(option, false, tooltip);
    }

    public ElementDropdown addOption(String option, boolean isDefault) {
        return this.addOption(option, isDefault, null);
    }

    public ElementDropdown addOption(String option, boolean isDefault, String tooltip) {
        if (isDefault) {
            this.defaultOption = this.options.size();
        }

        this.options.add(option);

        if (tooltip != null && !tooltip.isEmpty()) {
            this.tooltip = tooltip;
        }

        return this;
    }

    @Override
    public JsonObject toJson() {
        Preconditions.checkArgument(
                this.defaultOption >= 0 && this.defaultOption < this.options.size(),
                "Default option not an index"
        );

        this.object.addProperty("type", "dropdown");
        this.object.addProperty("text", this.text);
        this.object.addProperty("default", this.defaultOption);

        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            this.object.addProperty("tooltip", this.tooltip);
        }

        JsonArray optionsArray = new JsonArray();
        this.options.forEach(optionsArray::add);
        this.object.add("options", optionsArray);

        return this.object;
    }
}
