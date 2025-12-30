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

public class BasaltDeltaPillarPopulator extends Populator {

    public static final String NAME = "nether_basalt_delta_pillar";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));

        BlockManager object = new BlockManager(level);
        int amount = random.nextBoundedInt(128) + 128;

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            if(level.getBiomeId(x, 0, z) != BiomeID.BASALT_DELTAS) continue;
            ArrayList<Integer> ys = this.getHighestWorkableBlocks(object, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                if (random.nextBoundedInt(5) == 0) continue;
                for (int randomHeight = 0; randomHeight < random.nextBoundedInt(5) + 1; randomHeight++) {
                    int placeLocation = y + randomHeight;
                    if(placeLocation >= level.getMaxHeight()) break;
                    object.setBlockStateAt(x, placeLocation, z, BlockID.BASALT);
                }
            }
        }
        queueObject(chunk, object);
    }

    private ArrayList<Integer> getHighestWorkableBlocks(BlockManager level, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 126; y > 1; --y) {
            String b = level.getBlockIdIfCachedOrLoaded(x, y, z);
            if (b == Block.BLACKSTONE && level.getBlockIfCachedOrLoaded(x, y + 1, z).isAir()) {
                blockYs.add(y + 1);
            }
        }
        return blockYs;
    }

    @Override
    public String name() {
        return NAME;
    }
}
