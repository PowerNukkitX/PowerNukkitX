package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.ObjectGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectPaleOakTree;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class PaleGardenTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:random_pale_oak_tree_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        ObjectPaleOakTree object = new ObjectPaleOakTree();
        object.tryCreakingHeart = random.nextInt(4) == 0;
        return object;
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
        return Registries.BIOME.containsTag(BiomeTags.PALE_GARDEN, definition);
    }

    @Override
    public String name() {
        return NAME;
    }
}
