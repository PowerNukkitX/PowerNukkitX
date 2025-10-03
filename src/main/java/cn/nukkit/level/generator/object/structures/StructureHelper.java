package cn.nukkit.level.generator.object.structures;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.random.RandomSourceProvider;
import org.apache.logging.log4j.util.InternalApi;

import java.util.Map;

@InternalApi
public class StructureHelper extends BlockManager {

    final BlockVector3 origen;

    public StructureHelper(Level level, BlockVector3 origen) {
        super(level);
        this.origen = origen;
    }

    /**
     * Fills a box with the given block.
     *
     * @param min    the minimum coordinates, relative to this structure's root point
     * @param max    the maximum coordinates, relative to this structure's root point
     * @param state  the block state
     */
    public void fill(BlockVector3 min, BlockVector3 max, BlockState state) {
        this.fill(min, max, state, state);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min     the minimum coordinates, relative to this structure's root point
     * @param max     the maximum coordinates, relative to this structure's root point
     * @param outer   the block for the faces
     * @param inner   the block for the interior
     */
    public void fill(BlockVector3 min, BlockVector3 max, BlockState outer, BlockState inner) {
        for (int y = min.y; y <= max.y; y++) {
            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    BlockState state;
                    if (x != min.x && x != max.x && z != min.z && z != max.z && y != min.y && y != max.y) {
                        state = inner;
                    } else {
                        state = outer;
                    }
                    this.setBlockStateAt(new BlockVector3(x, y, z), state);
                }
            }
        }
    }
    public void setBlockWithRandomBlock(BlockVector3 pos, RandomSourceProvider random, Map<BlockState, Integer> blocks) {
        BlockState state = this.getRandomBlock(random, blocks);
        this.setBlockStateAt(pos, state);
    }
    public void addRandomBlock(Map<BlockState, Integer> blocks, int weight, BlockState state) {
        blocks.put(state, weight);
    }

    /**
     * Chooses a random block from a weighted list.
     *
     * @param random the PRNG to use
     * @param blocks a map of blocks to integer weights
     * @return a random block full id
     */
    public BlockState getRandomBlock(RandomSourceProvider random, Map<BlockState, Integer> blocks) {
        int totalWeight = 0;
        for (int weight : blocks.values()) {
            totalWeight += weight;
        }
        int weight = random.nextBoundedInt(totalWeight);
        for (Map.Entry<BlockState, Integer> entry : blocks.entrySet()) {
            weight -= entry.getValue();
            if (weight < 0) {
                return entry.getKey();
            }
        }
        return blocks.keySet().stream().findAny().get();
    }

    public void fillWithRandomBlock(BlockVector3 min, BlockVector3 max, RandomSourceProvider random, Map<BlockState, Integer> blocks) {
        for (int y = min.y; y <= max.y; y++) {
            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    BlockState state = this.getRandomBlock(random, blocks);
                    this.setBlockStateAt(new BlockVector3(x, y, z), state);
                }
            }
        }
    }

    public void setBlockDownward(BlockVector3 pos, BlockState state) {
        int y = pos.y;
        while (getBlockAt(pos.getX(), y, pos.getZ()).canBeReplaced() && y > 1) {
            setBlockStateAt(pos.getX(), y, pos.getZ(), state);
            y--;
        }
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return super.getBlockAt(x + origen.x, y + origen.y, z + origen.z);
    }

    @Override
    public void setBlockStateAt(int x, int y, int z, BlockState state) {
        super.setBlockStateAt(x + origen.x, y + origen.y, z + origen.z, state);
    }
}
