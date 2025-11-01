package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectSwampOakTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class SwampTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:swamp_oak_tree_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectSwampOakTree(7, 8);
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.SWAMP);
    }

    @Override
    public int getMin() {
        return 3;
    }

    @Override
    public int getMax() {
        return 5;
    }

    @Override
    public String name() {
        return NAME;
    }

    protected boolean checkBlock(Block bl) {
        return bl.canBeReplaced();
    }
}
