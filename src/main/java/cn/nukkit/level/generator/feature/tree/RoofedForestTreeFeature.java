package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectDarkOakTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class RoofedForestTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:roofed_forest_tree_feature_rules";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectDarkOakTree();
    }

    @Override
    public int getMin() {
        return 8;
    }

    @Override
    public int getMax() {
        return 10;
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return Registries.BIOME.containsTag(BiomeTags.ROOFED, definition);
    }

    @Override
    public String name() {
        return NAME;
    }
}
