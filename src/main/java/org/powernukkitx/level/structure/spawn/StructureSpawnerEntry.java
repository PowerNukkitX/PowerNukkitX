package org.powernukkitx.level.structure.spawn;

import com.google.common.base.Preconditions;

/**
 * Weighted entry in a structure-specific SpawnerData list.
 *
 * @author Curse
 */
public record StructureSpawnerEntry(StructureSpawnerData data, int weight) {
    public StructureSpawnerEntry {
        Preconditions.checkNotNull(data, "data");
    }

    /**
     * Creates an entry with the vanilla structure-list weight of one.
     */
    public static StructureSpawnerEntry of(StructureSpawnerData data) {
        return new StructureSpawnerEntry(data, 1);
    }
}
