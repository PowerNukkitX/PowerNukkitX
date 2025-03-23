package cn.nukkit.form.element.simple;

import com.google.gson.JsonObject;

public interface ElementSimple {
    ElementSimple[] EMPTY_LIST = new ElementSimple[0];

    JsonObject toJson();

    ElementSimple updateWith(ElementSimple element);
}
