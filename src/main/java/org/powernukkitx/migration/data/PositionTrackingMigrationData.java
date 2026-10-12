package org.powernukkitx.migration.data;

import java.util.List;

/**
 * Carries normalized position-tracking data through versioned migration steps. Source entries retain the original handle range while
 * entries represent records ready for the server-global BDS-compatible storage.
 *
 * @author Curse
 */
public record PositionTrackingMigrationData(
        Source source,
        List<Entry> entries,
        int sourceLastId,
        int targetLastId,
        int reservedOffset,
        List<CanonicalEntry> canonicalEntries,
        int handleOffset,
        int migratedLastId
) {
    /**
     * Identifies the storage representation being migrated.
     *
     * @author Curse
     */
    public enum Source {
        LEGACY_PNX,
        BDS,
        CANONICAL
    }

    /**
     * Represents one normalized source position-tracking entry.
     *
     * @author Curse
     */
    public record Entry(int handle, int dimensionId, String levelName, double x, double y, double z, byte status) {
    }

    /**
     * Represents one position-tracking entry.
     *
     * @author Curse
     */
    public record CanonicalEntry(int handle, int dimensionId, String levelName, int x, int y, int z, byte status) {
    }
}
