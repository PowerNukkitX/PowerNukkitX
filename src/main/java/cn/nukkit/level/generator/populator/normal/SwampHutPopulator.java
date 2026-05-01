package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.ObjectSwampHut;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.placement.StructurePlacement;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class SwampHutPopulator extends Populator {

    public static final String NAME = "normal_swamp_hut";

    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(14357620L)
            .minDistance(8)
            .maxDistance(32)
            .isBiomeValid(biome -> Registries.BIOME.get(biome).getTags().contains(BiomeTags.SWAMP))
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
