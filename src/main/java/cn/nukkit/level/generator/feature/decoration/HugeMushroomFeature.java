package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectBigMushroom;
import cn.nukkit.level.generator.object.ObjectDarkOakTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class HugeMushroomFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:huge_mushroom_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectBigMushroom();
    }

    @Override
    public int getMin() {
        return -4;
    }

    @Override
    public int getMax() {
        return 1;
    }

    @Override
    public String name() {
        return NAME;
    }
}
