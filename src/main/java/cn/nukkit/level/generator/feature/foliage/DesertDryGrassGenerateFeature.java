package cn.nukkit.level.generator.feature.foliage;


import cn.nukkit.block.BlockShortDryGrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockTallDryGrass;
import cn.nukkit.utils.random.NukkitRandom;

public class DesertDryGrassGenerateFeature extends GroupedDiscFeature {

    private static final BlockState SHORT_DRY_GRASS = BlockShortDryGrass.PROPERTIES.getDefaultState();
    private static final BlockState DRY_GRASS = BlockTallDryGrass.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:desert_after_surface_dry_grass_feature_rules";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public BlockState getSourceBlock() {
        return new NukkitRandom().nextInt(3) == 0 ? DRY_GRASS : SHORT_DRY_GRASS;
    }

    @Override
    public int getMinRadius() {
        return 3;
    }

    @Override
    public int getMaxRadius() {
        return 4;
    }

    @Override
    public double getProbability() {
        return 0.4f;
    }

    @Override
    public int getBase() {
        return -19;
    }

    @Override
    public int getRandom() {
        return 20;
    }

}
