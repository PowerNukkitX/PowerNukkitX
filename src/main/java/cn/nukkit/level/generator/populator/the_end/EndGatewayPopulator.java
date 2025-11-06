package cn.nukkit.level.generator.populator.the_end;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.noise.d.NoiseGeneratorSimplexD;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectEndGateway;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;

import static cn.nukkit.level.generator.stages.end.TheEndTerrainStage.getIslandHeight;

public class EndGatewayPopulator extends Populator {

    public static final String NAME = "the_end_gateway";

    protected final NukkitRandom random = new NukkitRandom();
    protected NoiseGeneratorSimplexD islandNoise;
    protected final static ObjectEndGateway objectEndGateway = new ObjectEndGateway();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        if ((long) chunkX * (long) chunkX + (long) chunkZ * (long) chunkZ <= 4096L) {
            return;
        }

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if(islandNoise == null) islandNoise = new NoiseGeneratorSimplexD(new NukkitRandom(level.getSeed()));

        if (getIslandHeight(chunkX, chunkZ, 1, 1, islandNoise) > 40f) {
            if (random.nextBoundedInt(700) == 0) {
                int x = (chunkX << 4) + random.nextBoundedInt(16);
                int z = (chunkZ << 4) + random.nextBoundedInt(16);
                int y = level.getHeightMap(x, z) + random.nextBoundedInt(7) + 3;

                if (y > 1 && y < 254) {
                    BlockManager object = new BlockManager(level);
                    objectEndGateway.generate(object, random, new Vector3(x, y, z));
                    queueObject(chunk, object);
                }
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
