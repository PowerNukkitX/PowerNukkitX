package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.GriddedFeature;
import cn.nukkit.level.generator.feature.LegacyTreeGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectLegacyObjectWrapper;
import cn.nukkit.level.generator.object.legacytree.LegacyBirchTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTallBirchTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class BirchForestMutatedTreeFeature extends GriddedFeature {

    public static final String NAME = "minecraft:legacy:birch_forest_mutated_tree_feature";


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectLegacyObjectWrapper(new LegacyTallBirchTree());
    }
}
