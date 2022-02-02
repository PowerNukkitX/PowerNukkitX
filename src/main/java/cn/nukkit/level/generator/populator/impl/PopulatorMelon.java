package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
public class PopulatorMelon extends PopulatorSurfaceBlock {
    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        final int id = chunk.getBiomeId(x, y);
        return (id == EnumBiome.JUNGLE.id || id == EnumBiome.JUNGLE_EDGE.id || id == EnumBiome.JUNGLE_HILLS.id || id == EnumBiome.JUNGLE_EDGE_M.id
                || id == EnumBiome.JUNGLE_M.id)
                && EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return MELON_BLOCK << Block.DATA_BITS;
    }
}
