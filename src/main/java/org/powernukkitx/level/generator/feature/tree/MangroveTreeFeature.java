package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.ObjectGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectMangroveTree;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class MangroveTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:mangrove_swamp_mangrove_tree_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        ObjectMangroveTree tree = new ObjectMangroveTree(random.nextFloat() > 0.15F);
        tree.setWithBeenest(random.nextFloat() < 0.01F);
        return tree;
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return Registries.BIOME.containsTag(BiomeTags.MANGROVE_SWAMP, definition);
    }

    @Override
    public int getMin() {
        return 12;
    }

    @Override
    public int getMax() {
        return 15;
    }

    @Override
    public String name() {
        return NAME;
    }
}
