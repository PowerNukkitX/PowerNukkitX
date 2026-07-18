package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.ObjectGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectDarkOakTree;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class RoofedForestTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:roofed_forest_tree_feature_rules";

    private final static ObjectGenerator GENERATOR = new ObjectDarkOakTree();

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return GENERATOR;
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
