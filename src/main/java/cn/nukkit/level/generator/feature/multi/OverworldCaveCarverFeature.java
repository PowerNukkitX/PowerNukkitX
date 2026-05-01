package cn.nukkit.level.generator.feature.multi;

import cn.nukkit.level.generator.feature.MultiFeatureWrapper;
import cn.nukkit.level.generator.feature.decoration.MonsterRoomFeature;
import cn.nukkit.level.generator.feature.terrain.CaveGenerateFeature;
import cn.nukkit.level.generator.feature.terrain.CaveExtraUndergroundFeature;
import cn.nukkit.level.generator.feature.terrain.CanyonCarverFeature;

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
