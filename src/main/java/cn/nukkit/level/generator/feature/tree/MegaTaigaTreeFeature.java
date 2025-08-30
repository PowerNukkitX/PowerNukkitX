package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectLegacyObjectWrapper;
import cn.nukkit.level.generator.object.ObjectSavannaTree;
import cn.nukkit.level.generator.object.legacytree.LegacyBigSpruceTree;
import cn.nukkit.level.generator.object.legacytree.LegacySpruceTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

public class MegaTaigaTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:mega_taiga_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(NukkitRandom random) {
        LegacyTreeGenerator generator = random.nextInt(3) != 0 ?
                new LegacyBigSpruceTree(0.75f, 4) :
                new LegacySpruceTree();
        return new ObjectLegacyObjectWrapper(generator);
    }

    @Override
    public int getMin() {
        return 2;
    }

    @Override
    public int getMax() {
        return 4;
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.TAIGA);
    }

    @Override
    public String name() {
        return NAME;
    }
}
