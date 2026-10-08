package org.powernukkitx.level.structure.spawn;

/**
 * Tri-state spawn-condition override.
 *
 * @author Curse
 */
public enum SpawnOverrideState {
    UNSET(0),
    NO(1),
    YES(2);

    private final int id;

    SpawnOverrideState(int id) {
        this.id = id;
    }

    /**
     * Returns the native override-state ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Resolves a native override-state ID.
     */
    public static SpawnOverrideState fromId(int id) {
        return switch (id) {
            case 0 -> UNSET;
            case 1 -> NO;
            case 2 -> YES;
            default -> throw new IllegalArgumentException("Unknown spawn override state: " + id);
        };
    }
}
