package cn.nukkit.form.element.simple;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true, fluent = true)
public class ButtonImage {
    protected final JsonObject object = new JsonObject();

    protected Type type;
    protected String path;

    public ButtonImage(Type type, String path) {
        this.type(type);
        this.path(path);
    }

    public ButtonImage type(Type type) {
        this.type = type;
        this.object.addProperty("type", type.name().toLowerCase());
        return this;
    }

    public ButtonImage path(String path) {
        this.path = path;
        this.object.addProperty("data", path);
        return this;
    }

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
