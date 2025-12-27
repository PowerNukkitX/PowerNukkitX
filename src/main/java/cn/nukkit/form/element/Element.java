package cn.nukkit.form.element;

import com.google.gson.JsonObject;

/**
 * Abstract base class for form elements.
 * Provides serialization utilities for Minecraft client forms.
 */
public abstract class Element {
    /**
     * The object used to add element parameters for serialization.
     * This object is reused to avoid unnecessary allocations.
     */
    protected JsonObject object = new JsonObject();

    /**
     * Serializes the element for the Minecraft client.
     * @return The element as a JsonObject
     */
    public abstract JsonObject toJson();
}
