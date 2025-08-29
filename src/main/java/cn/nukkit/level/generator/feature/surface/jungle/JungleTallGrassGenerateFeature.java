package cn.nukkit.level.generator.feature.surface.jungle;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockTallGrass;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.generator.feature.surface.SurfaceGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;

public class JungleTallGrassGenerateFeature extends SurfaceGenerateFeature {

    private static final BlockState LOWER = BlockTallGrass.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(false));
    private static final BlockState UPPER = BlockTallGrass.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true));


    public static final String NAME = "minecraft:jungle_after_surface_tall_grass_feature_rules";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, LOWER);
        manager.setBlockStateAt(x, y+1, z, UPPER);
    }

    @Override
    public int getBase() {
        return 5;
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
