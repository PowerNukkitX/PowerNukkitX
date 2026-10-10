package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.ObjectSwampHut;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructureRandomSpreadPlacement;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;

import java.util.List;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class SwampHutPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_swamp_hut";

    public static final StructurePlacement PLACEMENT = new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
            .salt(14357617L)
            .minDistance(8)
            .maxDistance(32)
            .isBiomeValid(biome -> Registries.BIOME.getTags(biome).contains(BiomeTags.SWAMP))
            .build(), StructureRandomSpreadPlacement.SpreadType.LINEAR);

    protected static final ObjectSwampHut SWAMP_HUT = new ObjectSwampHut();

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        int biome = chunk.getBiomeId(7, SEA_LEVEL, 7);
        if (!PLACEMENT.canGenerate(level.getSeed(), random, chunkX, chunkZ, biome)) {
            return;
        }

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int x = (chunkX << 4) + random.nextBoundedInt(15);
        int z = (chunkZ << 4) + random.nextBoundedInt(15);
        int y = level.getHeightMap(x, z) - 1;
        BlockManager manager = new BlockManager(level);
        SWAMP_HUT.generate(manager, null, new Vector3(x, y, z));
        StructureAabbVolumes.addDynamic(
                level,
                "minecraft:swamp_hut",
                List.of(new BoundingBox(x, y + 1, z, x + 6, y + 7, z + 8))
        );
        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }

}
