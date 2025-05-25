package cn.nukkit.form.element.custom;

import com.google.gson.JsonObject;

public interface ElementCustom {
    JsonObject toJson();

    /**
     * @return Whether the element can be responded to
     */
    default boolean hasResponse() {
        return true;
    }
}
