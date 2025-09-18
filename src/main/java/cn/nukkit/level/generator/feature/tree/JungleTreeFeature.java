package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.BlockJungleLeaves;
import cn.nukkit.block.BlockJungleLog;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.generator.feature.GriddedFeature;
import cn.nukkit.level.generator.object.ObjectJungleTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectJungleBigTree;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class JungleTreeFeature extends GriddedFeature {

    public static final String NAME = "minecraft:jungle_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return switch (random.nextInt(9)) {
            case 0, 1, 3, 4, 5 -> new ObjectJungleBigTree(10, 20,
                    BlockJungleLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Y),
                    BlockJungleLeaves.PROPERTIES.getDefaultState());
            case 6 -> new ObjectJungleTree(4 + random.nextBoundedInt(7), 3);
            default -> new ObjectJungleTree(7, 8);
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
