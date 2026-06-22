package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.feature.LegacyTreeGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectFallenTree;
import cn.nukkit.level.generator.object.TreeGenerator;
import cn.nukkit.level.generator.object.legacytree.LegacyBirchTree;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;

public class BirchForestTreeFeature extends LegacyTreeGeneratorFeature {

    public static final String NAME = "minecraft:birch_forest_surface_trees_feature";

    @Override
    public TreeGenerator getGenerator(RandomSourceProvider random) {
        return random.nextInt(100) == 0 ? new ObjectFallenTree(WoodType.BIRCH) : new LegacyBirchTree();
    }

    @Override
    public int getMin() {
        return 7;
    }

    @Override
    public int getMax() {
        return 8;
    }

    @Override
    public String getRequiredTag() {
        return BiomeTags.BIRCH;
    }

    @Override
    public String name() {
        return NAME;
    }
}
