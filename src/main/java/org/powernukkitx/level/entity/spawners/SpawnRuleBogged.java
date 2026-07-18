package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleBogged extends SpawnRule {

    public SpawnRuleBogged() {
        super(Entity.BOGGED, 1, 2, 40,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionBiomeFilter(BiomeTags.SWAMP, BiomeTags.MANGROVE_SWAMP),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
        );
    }

}
