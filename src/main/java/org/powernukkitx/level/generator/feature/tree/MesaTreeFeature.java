package org.powernukkitx.level.generator.feature.tree;


import org.powernukkitx.level.generator.feature.LegacyTreeGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectFallenTree;
import org.powernukkitx.level.generator.object.TreeGenerator;
import org.powernukkitx.level.generator.object.legacytree.LegacyOakTree;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class MesaTreeFeature extends LegacyTreeGeneratorFeature {

    public static final String NAME = "minecraft:mesa_tree_feature";

    @Override
    public TreeGenerator getGenerator(RandomSourceProvider random) {
        return random.nextInt(100) == 0 ? new ObjectFallenTree() : new LegacyOakTree();
    }

    @Override
    public String getRequiredTag() {
        return BiomeTags.MESA;
    }

    @Override
    public String name() {
        return NAME;
    }
}
