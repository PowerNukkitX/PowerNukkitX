package cn.nukkit.form.element.simple;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents an image used in a {@link ElementButton} for a simple form.
 * Supports images from resource packs (PATH) or URLs (URL).
 */
@Getter
@Accessors(chain = true, fluent = true)
public class ButtonImage {
    protected final JsonObject object = new JsonObject();

    protected Type type;
    protected String data;

    public ButtonImage(Type type, String data) {
        this.type(type);
        this.data(data);
    }

    public ButtonImage type(Type type) {
        this.type = type;
        this.object.addProperty("type", type.name().toLowerCase());
        return this;
    }

    public ButtonImage data(String path) {
        this.data = path;
        this.object.addProperty("data", path);
        return this;
    }

    /**
     * Enum representing the type of image: PATH (resource pack) or URL (internet).
     */
    public enum Type {
        /** Image located inside a resource pack. */
        PATH,
        /** Image accessed via the internet. */
        URL;

        /**
         * Creates a ButtonImage of this type with the given path.
         * @param path The image path or URL
         * @return A new ButtonImage
         */
        public ButtonImage of(String path) {
            return new ButtonImage(this, path);
        }
    }

    /**
     * Serializes the ButtonImage to JSON.
     * @return The image as a JsonObject
     */
    public JsonObject toJson() {
        return this.object;
    }
}
