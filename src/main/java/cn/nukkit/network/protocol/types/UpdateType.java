package cn.nukkit.network.protocol.types;

public enum UpdateType {

    CLEAR,
    REMOVE,
    SET_INT,
    SET_FLOAT;

    public static final UpdateType[] VALUES = values();
}
