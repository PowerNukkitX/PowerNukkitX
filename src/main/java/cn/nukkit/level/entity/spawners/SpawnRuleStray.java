package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityStray;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

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
                new ConditionAny(
                        new ConditionAll(
                                new ConditionSpawnUnderground(),
                                new ConditionPopulationControl(EntityStray.class, new int[]{8, 16, 8})
                        ),
                        new ConditionAll(
                                new ConditionSpawnOnSurface(),
                                new ConditionPopulationControl(EntityStray.class, new int[]{8, 0, 10})
                        )
                )
        );
    }

}
