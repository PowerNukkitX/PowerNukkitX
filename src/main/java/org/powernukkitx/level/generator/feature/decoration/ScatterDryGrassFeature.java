package org.powernukkitx.level.generator.feature.decoration;


import org.powernukkitx.block.BlockShortDryGrass;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTallDryGrass;
import org.powernukkitx.utils.random.NukkitRandom;

public class ScatterDryGrassFeature extends GroupedDiscFeature {

    private static final BlockState SHORT_DRY_GRASS = BlockShortDryGrass.PROPERTIES.getDefaultState();
    private static final BlockState DRY_GRASS = BlockTallDryGrass.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_dry_grass_feature";

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
        return -10;
    }

    @Override
    public int getRandom() {
        return 12;
    }

}
