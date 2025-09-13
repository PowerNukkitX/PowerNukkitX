package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockSweetBerryBush;
import cn.nukkit.level.generator.object.BlockManager;

import static cn.nukkit.block.property.CommonBlockProperties.GROWTH;

public class ScatterSweetBerryBushFeature extends SurfaceGenerateFeature {

    private static final BlockState STATE = BlockSweetBerryBush.PROPERTIES.getBlockState(GROWTH.createValue(GROWTH.getMax()));

    public static final String NAME = "minecraft:scatter_sweet_berry_bush_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, STATE);
    }

    @Override
    public int getBase() {
        return -66;
    }

    @Override
    public int getRandom() {
        return 70;
    }

    @Override
    public String name() {
        return NAME;
    }
}
