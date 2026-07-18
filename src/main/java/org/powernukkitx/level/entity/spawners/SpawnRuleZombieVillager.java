package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleZombieVillager extends SpawnRule {

    public SpawnRuleZombieVillager() {
        super(Entity.ZOMBIE_VILLAGER_V2, 2, 4, 5,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionBiomeFilter(BiomeTags.MONSTER),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
        );
    }

}
