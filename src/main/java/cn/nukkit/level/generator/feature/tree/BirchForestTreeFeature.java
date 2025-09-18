package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.LegacyTreeGeneratorFeature;
import cn.nukkit.level.generator.object.legacytree.LegacyBirchTree;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTallBirchTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class BirchForestTreeFeature extends LegacyTreeGeneratorFeature {

    public static final String NAME = "minecraft:birch_forest_surface_trees_feature";

    @Override
    public LegacyTreeGenerator getGenerator(RandomSourceProvider random) {
        if(random.nextInt(10) < 1) {
            return new LegacyTallBirchTree();
        } else return new LegacyBirchTree();
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
