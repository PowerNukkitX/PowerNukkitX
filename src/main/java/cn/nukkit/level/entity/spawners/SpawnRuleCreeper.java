package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

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
