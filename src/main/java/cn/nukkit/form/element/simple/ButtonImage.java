package cn.nukkit.form.element.simple;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The image of a {@link ElementButton}
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
     * There are two types of images:
     * - PATH (image located inside a resource pack)
     * - URL (image accessed via the internet)
     */
    public enum Type {
        PATH,
        URL;

        public ButtonImage of(String path) {
            return new ButtonImage(this, path);
        }
    }

    public JsonObject toJson() {
        return this.object;
    }
}
