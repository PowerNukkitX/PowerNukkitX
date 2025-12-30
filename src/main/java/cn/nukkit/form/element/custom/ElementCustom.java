package cn.nukkit.form.element.custom;

import com.google.gson.JsonObject;

/**
 * Interface for custom form elements (e.g., input, dropdown, slider, etc.).
 * Provides serialization and response utilities.
 */
public interface ElementCustom {
    /**
     * Serializes the element to JSON.
     * @return The element as a JsonObject
     */
    JsonObject toJson();
    /**
     * Indicates whether the element can be responded to by the user.
     * @return true if the element can be responded to, false otherwise
     */
    default boolean hasResponse() {
        return true;
    }
}
