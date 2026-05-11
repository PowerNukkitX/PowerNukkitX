package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.feature.GriddedFeature;
import cn.nukkit.level.generator.object.ObjectFallenTree;
import cn.nukkit.level.generator.object.ObjectFancyOakTree;
import cn.nukkit.level.generator.object.ObjectJungleTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectJungleBigTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;

public class JungleTreeFeature extends GriddedFeature {

    public static final String NAME = "minecraft:jungle_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return switch (random.nextInt(10)) {
            case 0 -> new ObjectJungleBigTree(10, 20);
            case 4, 5, 6 -> random.nextInt(100) == 0 ? new ObjectFallenTree(WoodType.JUNGLE) : new ObjectJungleTree(4 + random.nextBoundedInt(7), 3);
            case 7, 8 -> new ObjectFancyOakTree();
            default -> random.nextInt(100) == 0 ? new ObjectFallenTree(WoodType.JUNGLE) : new ObjectJungleTree(7, 8);

        };
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.JUNGLE);
    }


    @Override
    public String name() {
        return NAME;
    }
}
