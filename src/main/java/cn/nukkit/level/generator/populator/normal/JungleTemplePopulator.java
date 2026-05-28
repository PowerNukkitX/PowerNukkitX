package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.ObjectJungleTemple;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.placement.StructurePlacement;
import cn.nukkit.math.Vector3;

public class JungleTemplePopulator extends Populator {

    public static final String NAME = "normal_jungle_temple";

    protected static final ObjectJungleTemple JUNGLE_TEMPLE = new ObjectJungleTemple();
    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(14357619L)
            .minDistance(8)
            .maxDistance(32)
            .isBiomeValid(biome -> biome == BiomeID.JUNGLE)
            .build());

    @Override
    public void apply(ChunkGenerateContext context) {
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
        JUNGLE_TEMPLE.generate(manager, random, new Vector3(x, y, z));
        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }

}
