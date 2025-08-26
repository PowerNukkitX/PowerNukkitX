package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.BlockJungleLeaves;
import cn.nukkit.block.BlockJungleLog;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.NewJungleTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectJungleBigTree;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.NukkitRandom;

public class LegacyJungleTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:jungle_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(NukkitRandom random) {
        return switch (random.nextInt(5)) {
            case 0, 1, 3 -> new ObjectJungleBigTree(10, 20,
                    BlockJungleLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Y),
                    BlockJungleLeaves.PROPERTIES.getDefaultState());
            case 4 -> new NewJungleTree(4 + random.identical().nextBoundedInt(7), 3);
            default -> new NewJungleTree(1, 2);
        };
    }

    @Override
    public int getMin() {
        return 6;
    }

    @Override
    public int getMax() {
        return 10;
    }

    @Override
    public String name() {
        return NAME;
    }
}
