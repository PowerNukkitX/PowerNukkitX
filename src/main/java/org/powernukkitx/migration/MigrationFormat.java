package org.powernukkitx.migration;

import java.util.List;

/**
 * Identifies each independently versioned storage format handled by the migration framework.
 *
 * @author Curse
 */
public enum MigrationFormat {
    WORLD_STORAGE("world_storage"),
    CHUNK("chunk"),
    BLOCK_ENTITY("block_entity"),
    ACTOR("actor"),
    PLAYER("player"),
    POSITION_TRACKING("position_tracking"),
    SCOREBOARD("scoreboard"),
    STRUCTURE("structure");

    private static final List<MigrationFormat> EXECUTION_ORDER = List.of(values());
    private final String serializedName;

    MigrationFormat(String serializedName) {
        this.serializedName = serializedName;
    }

    /**
     * Returns the stable serialized name of this migration format.
     */
    public String serializedName() {
        return this.serializedName;
    }

    /**
     * Returns migration formats in their defined execution order.
     */
    public static List<MigrationFormat> executionOrder() {
        return EXECUTION_ORDER;
    }
}
