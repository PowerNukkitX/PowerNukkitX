package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;

public class GeneratorRoot extends BlockManager {

    public GeneratorRoot(Level level) {
        super(level);
    }

    public boolean canReplace(int x, int y, int z) {
        Block cached = getCachedBlock(x, y, z);
        if(cached == null) return true;
        return cached.canBeReplaced();
    }

    @Override
    public void setBlockStateAt(int x, int y, int z, BlockState state) {
        if(canReplace(x, y, z)) {
            super.setBlockStateAt(x, y, z, state);
        }
    }
}
