package cn.nukkit.form.window;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementDivider;
import cn.nukkit.form.element.ElementHeader;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.custom.ElementCustom;
import cn.nukkit.form.element.custom.ElementDropdown;
import cn.nukkit.form.element.custom.ElementInput;
import cn.nukkit.form.element.custom.ElementSlider;
import cn.nukkit.form.element.custom.ElementStepSlider;
import cn.nukkit.form.element.custom.ElementToggle;
import cn.nukkit.form.response.CustomResponse;
import cn.nukkit.form.response.ElementResponse;
import cn.nukkit.utils.JSONUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
public class CustomForm extends Form<CustomResponse> {
    protected static Type LIST_STRING_TYPE = new TypeToken<List<String>>(){}.getType();

    protected ObjectArrayList<ElementCustom> elements = new ObjectArrayList<>();

    public CustomForm(String title) {
        super(title);
    }

    public CustomForm addElement(ElementCustom element) {
        this.elements.add(element);
        return this;
    }

    @Override
    public CustomForm onSubmit(BiConsumer<Player, CustomResponse> submitted) {
        return (CustomForm) super.onSubmit(submitted);
    }

    @Override
    public CustomForm onClose(Consumer<Player> callback) {
        return (CustomForm) super.onClose(callback);
    }

    @Override
    public CustomForm send(Player player) {
        return (CustomForm) super.send(player);
    }

    @Override
    public CustomForm send(Player player, int id) {
        return (CustomForm) super.send(player, id);
    }

    @Override
    public CustomForm sendUpdate(Player player) {
        return (CustomForm) super.sendUpdate(player);
    }

    @Override
    public String windowType() {
        return "custom_form";
    }

    @Override
    public String toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", this.windowType());
        object.addProperty("title", this.title);

        JsonArray elementArray = new JsonArray();
        this.elements().forEach(element -> elementArray.add(element.toJson()));

        object.add("content", elementArray);
        return object.toString();
    }

    @Override
    public CustomResponse respond(Player player, String formData) {
        if (!super.handle(player, formData)) {
            this.supplyClosed(player);
            return null;
        }

        CustomResponse response = new CustomResponse();

        List<String> elementResponses = JSONUtils.from(formData, LIST_STRING_TYPE);

        for (int i = 0, j = 0; i < this.elements.size(); i++) {
            ElementCustom element = this.elements.get(i);

            Object elementResponse = null;
            String responseData = element.hasResponse() ? elementResponses.get(j++) : "";

            switch (element) {
                case ElementDropdown dropdown -> {
                    int index = Integer.parseInt(responseData);
                    String option = dropdown.options().get(index);
                    elementResponse = new ElementResponse(index, option);
                }
                case ElementInput input -> elementResponse = responseData;
                case ElementSlider slider -> elementResponse = Float.parseFloat(responseData);
                case ElementStepSlider stepSlider -> {
                    int index = Integer.parseInt(responseData);
                    String step = stepSlider.steps().get(index);
                    elementResponse = new ElementResponse(index, step);
                }
                case ElementToggle toggle -> elementResponse = Boolean.parseBoolean(responseData);
                case ElementDivider divider -> elementResponse = divider.text();
                case ElementHeader header -> elementResponse = header.text();
                case ElementLabel label -> elementResponse = label.text();
                default -> {}
            }

            response.setResponse(i, elementResponse);
        }

        this.supplySubmitted(player, response);
        return response;
    }

    @Override
    public <M> CustomForm putMeta(String key, M object) {
        return (CustomForm) super.putMeta(key, object);
    }
}
