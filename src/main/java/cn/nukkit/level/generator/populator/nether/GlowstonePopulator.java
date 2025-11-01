package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.Xoroshiro128;

import java.util.ArrayList;

import static cn.nukkit.block.BlockID.GLOWSTONE;
import static cn.nukkit.block.BlockID.NETHERRACK;

public class GlowstonePopulator extends Populator {

    public static final String NAME = "nether_glowstone";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        BlockManager object = new BlockManager(level);
        if (random.nextInt(11) == 0) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(chunk, x & 0xF, z & 0xF);
            if (y != -1 && level.getBlockIdAt(x, y, z) != NETHERRACK) {
                int count = NukkitMath.randomRange(random, 40, 60);
                object.setBlockStateAt(x, y, z, GLOWSTONE);
                int cyclesNum = 0;
                while (count != 0) {
                    if (cyclesNum == 1500) break;
                    int spawnX = x + random.nextInt(9) - random.nextInt(9);
                    int spawnY = y - random.nextInt(9);
                    int spawnZ = z + random.nextInt(9) - random.nextInt(9);
                    if (cyclesNum % 128 == 0 && cyclesNum != 0) {
                        object.setBlockStateAt(x + random.nextInt(-3, 3), y - random.nextInt(5), z + random.nextInt(-3, 3), GLOWSTONE);
                        count--;
                    }
                    if (checkAroundBlock(spawnX, spawnY, spawnZ, object)) {
                        object.setBlockStateAt(spawnX, spawnY, spawnZ, GLOWSTONE);
                        count--;
                    }
                    cyclesNum++;
                }
            }
        }
        queueObject(chunk, object);
    }

    private int getHighestWorkableBlock(IChunk chunk, int x, int z) {
        int y;
        //start scanning a bit lower down to allow space for placing on top
        for (y = 125; y >= 0; y--) {
            String b = chunk.getBlockState(x, y, z).getIdentifier();
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }

    private boolean checkAroundBlock(int x, int y, int z, BlockManager level) {
        for (BlockFace i : BlockFace.values()) {
            if (level.getBlockIdAt(x + i.getXOffset(), y + i.getYOffset(), z + i.getZOffset()) == GLOWSTONE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String name() {
        return NAME;
    }
}
