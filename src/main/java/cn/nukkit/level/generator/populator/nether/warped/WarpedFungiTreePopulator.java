package cn.nukkit.level.generator.populator.nether.warped;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.legacytree.LegacyCrimsonTree;
import cn.nukkit.level.generator.object.legacytree.LegacyWarpedTree;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.Objects;

public class WarpedFungiTreePopulator extends Populator {

    public static final String NAME = "nether_warped_fungi_tree";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        BlockManager object = new BlockManager(level);
        int amount = random.nextInt(6) + 4;

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            if(level.getBiomeId(x, 0, z) != BiomeID.WARPED_FOREST) continue;
            ArrayList<Integer> ys = this.getHighestWorkableBlocks(object, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                if (random.nextInt(4) == 1) continue;
                new LegacyWarpedTree().placeObject(object, x, y, z, random);
            }
        }
        queueObject(chunk, object);
    }

    private ArrayList<Integer> getHighestWorkableBlocks(BlockManager level, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            String b = level.getBlockIdAt(x, y, z);
            if ((Objects.equals(b, Block.WARPED_NYLIUM)) && level.getBlockAt(x, y + 1, z).canBeReplaced()) {
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
