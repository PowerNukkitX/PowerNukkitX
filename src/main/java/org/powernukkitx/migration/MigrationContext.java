package org.powernukkitx.migration;

import com.google.common.base.Preconditions;
import org.powernukkitx.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Provides shared server state and cross-record data to versioned migration steps.
 *
 * @author Curse
 */
public final class MigrationContext {
    private final Server server;
    private final Map<UUID, Long> migratedActorUniqueIdsByUuid = new HashMap<>();
    private final Map<Long, Long> migratedLegacyPlayerUniqueIds = new HashMap<>();
    private int legacyPositionTrackingSourceLastId = -1;
    private int legacyPositionTrackingOffset;
    private Set<Integer> legacyPositionTrackingSourceHandles = Set.of();

    /**
     * Creates a migration context for the supplied server.
     */
    public MigrationContext(Server server) {
        this.server = server;
    }

    /**
     * Returns the server being migrated.
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * Returns shared actor UUID to UniqueID mappings collected during migration.
     */
    public Map<UUID, Long> getMigratedActorUniqueIdsByUuid() {
        return this.migratedActorUniqueIdsByUuid;
    }

    /**
     * Returns legacy PNX player ActorUniqueID remaps collected during player migration.
     */
    public Map<Long, Long> getMigratedLegacyPlayerUniqueIds() {
        return this.migratedLegacyPlayerUniqueIds;
    }

    /**
     * Stores the active legacy PNX position-tracking remap and existing source handles.
     */
    public void setLegacyPositionTrackingRemap(int sourceLastId, int offset, Set<Integer> sourceHandles) {
        Preconditions.checkArgument(sourceLastId >= 0, "sourceLastId must not be negative");
        Preconditions.checkArgument(offset >= 0, "offset must not be negative");
        Preconditions.checkNotNull(sourceHandles, "sourceHandles must not be null");
        Preconditions.checkArgument(sourceHandles.stream().allMatch(handle -> handle > 0 && handle <= sourceLastId),
                "sourceHandles must be inside the source allocator range");
        this.legacyPositionTrackingSourceLastId = sourceLastId;
        this.legacyPositionTrackingOffset = offset;
        this.legacyPositionTrackingSourceHandles = Set.copyOf(sourceHandles);
    }

    /**
     * Returns whether a legacy PNX position-tracking remap is active.
     */
    public boolean hasLegacyPositionTrackingRemap() {
        return this.legacyPositionTrackingSourceLastId >= 0;
    }

    /**
     * Returns the legacy PNX source position-tracking allocator.
     */
    public int getLegacyPositionTrackingSourceLastId() {
        return this.legacyPositionTrackingSourceLastId;
    }

    /**
     * Returns the reserved global position-tracking handle offset.
     */
    public int getLegacyPositionTrackingOffset() {
        return this.legacyPositionTrackingOffset;
    }

    /**
     * Returns handles backed by valid legacy PNX position-tracking records.
     */
    public Set<Integer> getLegacyPositionTrackingSourceHandles() {
        return this.legacyPositionTrackingSourceHandles;
    }

    /**
     * Clears the transient legacy PNX position-tracking remap.
     */
    public void clearLegacyPositionTrackingRemap() {
        this.legacyPositionTrackingSourceLastId = -1;
        this.legacyPositionTrackingOffset = 0;
        this.legacyPositionTrackingSourceHandles = Set.of();
    }
}
