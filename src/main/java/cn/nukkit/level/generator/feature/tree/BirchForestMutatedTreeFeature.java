package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.feature.GriddedFeature;
import cn.nukkit.level.generator.object.ObjectFallenTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.legacytree.LegacyBirchTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTallBirchTree;
import cn.nukkit.utils.random.RandomSourceProvider;

public class BirchForestMutatedTreeFeature extends GriddedFeature {

    public static final String NAME = "minecraft:legacy:birch_forest_mutated_tree_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        boolean fallen = random.nextInt(100) == 0;
        if(random.nextBoolean()) {
            return fallen ? new ObjectFallenTree(WoodType.BIRCH, 4,10) : new LegacyTallBirchTree();
        } else return fallen ? new ObjectFallenTree(WoodType.BIRCH) : new LegacyBirchTree();
    }
}
