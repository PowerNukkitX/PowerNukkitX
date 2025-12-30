package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.GriddedFeature;
import cn.nukkit.level.generator.object.ObjectBigSpruceTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectSmallSpruceTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class MegaTaigaTreeFeature extends GriddedFeature {

    public static final String NAME = "minecraft:mega_taiga_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        if(random.nextInt(5) < 2) {
            return new ObjectBigSpruceTree();
        } else return new ObjectSmallSpruceTree();
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
    public int getDistanceToNextField() {
        return 1;
    }

    @Override
    public String name() {
        return NAME;
    }
}
