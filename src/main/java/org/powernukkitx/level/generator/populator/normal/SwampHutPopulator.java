package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.ObjectSwampHut;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class SwampHutPopulator extends Populator {

    public static final String NAME = "normal_swamp_hut";

    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(14357620L)
            .minDistance(8)
            .maxDistance(32)
            .isBiomeValid(biome -> Registries.BIOME.getTags(biome).contains(BiomeTags.SWAMP))
            .build());

    protected static final ObjectSwampHut SWAMP_HUT = new ObjectSwampHut();

    @Override
    public void apply(ChunkGenerateContext context) {
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
        int y = level.getHeightMap(x, z);
        BlockManager manager = new BlockManager(level);
        SWAMP_HUT.generate(manager, null, new Vector3(x, y, z));
        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }

}
