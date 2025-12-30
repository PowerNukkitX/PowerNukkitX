package cn.nukkit.form.element.simple;

import com.google.gson.JsonObject;

/**
 * Interface for simple form elements (e.g., buttons, dividers, headers, labels).
 * Provides serialization and update utilities.
 */
public interface ElementSimple {
    /**
     * An empty array of ElementSimple, for convenience.
     */
    ElementSimple[] EMPTY_LIST = new ElementSimple[0];

    /**
     * Serializes the element to JSON.
     *
     * @return The element as a JsonObject
     */
    JsonObject toJson();

    /**
     * Updates this element with another element's properties.
     *
     * @param element The element to update from
     * @return The updated element
     */
    ElementSimple updateWith(ElementSimple element);
}
