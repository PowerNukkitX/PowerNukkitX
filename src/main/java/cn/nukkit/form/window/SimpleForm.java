package cn.nukkit.form.window;

import cn.nukkit.Player;
import cn.nukkit.form.element.simple.ButtonImage;
import cn.nukkit.form.element.simple.ElementButton;
import cn.nukkit.form.response.SimpleResponse;
import com.google.gson.JsonArray;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
public class SimpleForm extends Form<SimpleResponse> {
    protected String content = "";

    protected Object2ObjectArrayMap<ElementButton, Consumer<Player>> buttons = new Object2ObjectArrayMap<>();

    public SimpleForm(String title) {
        super(title);
    }

    public SimpleForm(String title, String content) {
        super(title);

        this.content = content;
    }

    @Override
    public SimpleForm title(String title) {
        return (SimpleForm) super.title(title);
    }

    public SimpleForm addButton(ElementButton element) {
        return this.addButton(element, null);
    }

    public SimpleForm addButton(ElementButton element, Consumer<Player> callback) {
        this.buttons.put(element, callback);
        return this;
    }

    public SimpleForm addButton(String text) {
        return this.addButton(text, null, null);
    }

    public SimpleForm addButton(String text, Consumer<Player> callback) {
        return this.addButton(text, null, callback);
    }

    public SimpleForm addButton(String text, ButtonImage image) {
        return this.addButton(text, image, null);
    }

    public SimpleForm addButton(String text, ButtonImage image, Consumer<Player> callback) {
        return this.addButton(new ElementButton(text, image), callback);
    }

    public ElementButton updateElement(int index, ElementButton newElement) {
        if (this.buttons.size() > index) {
            return this.buttons.keySet().toArray(ElementButton.EMPTY_LIST)[index]
                    .text(newElement.text())
                    .image(newElement.image());
        }
        return null;
    }

    public void updateElement(int index, ElementButton newElement, Consumer<Player> callback) {
        ElementButton element = this.updateElement(index, newElement);
        if (element != null) {
            this.buttons.put(element, callback);
        }
    }

    public void removeElement(int index) {
        this.removeElement(this.buttons.keySet().toArray(ElementButton.EMPTY_LIST)[index]);
    }

    public void removeElement(ElementButton elementButton) {
        this.buttons.remove(elementButton);
    }

    @Override
    public SimpleForm onSubmit(BiConsumer<Player, SimpleResponse> submitted) {
        return (SimpleForm) super.onSubmit(submitted);
    }

    @Override
    public SimpleForm onClose(Consumer<Player> closed) {
        return (SimpleForm) super.onClose(closed);
    }

    @Override
    public SimpleForm send(Player player) {
        return (SimpleForm) super.send(player);
    }

    @Override
    public SimpleForm send(Player player, int id) {
        return (SimpleForm) super.send(player, id);
    }

    @Override
    public SimpleForm sendUpdate(Player player) {
        return (SimpleForm) super.sendUpdate(player);
    }

    @Override
    public String windowType() {
        return "form";
    }

    @Override
    public String toJson() {
        this.object.addProperty("type", this.windowType());
        this.object.addProperty("title", this.title);
        this.object.addProperty("content", this.content);

        JsonArray buttons = new JsonArray();
        this.buttons()
                .keySet()
                .forEach(element -> buttons.add(element.toJson()));
        this.object.add("buttons", buttons);

        return this.object.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SimpleResponse respond(Player player, String formData) {
        if (!super.handle(player, formData)) {
            this.supplyClosed(player);
            return null;
        }


        Map.Entry<ElementButton, Consumer<Player>>[] entries = this.buttons.entrySet().toArray(Map.Entry[]::new);

        int clickedId = -1;
        try {
            clickedId = Integer.parseInt(formData);
        } catch (Exception ignored) {}

        if (entries.length < clickedId || clickedId == -1) {
            return null;
        }

        Map.Entry<ElementButton, Consumer<Player>> entry = entries[clickedId];

        Consumer<Player> action = entry.getValue();
        if (action != null) {
            action.accept(player);
        }

        SimpleResponse response = new SimpleResponse(clickedId, entry.getKey());
        this.supplySubmitted(player, response);
        return response;
    }

    @Override
    public <M> SimpleForm putMeta(String key, M object) {
        return (SimpleForm) super.putMeta(key, object);
    }
}
