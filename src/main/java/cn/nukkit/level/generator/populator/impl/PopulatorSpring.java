package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.List;

/**
 * @author GoodLucky777
 */
public class PopulatorSpring extends Populator {

    private final BlockState state;
    private final List<BlockState> surroundState;
    private final int tries;
    private final int minHeight;
    private final int maxHeight;

    public PopulatorSpring(BlockState state, List<BlockState> surroundState, int tries, int minHeight, int maxHeight) {
        this.state = state;
        this.surroundState = surroundState;
        this.tries = tries;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int sourceX = chunkX << 4;
        int sourceZ = chunkZ << 4;

        for (int i = 0; i < tries; i++) {
            int x = sourceX + random.nextBoundedInt(16);
            int z = sourceZ + random.nextBoundedInt(16);
            int y = NukkitMath.randomRange(random, minHeight, maxHeight);

            if (!(level.getBlockStateAt(x, y, z).equals(BlockState.AIR) || surroundState.contains(level.getBlockStateAt(x, y, z)))) {
                continue;
            }

            if (!(surroundState.contains(level.getBlockStateAt(x, y - 1, z)) || surroundState.contains(level.getBlockStateAt(x, y + 1, z)))) {
                continue;
            }

            int surroundCount = 0;
            if (surroundState.contains(level.getBlockStateAt(x + 1, y, z))) surroundCount++;
            if (surroundState.contains(level.getBlockStateAt(x - 1, y, z))) surroundCount++;
            if (surroundState.contains(level.getBlockStateAt(x, y, z + 1))) surroundCount++;
            if (surroundState.contains(level.getBlockStateAt(x, y, z - 1))) surroundCount++;

            if (surroundCount != 3) {
                continue;
            }

            int airCount = 0;
            if (level.getBlockIdAt(x + 1, y, z) == AIR) airCount++;
            if (level.getBlockIdAt(x - 1, y, z) == AIR) airCount++;
            if (level.getBlockIdAt(x, y, z + 1) == AIR) airCount++;
            if (level.getBlockIdAt(x, y, z - 1) == AIR) airCount++;

            if (airCount != 1) {
                continue;
            }

            level.setBlockStateAt(x, y, z, state);
            level.getChunk(chunkX, chunkZ).getProvider().getLevel().scheduleUpdate(state.getBlock(), new Vector3(x, y, z), 1, 0, false);
        }
    }
}