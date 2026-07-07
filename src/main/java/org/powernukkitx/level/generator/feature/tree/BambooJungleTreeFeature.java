package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBamboo;
import org.powernukkitx.level.generator.feature.ObjectGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectJungleBigTree;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class BambooJungleTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:bamboo_jungle_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectJungleBigTree(10, 20);
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return Registries.BIOME.containsTag(BiomeTags.BAMBOO, definition);
    }

    @Override
    public int getMin() {
        return -1;
    }

    @Override
    public int getMax() {
        return 1;
    }

    @Override
    protected boolean checkBlock(Block bl) {
        return super.checkBlock(bl) && !(bl instanceof BlockBamboo);
    }

    @Override
    public String name() {
        return NAME;
    }
}
