package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockBush;
import cn.nukkit.block.BlockShortGrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.object.BlockManager;

public class BushFeature extends SurfaceGenerateFeature {

    private static final BlockState STATE = BlockBush.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_bush_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, STATE);
    }

    @Override
    public int getBase() {
        return 8;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }
}
