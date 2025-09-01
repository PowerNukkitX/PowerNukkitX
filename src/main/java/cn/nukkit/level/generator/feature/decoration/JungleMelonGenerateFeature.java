package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockMelonBlock;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.object.BlockManager;

public class JungleMelonGenerateFeature extends SurfaceGenerateFeature {

    private static final BlockState MELON = BlockMelonBlock.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:jungle_after_surface_melon_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int getBase() {
        return -1500;
    }

    @Override
    public int getRandom() {
        return 1520;
    }

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, MELON);
    }
}
