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

/**
 * Abstract class used to dynamically generate and send forms to players.
 *
 * @param <T> A response object
 */
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

    @Getter
    protected T response;

    public Form(String title) {
        this.title = title;
    }

    /**
     * Internally used to accept the consumer to execute when the form was closed
     * @param player The player who closed the form
     */
    public void supplyClosed(Player player) {
        if (this.closed != null)
            this.closed.accept(player);
    }

    /**
     * Internally used to accept the consumer to execute when the form was submitted
     * @param player The player who submitted the form
     * @param data The data submitted by the player
     */
    public void supplySubmitted(Player player, T data) {
        this.response = data;
        if (this.submitted != null)
            this.submitted.accept(player, data);
    }

    /**
     * @param closed The consumer executed when a player closes the form
     * @return The form
     */
    public Form<T> onClose(Consumer<Player> closed) {
        this.closed = closed;
        return this;
    }

    /**
     * @param submitted The consumer executed when a player submits the form
     * @return The form
     */
    public Form<T> onSubmit(BiConsumer<Player, T> submitted) {
        this.submitted = submitted;
        return this;
    }

    /**
     * Sends the form to a player
     * @param player The player to send the form to
     * @return The form
     */
    public Form<T> send(Player player) {
        if (this.isViewer(player)) {
            return this;
        }

        this.viewers.add(player);

        player.sendForm(this);
        return this;
    }

    /**
     * Sends the form to a player
     * @param player The player to send the form to
     * @param id The ID to use internally for the player
     * @return The form
     */
    public Form<T> send(Player player, int id) {
        if (this.isViewer(player)) {
            return this;
        }

        this.viewers.add(player);

        player.sendForm(this, id);
        return this;
    }

    /**
     * Update the form while the player still has the form open
     * Not recommended for scrolling content
     *
     * @param player The player to send the update to
     * @return The form
     */
    public Form<T> sendUpdate(Player player) {
        if (!this.isViewer(player)) {
            this.send(player);
            return this;
        }

        player.updateForm(this);
        return this;
    }

    public boolean handle(Player player, String formData) {
        this.viewers.remove(player);
        player.checkClosedForms();

        return formData != null && !formData.equals("null");
    }

    public abstract Response respond(Player player, String formData);

    public boolean isViewer(Player player) {
        return this.viewers.contains(player);
    }

    /**
     * Get the value of a key
     *
     * @param key The key
     * @return The value
     * @param <M> Any
     */
    @SuppressWarnings("unchecked")
    public <M> M getMeta(String key) {
        return (M) this.meta.get(key);
    }

    /**
     * Get the value of a key
     *
     * @param key The key
     * @param defaultValue The default value
     * @return If present, the value. Otherwise, returns the default value
     * @param <M> Any
     */
    @SuppressWarnings("unchecked")
    public <M> M getMeta(String key, M defaultValue) {
        Object value = this.getMeta(key);
        return value == null ? defaultValue : (M) value;
    }

    /**
     * Put data inside here, e.g. to identify which form has been opened
     *
     * @param key The key
     * @param object The value
     * @param <M> Any
     */
    public <M> Form<?> putMeta(String key, M object) {
        this.meta.put(key, object);
        return this;
    }

    public abstract String windowType();

    public abstract String toJson();
}
