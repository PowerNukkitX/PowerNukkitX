package org.powernukkitx.level.generator.populator.nether;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFire;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockSoulFire;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;

import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.math.NukkitMath;

import java.util.ArrayList;

public class FirePopulator extends Populator {

    public static final String NAME = "nether_fire";

    protected static final BlockState FIRE = BlockFire.PROPERTIES.getDefaultState();
    protected static final BlockState SOUL_FIRE = BlockSoulFire.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        BlockManager object = new BlockManager(level);
        int amount = random.nextInt(2) + 1;

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            ArrayList<Integer> ys = this.getHighestWorkableBlocks(object, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                if (random.nextInt(4) == 1) continue;
                object.setBlockStateAt(x, y, z, object.getBlockAt(x, y-1, z).getId() == BlockID.NETHERRACK ? FIRE : SOUL_FIRE);
            }
        }
        queueObject(chunk, object);
    }

    private ArrayList<Integer> getHighestWorkableBlocks(BlockManager level, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            String b = level.getBlockIdIfCachedOrLoaded(x, y, z);
            if ((b == Block.NETHERRACK || b == Block.SOUL_SAND || b == Block.SOUL_SOIL) && level.getBlockIfCachedOrLoaded(x, y + 1, z).isAir()) {
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
