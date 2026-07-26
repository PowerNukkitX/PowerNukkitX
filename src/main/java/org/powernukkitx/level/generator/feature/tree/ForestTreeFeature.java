package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.level.generator.feature.LegacyTreeGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectFallenTree;
import org.powernukkitx.level.generator.object.TreeGenerator;
import org.powernukkitx.level.generator.object.legacytree.LegacyBirchTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyOakTree;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class ForestTreeFeature extends LegacyTreeGeneratorFeature {

    public static final String NAME = "minecraft:forest_surface_trees_feature";

    @Override
    public TreeGenerator getGenerator(RandomSourceProvider random) {
        boolean fallen = random.nextInt(100) == 0;
        if(random.nextInt(10) < 6) {
            return fallen ? new ObjectFallenTree() : new LegacyOakTree();
        } else return fallen ? new ObjectFallenTree(WoodType.BIRCH) : new LegacyBirchTree();
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
    protected float getBeeNestChance() {
        return 0.00035F;
    }

    @Override
    public String name() {
        return NAME;
    }
}
