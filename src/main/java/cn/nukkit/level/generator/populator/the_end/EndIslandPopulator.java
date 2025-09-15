package cn.nukkit.level.generator.populator.the_end;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.noise.d.vanilla.NoiseGeneratorSimplexD;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectEndGateway;
import cn.nukkit.level.generator.object.ObjectEndIsland;
import cn.nukkit.level.generator.object.ObjectExitPortal;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;

import static cn.nukkit.level.generator.stages.end.TheEndTerrainStage.getIslandHeight;

public class EndIslandPopulator extends Populator {

    public static final String NAME = "the_end_island";

    protected final NukkitRandom random = new NukkitRandom();
    protected NoiseGeneratorSimplexD islandNoise;
    protected final static ObjectEndIsland objectEndIsland = new ObjectEndIsland();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();

        if(islandNoise == null) islandNoise = new NoiseGeneratorSimplexD(new NukkitRandom(level.getSeed()));

        if (random.nextBoundedInt(14) == 0) {
            float height = getIslandHeight(chunkX, chunkZ, 1, 1, islandNoise);

            if (height < -20f) {
                Vector3 position = new Vector3(chunkX << 4, 0, chunkZ << 4);
                BlockManager manager = new BlockManager(level);
                BlockManager object = new BlockManager(level);
                objectEndIsland.generate(object, random, position.add(8 + random.nextBoundedInt(16), 55 + random.nextBoundedInt(16), 8 + random.nextBoundedInt(16)));
                if (random.nextBoundedInt(4) == 0) {
                    objectEndIsland.generate(object, random, position.add(8 + random.nextBoundedInt(16), 55 + random.nextBoundedInt(16), 8 + random.nextBoundedInt(16)));
                }
                for(Block block : object.getBlocks()) {
                    if(block.getChunk() != chunk) {
                        IChunk nextChunk = block.getChunk();
                        long chunkHash = Level.chunkHash(nextChunk.getX(), nextChunk.getZ());
                        getChunkPlacementQueue(chunkHash, level).setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    }
                    if(block.getChunk().isGenerated()) {
                        manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    }
                }
                writeOutsideChunkStructureData(chunk);
                manager.applySubChunkUpdate(manager.getBlocks());
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
