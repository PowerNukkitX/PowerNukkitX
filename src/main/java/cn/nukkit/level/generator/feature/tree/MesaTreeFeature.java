package cn.nukkit.level.generator.feature.tree;


import cn.nukkit.level.generator.feature.LegacyTreeGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectFallenTree;
import cn.nukkit.level.generator.object.TreeGenerator;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

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
