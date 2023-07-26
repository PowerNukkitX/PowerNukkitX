package cn.nukkit.level.generator.populator.helper;

import static cn.nukkit.block.BlockID.GRASS;

import cn.nukkit.level.format.FullChunk;

/**
 * @author DaPorkchop_
 */
public interface EnsureGrassBelow {
    static boolean ensureGrassBelow(int x, int y, int z, FullChunk chunk) {
        return EnsureBelow.ensureBelow(x, y, z, GRASS, chunk);
    }
}
