package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleGoat extends SpawnRule {

    public SpawnRuleGoat() {
        super(Entity.GOAT, 1, 3,
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockID.STONE, BlockID.SNOW, BlockID.POWDER_SNOW, BlockID.SNOW_LAYER, BlockID.PACKED_ICE, BlockID.GRAVEL),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.SNOWY_SLOPES, BiomeTags.JAGGED_PEAKS, BiomeTags.FROZEN_PEAKS),
                new ConditionDensityLimit(Entity.GOAT, 3));
    }

}
