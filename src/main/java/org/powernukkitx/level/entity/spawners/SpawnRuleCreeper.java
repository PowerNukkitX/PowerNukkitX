package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleCreeper extends SpawnRule {

    public SpawnRuleCreeper() {
        super(Entity.CREEPER, 100,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionBiomeFilter(BiomeTags.MONSTER),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER),

                new ConditionAny(
                        new ConditionSpawnUnderground(),
                        new ConditionAll(
                                new ConditionSpawnOnSurface(),
                                new ConditionDensityLimit(EntityID.CREEPER, 5)
                        )
                )
        );
    }

}
