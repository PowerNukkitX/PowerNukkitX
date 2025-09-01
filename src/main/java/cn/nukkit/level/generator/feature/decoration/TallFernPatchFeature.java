package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockLargeFern;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockTallGrass;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.generator.object.BlockManager;

public class TallFernPatchFeature extends SurfaceGenerateFeature {

    private static final BlockState LOWER = BlockLargeFern.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(false));
    private static final BlockState UPPER = BlockLargeFern.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true));

    public static final String NAME = "minecraft:fern_double_plant_patch_feature";

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
