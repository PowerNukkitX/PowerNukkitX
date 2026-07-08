package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleStray extends SpawnRule {

    public SpawnRuleStray() {
        super(Entity.STRAY, 1, 2, 120,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionBiomeFilter(BiomeTags.FROZEN),
                new ConditionAny(
                        new ConditionNot(new ConditionBiomeFilter(BiomeTags.OCEAN)),
                        new ConditionAll(
                                new ConditionBiomeFilter(BiomeTags.OCEAN),
                                new ConditionHeightFilter(60, 66)
                        )
                ),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)

        );
    }

}
