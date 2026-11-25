package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.GriddedFeature;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectSmallSpruceTree;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class GroveTreeFeature extends GriddedFeature {
    public static final String NAME = "minecraft:grove_spruce_tree_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectSmallSpruceTree();
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return Registries.BIOME.containsTag(BiomeTags.GROVE, definition);
    }

    @Override
    public int getSplit() {
        return 2;
    }

}
