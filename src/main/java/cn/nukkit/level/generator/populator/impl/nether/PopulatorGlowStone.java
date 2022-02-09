package cn.nukkit.level.generator.populator.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

public class PopulatorGlowStone extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextBoundedInt(10) == 0) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(chunk, x & 0xF, z & 0xF);
            if (y != -1 && level.getBlockIdAt(x, y, z) != NETHERRACK) {
                int count = NukkitMath.randomRange(random, 40, 60);
                level.setBlockAt(x, y, z, GLOWSTONE);
                int cyclesNum = 0;
                while (count != 0) {
                    if (cyclesNum == 1500) break;
                    int spawnX = x + random.nextBoundedInt(8) - random.nextBoundedInt(8);
                    int spawnY = y - random.nextBoundedInt(5);
                    int spawnZ = z + random.nextBoundedInt(8) - random.nextBoundedInt(8);
                    if (cyclesNum >= 128 && (cyclesNum & cyclesNum-1) == 0) {
                        level.setBlockAt(x + random.nextRange(-3, 3), y - random.nextBoundedInt(4), z + random.nextRange(-3, 3), GLOWSTONE);
                        count--;
                    }
                    if (checkAroundBlock(spawnX, spawnY, spawnZ, level)) {
                        level.setBlockAt(spawnX, spawnY, spawnZ, GLOWSTONE);
                        count--;
                    }
                    cyclesNum++;
                }
            }
        }
    }

    private int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        //start scanning a bit lower down to allow space for placing on top
        for (y = 125; y >= 0; y--) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }

    private boolean checkAroundBlock(int x, int y, int z, ChunkManager level) {
        for (BlockFace i : BlockFace.values()) {
            if (level.getBlockIdAt(x + i.getXOffset(), y + i.getYOffset(), z + i.getZOffset()) == BlockID.GLOWSTONE) {
                return true;
            }
        }
        return false;
    }
}
