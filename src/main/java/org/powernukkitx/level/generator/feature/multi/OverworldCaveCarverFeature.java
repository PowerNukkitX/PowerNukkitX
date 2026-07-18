package org.powernukkitx.level.generator.feature.multi;

import org.powernukkitx.level.generator.feature.MultiFeatureWrapper;
import org.powernukkitx.level.generator.feature.decoration.MonsterRoomFeature;
import org.powernukkitx.level.generator.feature.terrain.CaveGenerateFeature;
import org.powernukkitx.level.generator.feature.terrain.CaveExtraUndergroundFeature;
import org.powernukkitx.level.generator.feature.terrain.CanyonCarverFeature;

public class OverworldCaveCarverFeature extends MultiFeatureWrapper {

    public static final String NAME = "minecraft:overworld_cave_carver_feature";

    @Override
    protected String[] getFeatures() {
        return new String[] {
                CaveGenerateFeature.NAME,
                CaveExtraUndergroundFeature.NAME,
                CanyonCarverFeature.NAME,
                MonsterRoomFeature.NAME
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
