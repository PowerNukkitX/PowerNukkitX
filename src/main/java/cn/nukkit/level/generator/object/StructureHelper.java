package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import org.apache.logging.log4j.util.InternalApi;

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

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return super.getBlockAt(x + origen.x, y + origen.y, z + origen.z);
    }

    @Override
    public void setBlockStateAt(int x, int y, int z, BlockState state) {
        super.setBlockStateAt(x + origen.x, y + origen.y, z + origen.z, state);
    }
}
