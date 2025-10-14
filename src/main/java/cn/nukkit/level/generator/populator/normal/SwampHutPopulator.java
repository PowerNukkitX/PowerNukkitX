package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.ObjectSwampHut;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class SwampHutPopulator extends Populator {

    public static final String NAME = "normal_swamp_hut";
    protected static final int MIN_DISTANCE = 8;
    protected static final int MAX_DISTANCE = 32;

    protected static final ObjectSwampHut SWAMP_HUT = new ObjectSwampHut();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int x = (chunkX << 4) + random.nextBoundedInt(15);
        int z = (chunkZ << 4) + random.nextBoundedInt(15);
        int y = level.getHeightMap(x, z);
        if(canGenerate(random, chunk)) {
            BlockManager manager = new BlockManager(level);
            SWAMP_HUT.generate(manager, null, new Vector3(x, y, z));
            queueObject(chunk, manager);
        }
    }

    public boolean canGenerate(RandomSourceProvider random, IChunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        return ((chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ) && isBiomeValid(chunk);
    }

    protected boolean isBiomeValid(IChunk chunk) {
        return Registries.BIOME.get(chunk.getBiomeId(7, SEA_LEVEL, 7)).getTags().contains(BiomeTags.SWAMP);
    }

    @Override
    public String name() {
        return NAME;
    }

}
