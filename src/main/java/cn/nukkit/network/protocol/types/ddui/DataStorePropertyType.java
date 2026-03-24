package cn.nukkit.network.protocol.types.ddui;

public enum DataStorePropertyType {

    BOOLEAN(1),
    INT64(2),
    STRING(4),
    OBJECT(6);

    private static final DataStorePropertyType[] VALUES = values();

    private final int id;

    DataStorePropertyType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DataStorePropertyType from(int id) {
        for (DataStorePropertyType value : VALUES) {
            if (value.id == id) {
                return value;
            }
        }
        throw new UnsupportedOperationException("Received unknown DataStorePropertyType ID: " + id);
    }
}
