package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectLegacyObjectWrapper;
import cn.nukkit.level.generator.object.ObjectSavannaTree;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.utils.random.RandomSourceProvider;

public class SavannaMutatedTreeFeature extends SavannaTreeFeature {

    public static final String NAME = "minecraft:savanna_mutated_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return random.nextInt(3) == 0 ? new ObjectLegacyObjectWrapper(new LegacyOakTree()) : new ObjectSavannaTree();
    }

    @Override
    public String name() {
        return NAME;
    }
}
