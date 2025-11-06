package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.MultiFeatureWrapper;
import cn.nukkit.level.generator.feature.decoration.DeadBushFeature;
import cn.nukkit.level.generator.feature.decoration.MesaFoliageFeature;

public class MesaPlateauStoneTreeFeature extends MultiFeatureWrapper {

    public static final String NAME = "minecraft:mesa_plateau_stone_surface_trees_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected String[] getFeatures() {
        return new String[] {
                DeadBushFeature.NAME,
                MesaFoliageFeature.NAME,
                MesaTreeFeature.NAME
        };
    }
}
