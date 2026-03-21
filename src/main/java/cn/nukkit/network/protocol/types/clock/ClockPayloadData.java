package cn.nukkit.network.protocol.types.clock;

public interface ClockPayloadData {
    Type getType();

    enum Type {
        SYNC_STATE_DATA,
        INITIALIZE_REGISTRY_DATA,
        ADD_TIME_MARKER_DATA,
        REMOVE_TIME_MARKER_DATA;

        private static final Type[] VALUES = values();

        public static Type from(int ordinal) {
            if (ordinal >= 0 && ordinal < VALUES.length) {
                return VALUES[ordinal];
            }
            throw new UnsupportedOperationException("Detected unknown ClockPayloadData.Type ID: " + ordinal);
        }
    }
}
