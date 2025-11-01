package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectCherryTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectSavannaTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class CherryTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:cherry_grove_after_surface_cherry_tree_feature_rules";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectCherryTree();
    }

    @Override
    public int getMin() {
        return 2;
    }

    @Override
    public int getMax() {
        return 4;
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.CHERRY_GROVE);
    }

    @Override
    public String name() {
        return NAME;
    }
}
