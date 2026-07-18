package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.object.ObjectFallenTree;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectSavannaTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyOakTree;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class SavannaMutatedTreeFeature extends SavannaTreeFeature {

    public static final String NAME = "minecraft:savanna_mutated_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return random.nextInt(3) == 0 ? (random.nextInt(100) == 0 ? new ObjectFallenTree() : new LegacyOakTree()) : new ObjectSavannaTree();
    }

    @Override
    public String name() {
        return NAME;
    }
}
