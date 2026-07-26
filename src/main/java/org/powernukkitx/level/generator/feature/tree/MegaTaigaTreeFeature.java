package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.GriddedFeature;
import org.powernukkitx.level.generator.object.ObjectBigSpruceTree;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectSmallSpruceTree;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class MegaTaigaTreeFeature extends GriddedFeature {

    public static final String NAME = "minecraft:mega_taiga_surface_trees_feature";
    public static final String ALIAS = "minecraft:scatter_taiga_plant_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        if (random.nextInt(5) < 2) {
            return new ObjectBigSpruceTree();
        } else return new ObjectSmallSpruceTree();
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return Registries.BIOME.containsTag(BiomeTags.TAIGA, definition);
    }

    @Override
    public int getDistanceToNextField() {
        return 1;
    }

    @Override
    public String name() {
        return NAME;
    }
}
