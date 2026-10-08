package org.powernukkitx.level.structure.spawn;

import com.google.common.base.Preconditions;
import org.powernukkitx.level.structure.StructureBoundingBoxType;

import java.util.List;

/**
 * Spawn override for one structure spawn category.
 *
 * @author Curse
 */
public record StructureSpawnOverride(StructureBoundingBoxType boundingBoxType, List<StructureSpawnerEntry> spawns) {
    public StructureSpawnOverride {
        Preconditions.checkNotNull(boundingBoxType, "boundingBoxType");
        spawns = List.copyOf(Preconditions.checkNotNull(spawns, "spawns"));
    }
}
