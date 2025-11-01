package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectMangroveTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class MangroveTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:mangrove_swamp_mangrove_tree_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        ObjectMangroveTree tree = new ObjectMangroveTree();
        tree.setWithBeenest(random.nextInt(15) == 0);
        return tree;
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.MANGROVE_SWAMP);
    }

    @Override
    public int getMin() {
        return 4;
    }

    @Override
    public int getMax() {
        return 7;
    }

    @Override
    public String name() {
        return NAME;
    }
}
