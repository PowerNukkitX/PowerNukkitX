package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.LegacyTreeGeneratorFeature;
import cn.nukkit.level.generator.object.legacytree.LegacyBirchTree;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

public class ForestTreeFeature extends LegacyTreeGeneratorFeature {

    public static final String NAME = "minecraft:forest_surface_trees_feature";

    @Override
    public LegacyTreeGenerator getGenerator(NukkitRandom random) {
        if(random.nextInt(10) < 6) {
            return new LegacyOakTree();
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
        return BiomeTags.FOREST;
    }

    @Override
    public String name() {
        return NAME;
    }
}
