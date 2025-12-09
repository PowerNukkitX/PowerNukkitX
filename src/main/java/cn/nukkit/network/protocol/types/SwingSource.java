package cn.nukkit.network.protocol.types;

import lombok.Getter;

import java.util.HashMap;

public enum SwingSource {

    NONE("none"),
    BUILD("build"),
    MINE("mine"),
    INTERACT("interact"),
    ATTACK("attack"),
    USE_ITEM("useitem"),
    THROW_ITEM("throwitem"),
    DROP_ITEM("dropitem"),
    EVENT("event");

    private static final HashMap<String, SwingSource> BY_NAME = new HashMap<>();

    static {
        for (SwingSource value : values()) {
            BY_NAME.put(value.name, value);
        }
    }

    @Getter
    private final String name;

    SwingSource(String name) {
        this.name = name;
    }

    public static SwingSource from(String name) {
        return BY_NAME.get(name);
    }
}
