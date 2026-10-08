package org.powernukkitx.level.village;

public enum PoiType {
    ACQUIRABLE_JOB_SITE(2),
    HOME(0),
    MEETING(1);

    private static final int STORAGE_TYPE_COUNT = 3;
    private final int storageId;

    PoiType(int storageId) {
        this.storageId = storageId;
    }

    /**
     * Returns the persisted village POI type ID.
     */
    public int storageId() {
        return storageId;
    }

    /**
     * Returns the village POI type for a persisted ID.
     */
    public static PoiType fromStorageId(int storageId) {
        return switch (storageId) {
            case 0 -> HOME;
            case 1 -> MEETING;
            case 2 -> ACQUIRABLE_JOB_SITE;
            default -> throw new IllegalArgumentException("Unknown village POI type: " + storageId);
        };
    }

    /**
     * Returns the number of persisted village POI types.
     */
    public static int storageTypeCount() {
        return STORAGE_TYPE_COUNT;
    }
}
