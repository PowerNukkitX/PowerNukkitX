package cn.nukkit.level.generator.populator.nether.basalt_delta;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.NukkitMath;

import java.util.ArrayList;

public class BasaltDeltaMagmaPopulator extends Populator {

    public static final String NAME = "nether_basalt_delta_magma";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        BlockManager object = new BlockManager(level);
        int amount = random.nextBoundedInt(4) + 20;
        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            if(level.getBiomeId(x, 0, z) != BiomeID.BASALT_DELTAS) continue;
            ArrayList<Integer> ys = this.getHighestWorkableBlocks(object, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                object.setBlockStateAt(x, y, z, BlockID.MAGMA);
            }
        }
        queueObject(chunk, object);
    }

    private ArrayList<Integer> getHighestWorkableBlocks(BlockManager level, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            String b = level.getBlockIdIfCachedOrLoaded(x, y, z);
            String b1 = level.getBlockIdIfCachedOrLoaded(x + 1, y, z);
            String b2 = level.getBlockIdIfCachedOrLoaded(x - 1, y, z);
            String b3 = level.getBlockIdIfCachedOrLoaded(x, y, z + 1);
            String b4 = level.getBlockIdIfCachedOrLoaded(x, y, z - 1);
            if ((b == Block.BASALT || b == Block.BLACKSTONE) &&
                    level.getBlockIfCachedOrLoaded(x, y + 1, z).isAir() && (
                    b1 == BlockID.LAVA ||
                            b2 == BlockID.LAVA ||
                            b3 == BlockID.LAVA ||
                            b4 == BlockID.LAVA ||
                            b1 == BlockID.FLOWING_LAVA ||
                            b2 == BlockID.FLOWING_LAVA ||
                            b3 == BlockID.FLOWING_LAVA ||
                            b4 == BlockID.FLOWING_LAVA
            )
            ) {
                blockYs.add(y);
            }
        }
        return blockYs;
    }

    @Override
    public String name() {
        return NAME;
    }
}
