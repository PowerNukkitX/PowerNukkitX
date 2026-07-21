package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.level.generator.feature.ObjectGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectBigMushroom;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class HugeMushroomFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:huge_mushroom_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectBigMushroom();
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return Registries.BIOME.containsTag(BiomeTags.ROOFED, definition)
                || Registries.BIOME.containsTag(BiomeTags.SWAMP, definition)
                || Registries.BIOME.containsTag(BiomeTags.MOOSHROOM_ISLAND, definition);
    }

    @Override
    public int getMin() {
        return -4;
    }

    @Override
    public int getMax() {
        return 1;
    }

    @Override
    public String name() {
        return NAME;
    }
}
