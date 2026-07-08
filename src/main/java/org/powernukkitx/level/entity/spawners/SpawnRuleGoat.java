package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleGoat extends SpawnRule {

    public SpawnRuleGoat() {
        super(Entity.GOAT, 1, 3, 5,
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockID.STONE, BlockID.SNOW, BlockID.POWDER_SNOW, BlockID.SNOW_LAYER, BlockID.PACKED_ICE, BlockID.GRAVEL),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.SNOWY_SLOPES, BiomeTags.JAGGED_PEAKS, BiomeTags.FROZEN_PEAKS),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        );
    }

}
