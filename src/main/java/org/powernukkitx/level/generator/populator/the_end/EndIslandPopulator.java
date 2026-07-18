package org.powernukkitx.level.generator.populator.the_end;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.noise.d.NoiseGeneratorSimplexD;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectEndIsland;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.NukkitRandom;

import static org.powernukkitx.level.generator.stages.end.TheEndTerrainStage.getIslandHeight;

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
        if ((long) chunkX * (long) chunkX + (long) chunkZ * (long) chunkZ <= 4096L) {
            return;
        }

        if(islandNoise == null) islandNoise = new NoiseGeneratorSimplexD(new NukkitRandom(level.getSeed()));
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));

        if (random.nextBoundedInt(14) == 0) {
            float height = getIslandHeight(chunkX, chunkZ, 1, 1, islandNoise);

            if (height < -20f) {
                Vector3 position = new Vector3(chunkX << 4, 0, chunkZ << 4);
                BlockManager object = new BlockManager(level);
                objectEndIsland.generate(object, random, position.add(8 + random.nextBoundedInt(16), 55 + random.nextBoundedInt(16), 8 + random.nextBoundedInt(16)));
                if (random.nextBoundedInt(4) == 0) {
                    objectEndIsland.generate(object, random, position.add(8 + random.nextBoundedInt(16), 55 + random.nextBoundedInt(16), 8 + random.nextBoundedInt(16)));
                }
                queueObject(chunk, object);
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
