package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.FullChunk;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.HashSet;

/**
 * @author DaPorkchop_
 */
public final class PopulatorHelpers implements BlockID {

    private static final IntSet nonSolidBlocks = new IntOpenHashSet();

    static {
        nonSolidBlocks.add(AIR);
        nonSolidBlocks.add(LEAVES);
        nonSolidBlocks.add(LEAVES2);
        nonSolidBlocks.add(SNOW_LAYER);
    }
    
    private static final HashSet<BlockState> nonOceanSolidBlocks = new HashSet<BlockState>(3);
    
    static {
        nonOceanSolidBlocks.add(BlockState.of(AIR));
        nonOceanSolidBlocks.add(BlockState.of(WATER));
        nonOceanSolidBlocks.add(BlockState.of(STILL_WATER));
        nonOceanSolidBlocks.add(BlockState.of(ICE));
        nonOceanSolidBlocks.add(BlockState.of(PACKED_ICE));
        nonOceanSolidBlocks.add(BlockState.of(BLUE_ICE));
    }
    
    private PopulatorHelpers() {
    }

    public static boolean canGrassStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    public static boolean isNonSolid(int id) {
        return nonSolidBlocks.contains(id);
    }
    
    public static boolean isNonOceanSolid(BlockState blockState) {
        return nonOceanSolidBlocks.contains(blockState);
    }
}
