package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.LegacyTreeGeneratorFeature;
import cn.nukkit.level.generator.object.legacytree.LegacyBirchTree;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class PlainsTreeFeature extends LegacyTreeGeneratorFeature {

    public static final String NAME = "minecraft:plains_surface_trees_feature";

    @Override
    public LegacyTreeGenerator getGenerator(RandomSourceProvider random) {
        if(random.nextInt(20) < 1) {
            return new LegacyOakTree();
        } else return null;
    }

    @Override
    public int getMin() {
        return 1;
    }

    @Override
    public int getMax() {
        return 1;
    }

    @Override
    public String getRequiredTag() {
        return BiomeTags.PLAINS;
    }

    @Override
    public String name() {
        return NAME;
    }
}
