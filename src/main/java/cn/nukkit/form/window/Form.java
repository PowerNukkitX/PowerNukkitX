package cn.nukkit.form.window;

import cn.nukkit.Player;
import cn.nukkit.form.response.Response;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
public abstract class Form<T extends Response> {
    protected final JsonObject object = new JsonObject();
    protected final Object2ObjectOpenHashMap<String, Object> meta = new Object2ObjectOpenHashMap<>();

    protected ObjectArraySet<Player> viewers = new ObjectArraySet<>();

    protected String title = "";

    @Setter(AccessLevel.NONE)
    protected Consumer<Player> closed = player -> {};
    @Setter(AccessLevel.NONE)
    protected BiConsumer<Player, T> submitted = (player, response) -> {};

    public Form(String title) {
        this.title = title;
    }

    public void supplyClosed(Player player) {
        this.viewers.remove(player);
        if (this.closed != null)
            this.closed.accept(player);
    }

    public void supplySubmitted(Player player, T data) {
        this.viewers.remove(player);
        if (this.submitted != null)
            this.submitted.accept(player, data);
    }

    public Form<T> onClose(Consumer<Player> closed) {
        this.closed = closed;
        return this;
    }

    public Form<T> onSubmit(BiConsumer<Player, T> submitted) {
        this.submitted = submitted;
        return this;
    }

    public Form<T> send(Player player) {
        if (this.isViewer(player)) {
            return this;
        }

        this.viewers.add(player);

        player.sendForm(this);
        return this;
    }

    public Form<T> send(Player player, int id) {
        if (this.isViewer(player)) {
            return this;
        }

        this.viewers.add(player);

        player.sendForm(this, id);
        return this;
    }

    public Form<T> sendUpdate(Player player) {
        if (!this.isViewer(player)) {
            this.send(player);
            return this;
        }

        player.updateForm(this);
        return this;
    }

    public boolean handle(Player player, String formData) {
        return formData != null && !formData.equals("null");
    }

    public abstract Response respond(Player player, String formData);

    public boolean isViewer(Player player) {
        return this.viewers.contains(player);
    }

    @SuppressWarnings("unchecked")
    public <M> M getMeta(String key) {
        return (M) this.meta.get(key);
    }

    @SuppressWarnings("unchecked")
    public <M> M getMeta(String key, M defaultValue) {
        Object value = this.getMeta(key);
        return value == null ? defaultValue : (M) value;
    }

    public <M> void putMeta(String key, M object) {
        this.meta.put(key, object);
    }

    public abstract String windowType();

    public abstract String toJson();
}
