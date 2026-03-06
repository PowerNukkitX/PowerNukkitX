package cn.nukkit.network.protocol.types.ddui;

public enum DataStorePropertyType {

    BOOLEAN,
    INT64,
    STRING,
    OBJECT;

    private static final DataStorePropertyType[] VALUES = values();

    public static DataStorePropertyType from(int ordinal) {
        if (ordinal < 0 || ordinal >= VALUES.length) {
            throw new UnsupportedOperationException("Received unknown DataStorePropertyType ID: " + ordinal);
        }
        return VALUES[ordinal];
    }
}