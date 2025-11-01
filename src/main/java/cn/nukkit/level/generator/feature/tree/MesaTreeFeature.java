package cn.nukkit.level.generator.feature.tree;


import cn.nukkit.level.generator.feature.LegacyTreeGeneratorFeature;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class MesaTreeFeature extends LegacyTreeGeneratorFeature {

    public static final String NAME = "minecraft:mesa_tree_feature";

    @Override
    public LegacyTreeGenerator getGenerator(RandomSourceProvider random) {
        return new LegacyOakTree();
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
