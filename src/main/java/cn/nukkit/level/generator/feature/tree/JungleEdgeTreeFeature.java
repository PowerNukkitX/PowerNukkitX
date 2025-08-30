package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.NewJungleTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectLegacyObjectWrapper;
import cn.nukkit.level.generator.object.legacytree.LegacyOakTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.NukkitRandom;

public class JungleEdgeTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:jungle_edge_surface_trees_feature";

    private static final ObjectLegacyObjectWrapper OAK_TREE = new ObjectLegacyObjectWrapper(new LegacyOakTree());

    @Override
    public ObjectGenerator getGenerator(NukkitRandom random) {
        return switch (random.nextInt(5)) {
            case 0, 1 -> new NewJungleTree(7, 8);
            default -> OAK_TREE;
        };
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return Registries.BIOME.getBiomeId(definition.getName()) == BiomeID.JUNGLE_EDGE;
    }

    @Override
    public int getMin() {
        return 0;
    }

    @Override
    public int getMax() {
        return 2;
    }

    @Override
    public String name() {
        return NAME;
    }
}
