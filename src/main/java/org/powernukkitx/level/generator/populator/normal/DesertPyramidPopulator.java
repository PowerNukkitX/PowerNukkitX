package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.ObjectDesertPyramid;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.Vector3;

public class DesertPyramidPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_desert_pyramid";

    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(14357617L)
            .minDistance(8)
            .maxDistance(32)
            .isBiomeValid(biome -> biome == BiomeID.DESERT || biome == BiomeID.DESERT_HILLS || biome == BiomeID.DESERT_MUTATED)
            .build());

    protected static final ObjectDesertPyramid PYRAMID = new ObjectDesertPyramid();

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7), 7);
        if (!PLACEMENT.canGenerate(level.getSeed(), random, chunkX, chunkZ, biome)) {
            return;
        }

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int x = (chunkX << 4) + random.nextBoundedInt(15);
        int z = (chunkZ << 4) + random.nextBoundedInt(15);
        int y = level.getHeightMap(x, z);

        BlockManager manager = new BlockManager(level);
        PYRAMID.generate(manager, null, new Vector3(x, y, z));
        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }

}
