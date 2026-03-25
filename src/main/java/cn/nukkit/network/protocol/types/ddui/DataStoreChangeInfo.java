package cn.nukkit.network.protocol.types.ddui;

public abstract class DataStoreChangeInfo {

    public abstract Type getChangeType();

    public enum Type {
        UPDATE,
        CHANGE,
        REMOVAL;

        private static final Type[] VALUES = values();

        public static Type from(int ordinal) {
            if (ordinal < 0 || ordinal >= VALUES.length) {
                throw new UnsupportedOperationException("Received invalid change type ID for DataStoreChangeInfo: " + ordinal);
            }
            return VALUES[ordinal];
        }
    }
}