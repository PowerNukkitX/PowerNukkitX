package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectLegacyObjectWrapper;
import cn.nukkit.level.generator.object.ObjectSavannaTree;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

public class SavannaMutatedTreeFeature extends SavannaTreeFeature {

    public static final String NAME = "minecraft:savanna_mutated_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(NukkitRandom random) {
        return random.nextInt(3) == 0 ? new ObjectLegacyObjectWrapper(new LegacyOakTree()) : new ObjectSavannaTree();
    }

    @Override
    public String name() {
        return NAME;
    }
}
