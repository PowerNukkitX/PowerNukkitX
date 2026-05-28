package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectFallenTree;
import cn.nukkit.level.generator.object.ObjectFancyOakTree;
import cn.nukkit.level.generator.object.ObjectJungleTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;

public class JungleEdgeTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:legacy:jungle_edge_tree_feature";


    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return switch (random.nextInt(5)) {
            case 0, 1 -> random.nextInt(100) == 0 ? new ObjectFallenTree(WoodType.JUNGLE) : new ObjectJungleTree(7, 8);
            default -> new ObjectFancyOakTree();
        };
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.EDGE);
    }

    @Override
    public int getMin() {
        return 1;
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
