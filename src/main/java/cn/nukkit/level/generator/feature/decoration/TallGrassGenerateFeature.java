package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockTallGrass;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.generator.object.BlockManager;

public class TallGrassGenerateFeature extends SurfaceGenerateFeature {

    private static final BlockState LOWER = BlockTallGrass.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(false));
    private static final BlockState UPPER = BlockTallGrass.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true));

    public static final String PLAINS = "minecraft:plains_first_double_plant_grass_feature";
    public static final String JUNGLE = "minecraft:jungle_after_surface_tall_grass_feature_rules";
    public static final String SAVANNA = "minecraft:savanna_first_double_plant_grass_feature";

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
        return "minecraft:tall_grass_feature";
    }
}
