package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.format.FullChunk;

import static cn.nukkit.block.BlockID.AIR;


/**
 * @author LT_Name
 */
public interface EnsureAround {
    static boolean ensureAroundAir(int x, int y, int z, FullChunk chunk) {
        return chunk.getBlockId(x + 1, y, z) == AIR &&
                chunk.getBlockId(x - 1, y, z) == AIR &&
                chunk.getBlockId(x, y, z + 1) == AIR &&
                chunk.getBlockId(x, y, z - 1) == AIR;
    }
}