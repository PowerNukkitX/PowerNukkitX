package cn.nukkit.form.element;

import com.google.gson.JsonObject;

/**
 * The element base class, featuring serializable utilities
 */
public abstract class Element {
    /**
     * The object used to add an element parameters
     * Since a form might be updated by the code, we do not recreate the object everytime.
     */
    protected JsonObject object = new JsonObject();

    /**
     * @return Serialized element for the Minecraft client
     */
    public abstract JsonObject toJson();
}
