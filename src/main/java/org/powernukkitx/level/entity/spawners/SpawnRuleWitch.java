package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleWitch extends SpawnRule {

    public SpawnRuleWitch() {
        super(Entity.WITCH, 5,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionBiomeFilter(BiomeTags.MONSTER),
                new ConditionDensityLimit(Entity.WITCH, 1, 128),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
        );
    }

}
