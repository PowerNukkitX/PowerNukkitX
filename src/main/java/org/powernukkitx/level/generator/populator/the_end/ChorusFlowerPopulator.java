package org.powernukkitx.level.generator.populator.the_end;

import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockEndStone;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.noise.d.NoiseGeneratorSimplexD;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.legacytree.LegacyChorusTree;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.NukkitRandom;

import static org.powernukkitx.level.generator.stages.end.TheEndTerrainStage.getIslandHeight;

public class ChorusFlowerPopulator extends Populator {

    protected final static BlockState END_STONE = BlockEndStone.PROPERTIES.getDefaultState();

    public static final String NAME = "the_end_chorus_flower";

    protected NoiseGeneratorSimplexD islandNoise;
    protected final static LegacyChorusTree objectChorusTree = new LegacyChorusTree();

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
            for (int i = 0; i < random.nextBoundedInt(5); i++) {
                int x = (chunkX << 4) + random.nextBoundedInt(16);
                int z = (chunkZ << 4) + random.nextBoundedInt(16);
                int y = level.getHeightMap(x, z);
                if (y > 0) {
                    if (level.getBlockStateAt(x, y + 1, z).equals(BlockAir.STATE) && level.getBlockStateAt(x, y, z).equals(END_STONE)) {
                        BlockManager object = new BlockManager(level);
                        objectChorusTree.generate(object, random, new Vector3(x, y + 1, z), 8);
                        queueObject(chunk, object);
                    }
                }
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
