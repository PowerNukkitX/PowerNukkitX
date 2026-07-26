package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.ObjectGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectJungleBush;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class JungleBushFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:jungle_bush";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectJungleBush();
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return Registries.BIOME.containsTag(BiomeTags.JUNGLE, definition);
    }


    @Override
    public String name() {
        return NAME;
    }
}
