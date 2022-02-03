package cn.nukkit.level.generator.populator.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlockPN;
import cn.nukkit.math.NukkitRandom;

/**
 * @author Superice666 超神的冰凉
 */
public class PopulatorGroundSoulFire extends PopulatorSurfaceBlockPN {
    public PopulatorGroundSoulFire() {
        this.setBaseAmount(6);
        this.setRandomAmount(4);
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, SOUL_SAND, chunk);
    }

    @Override
    protected BlockState getBlockState(int x, int z, NukkitRandom random, FullChunk chunk) {
        return BlockState.of(SOUL_FIRE);
    }


    @Override
    protected void placeBlock(int x, int y, int z, BlockState blockState, FullChunk chunk, NukkitRandom random) {
        super.placeBlock(x, y, z, blockState, chunk, random);
        chunk.setBlockLight(x, y, z, Block.getLightLevel(SOUL_FIRE));
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        int y;
        for (y = 0; y <= 127; ++y) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
