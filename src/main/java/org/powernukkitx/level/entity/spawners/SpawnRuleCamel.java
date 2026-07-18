package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleCamel extends SpawnRule {

    public SpawnRuleCamel() {
        super(Entity.CAMEL, 1,
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockID.SAND, BlockID.RED_SAND, BlockID.SANDSTONE),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.DESERT),
                new ConditionDensityLimit(Entity.CAMEL, 1, 128),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL));
    }

}
