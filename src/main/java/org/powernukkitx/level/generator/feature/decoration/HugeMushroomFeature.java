package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.level.generator.feature.ObjectGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectBigMushroom;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.utils.random.RandomSourceProvider;

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
