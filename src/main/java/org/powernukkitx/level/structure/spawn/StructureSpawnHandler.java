package org.powernukkitx.level.structure.spawn;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.level.structure.AabbVolumes;
import org.powernukkitx.level.structure.StructureBoundingBoxType;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.registry.StructureSpawnOverrideRegistry;

import java.util.List;

/**
 * Resolves structure spawn state from geometry-only AABB volume matches.
 *
 * @author Curse
 */
public final class StructureSpawnHandler {
    private final StructureSpawnOverrideRegistry overrideRegistry;

    public StructureSpawnHandler(StructureSpawnOverrideRegistry overrideRegistry) {
        this.overrideRegistry = Preconditions.checkNotNull(overrideRegistry, "overrideRegistry");
    }

    /**
     * Returns the spawn list for a matched structure area, or null when no override applies.
     */
    @Nullable
    public List<StructureSpawnerEntry> findSpawnerEntries(AabbVolumes volumes, BlockVector3 position, SpawnCategory category) {
        Preconditions.checkNotNull(volumes, "volumes");
        Preconditions.checkNotNull(position, "position");
        Preconditions.checkNotNull(category, "category");

        List<AabbVolumes.DynamicSpawnAreaMatch> dynamicMatches = volumes.findDynamicSpawnAreas(position);
        AabbVolumes.DynamicSpawnAreaMatch dynamicPiece = findDynamicSpawnArea(dynamicMatches, StructureBoundingBoxType.PIECE);

        if (dynamicPiece != null) {
            List<StructureSpawnerEntry> spawns = getSpawnerEntries(dynamicPiece.structureType().type(), category, StructureBoundingBoxType.PIECE);
            if (spawns != null) return spawns;
        } else {
            AabbVolumes.DynamicSpawnAreaMatch dynamicStructure =
                    findDynamicSpawnArea(dynamicMatches, StructureBoundingBoxType.STRUCTURE);

            if (dynamicStructure != null) {
                List<StructureSpawnerEntry> spawns =
                        getSpawnerEntries(dynamicStructure.structureType().type(), category, StructureBoundingBoxType.STRUCTURE);
                if (spawns != null) return spawns;
            }
        }

        AabbVolumes.StaticSpawnAreaMatch staticPiece =
                findStaticSpawnArea(volumes.findStaticSpawnAreas(position), StructureBoundingBoxType.PIECE);

        return staticPiece == null
                ? null
                : getSpawnerEntries(staticPiece.structureType().type(), category, StructureBoundingBoxType.PIECE);
    }

    @Nullable
    private AabbVolumes.DynamicSpawnAreaMatch findDynamicSpawnArea(List<AabbVolumes.DynamicSpawnAreaMatch> matches, StructureBoundingBoxType boundingBoxType) {
        for (int i = matches.size() - 1; i >= 0; i--) {
            AabbVolumes.DynamicSpawnAreaMatch match = matches.get(i);
            if (match.boundingBoxType() == boundingBoxType) return match;
        }

        return null;
    }

    @Nullable
    private AabbVolumes.StaticSpawnAreaMatch findStaticSpawnArea(List<AabbVolumes.StaticSpawnAreaMatch> matches, StructureBoundingBoxType boundingBoxType) {
        for (int i = matches.size() - 1; i >= 0; i--) {
            AabbVolumes.StaticSpawnAreaMatch match = matches.get(i);
            if (match.boundingBoxType() == boundingBoxType) return match;
        }

        return null;
    }

    @Nullable
    private List<StructureSpawnerEntry> getSpawnerEntries(String structureType, SpawnCategory category, StructureBoundingBoxType boundingBoxType) {
        StructureSpawnOverride override = overrideRegistry.get(structureType, category);
        return override == null || override.boundingBoxType() != boundingBoxType ? null : override.spawns();
    }
}
