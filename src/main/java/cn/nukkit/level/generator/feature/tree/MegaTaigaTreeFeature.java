package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.GriddedFeature;
import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectBigSpruceTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectLegacyObjectWrapper;
import cn.nukkit.level.generator.object.legacytree.LegacySpruceTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

public class MegaTaigaTreeFeature extends GriddedFeature {

    public static final String NAME = "minecraft:mega_taiga_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(NukkitRandom random) {
        if(random.nextInt(5) < 4) {
            return new ObjectBigSpruceTree();
        } else return new ObjectLegacyObjectWrapper(new LegacySpruceTree());
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.TAIGA);
    }

    @Override
    public int getSplit() {
        return 2;
    }

    @Override
    public String name() {
        return NAME;
    }
}
