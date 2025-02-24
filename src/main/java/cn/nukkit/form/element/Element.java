package cn.nukkit.form.element;

import com.google.gson.JsonObject;

/**
 * The Element base class.
 */
public abstract class Element {
    protected JsonObject object = new JsonObject();

    /**
     * @return Serialized element for the Minecraft client
     */
    public abstract JsonObject toJson();
}
