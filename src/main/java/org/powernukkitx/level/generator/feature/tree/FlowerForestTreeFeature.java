package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.level.generator.object.ObjectFallenTree;
import org.powernukkitx.level.generator.object.TreeGenerator;
import org.powernukkitx.level.generator.object.legacytree.LegacyBirchTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyOakTree;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class FlowerForestTreeFeature extends ForestTreeFeature {

    public static final String NAME = "minecraft:flower_forest_surface_trees_feature";

    @Override
    public TreeGenerator getGenerator(RandomSourceProvider random) {
        boolean fallen = random.nextInt(100) == 0;
        if(random.nextInt(10) < 6) {
            return new LegacyOakTree();
        } else return fallen ? new ObjectFallenTree(WoodType.BIRCH) : new LegacyBirchTree();
    }

    @Override
    public int getMin() {
        return 8;
    }

    @Override
    public int getMax() {
        return 6;
    }

    @Override
    public String getRequiredTag() {
        return BiomeTags.FLOWER_FOREST;
    }

    @Override
    public String name() {
        return NAME;
    }
}
