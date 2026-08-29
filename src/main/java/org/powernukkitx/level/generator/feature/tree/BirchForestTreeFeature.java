package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.level.generator.feature.LegacyTreeGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectFallenTree;
import org.powernukkitx.level.generator.object.TreeGenerator;
import org.powernukkitx.level.generator.object.legacytree.LegacyBirchTree;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

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
    protected float getBeeNestChance() {
        return 0.00035F;
    }

    @Override
    public String name() {
        return NAME;
    }
}
