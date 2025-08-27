package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectSavannaTree;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

public class SavannaTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:savanna_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(NukkitRandom random) {
        return new ObjectSavannaTree();
    }

    @Override
    public int getMin() {
        return 1;
    }

    @Override
    public int getMax() {
        return 2;
    }

    @Override
    public String getRequiredTag() {
        return BiomeTags.SAVANNA;
    }

    @Override
    public String name() {
        return NAME;
    }
}
