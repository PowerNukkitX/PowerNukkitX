package cn.nukkit.network.protocol.types.attribute.data;

public interface AttributeData {
    Type getType();

    enum Type {
        BOOL,
        FLOAT,
        COLOR;

        private static final Type[] VALUES = values();

        public static Type from(int ordinal) {
            if (ordinal >= 0 && ordinal < VALUES.length) {
                return VALUES[ordinal];
            }
            throw new UnsupportedOperationException("Detected unknown AttributeData.Type ID: " + ordinal);
        }
    }
}
