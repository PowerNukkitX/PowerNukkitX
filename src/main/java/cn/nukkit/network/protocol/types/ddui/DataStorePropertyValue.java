package cn.nukkit.network.protocol.types.ddui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
public class DataStorePropertyValue {

    Type type;
    Object value;

    @Getter
    @RequiredArgsConstructor
    public enum Type {

        NONE(0),
        BOOL(1),
        INT64(2),
        STRING(4),
        TYPE(6);

        private static final Type[] VALUES = values();

        private final int id;

        public static Type from(int id) {
            for (Type value : VALUES) {
                if (value.getId() == id) {
                    return value;
                }
            }
            throw new UnsupportedOperationException("Detected unknown DataStorePropertyValue.Type ID: " + id);
        }
    }


    public static DataStorePropertyValue ofBoolean(boolean value) {
        return new DataStorePropertyValue(Type.BOOL, value);
    }

    public static DataStorePropertyValue ofLong(long value) {
        return new DataStorePropertyValue(Type.INT64, value);
    }

    public static DataStorePropertyValue ofString(String value) {
        return new DataStorePropertyValue(Type.STRING, value);
    }

    public static DataStorePropertyValue ofObject(Map<String, DataStorePropertyValue> children) {
        return new DataStorePropertyValue(Type.TYPE, children);
    }
}