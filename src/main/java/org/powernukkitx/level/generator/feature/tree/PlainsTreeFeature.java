package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.LegacyTreeGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectFallenTree;
import org.powernukkitx.level.generator.object.TreeGenerator;
import org.powernukkitx.level.generator.object.legacytree.LegacyOakTree;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class PlainsTreeFeature extends LegacyTreeGeneratorFeature {

    public static final String NAME = "minecraft:plains_surface_trees_feature";

    @Override
    public TreeGenerator getGenerator(RandomSourceProvider random) {
        if(random.nextInt(20) < 1) {
            return random.nextInt(100) == 0 ? new ObjectFallenTree() : new LegacyOakTree();
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
    protected float getBeeNestChance() {
        return 0.05F;
    }

    @Override
    public String name() {
        return NAME;
    }
}
