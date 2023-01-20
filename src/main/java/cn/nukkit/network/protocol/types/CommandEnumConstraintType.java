package cn.nukkit.network.protocol.types;

public enum CommandEnumConstraintType {
    CHEATS_ENABLED,
    OPERATOR_PERMISSIONS,
    HOST_PERMISSIONS,
    UNKNOWN_3;

    private static final CommandEnumConstraintType[] VALUES = values();

    public static CommandEnumConstraintType byId(int id) {
        if (id >= 0 && id < VALUES.length) {
            return VALUES[id];
        }
        throw new UnsupportedOperationException("Unknown CommandEnumConstraintType ID: " + id);
    }
}
