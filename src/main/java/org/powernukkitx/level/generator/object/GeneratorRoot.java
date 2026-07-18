package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBamboo;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;

public class GeneratorRoot extends BlockManager {

    public GeneratorRoot(Level level) {
        super(level);
    }

    public boolean canReplace(int x, int y, int z) {
        Block cached = getCachedBlock(x, y, z);
        if(cached == null) return true;
        return (cached.canBeReplaced() || cached.canPassThrough() || cached.diffusesSkyLight()) && !(cached instanceof BlockBamboo);
    }

    @Override
    public void setBlockStateAt(int x, int y, int z, BlockState state) {
        if(canReplace(x, y, z)) {
            super.setBlockStateAt(x, y, z, state);
        }
    }
}
