package org.powernukkitx.level.structure.spawn;

/**
 * Natural-spawn categories used by structure spawn overrides.
 *
 * @author Curse
 */
public enum SpawnCategory {
    MONSTER(0),
    CREATURE(1),
    AMBIENT(2),
    AXOLOTLS(3),
    UNDERGROUND_WATER_CREATURE(4),
    WATER_CREATURE(5),
    WATER_AMBIENT(6),
    MISC(7);

    private final int id;

    SpawnCategory(int id) {
        this.id = id;
    }

    /**
     * Returns the native category ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Resolves a native category ID.
     */
    public static SpawnCategory fromId(int id) {
        return switch (id) {
            case 0 -> MONSTER;
            case 1 -> CREATURE;
            case 2 -> AMBIENT;
            case 3 -> AXOLOTLS;
            case 4 -> UNDERGROUND_WATER_CREATURE;
            case 5 -> WATER_CREATURE;
            case 6 -> WATER_AMBIENT;
            case 7 -> MISC;
            default -> throw new IllegalArgumentException("Unknown spawn category: " + id);
        };
    }
}
