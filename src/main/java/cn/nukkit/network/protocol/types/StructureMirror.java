package cn.nukkit.network.protocol.types;

public enum StructureMirror {
    NONE,
    X,
    Z,
    XZ;

    private static final StructureMirror[] VALUES = StructureMirror.values();

    public static StructureMirror from(int id) {
        return VALUES[id];
    }
}
