package cn.nukkit.network.protocol.types;

public enum StructureRedstoneSaveMode {
    SAVES_TO_MEMORY,
    SAVES_TO_DISK;

    private static final StructureRedstoneSaveMode[] VALUES = StructureRedstoneSaveMode.values();

    public static StructureRedstoneSaveMode from(int id) {
        return VALUES[id];
    }
}
