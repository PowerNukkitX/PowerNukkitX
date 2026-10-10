package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructureRandomSpreadPlacement;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;

import java.util.List;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

/**
 * Produces Buried Treasure structure AABB volumes.
 *
 * @author Curse
 */
public class BuriedTreasurePopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_buried_treasure";

    public static final StructurePlacement PLACEMENT = new StructureRandomSpreadPlacement(
            StructurePlacement.PlacementSettings.builder()
                    .salt(16842397L)
                    .minDistance(2)
                    .maxDistance(4)
                    .isBiomeValid(biome -> biome == BiomeID.BEACH
                            || biome == BiomeID.COLD_BEACH
                            || biome == BiomeID.STONE_BEACH
                            || biome == BiomeID.MUSHROOM_ISLAND_SHORE)
                    .build(),
            StructureRandomSpreadPlacement.SpreadType.TRIANGULAR
    );

    @Override
    public void apply(ChunkGenerateContext context) {
        if (!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int biome = chunk.getBiomeId(8, SEA_LEVEL, 8);
        if (!PLACEMENT.canGenerate(chunk.getLevel().getSeed(), random, chunkX, chunkZ, biome)) return;

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        StructureAabbVolumes.addDynamic(
                chunk,
                "minecraft:buried_treasure",
                List.of(new BoundingBox(originX + 3, 0, originZ + 3, originX + 13, 255, originZ + 13))
        );
    }

    @Override
    public String name() {
        return NAME;
    }
}
