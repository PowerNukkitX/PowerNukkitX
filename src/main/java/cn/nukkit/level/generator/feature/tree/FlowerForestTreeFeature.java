package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.object.ObjectFallenTree;
import cn.nukkit.level.generator.object.TreeGenerator;
import cn.nukkit.level.generator.object.legacytree.LegacyBirchTree;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;

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
