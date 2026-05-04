package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntitySpider;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleSpider extends SpawnRule {

    public SpawnRuleSpider() {
        super(Entity.SPIDER, 100,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionBiomeFilter(BiomeTags.MONSTER),
                new ConditionAny(
                        new ConditionAll(
                                new ConditionSpawnUnderground(),
                                new ConditionPopulationControl(EntitySpider.class, new int[]{8, 16, 8})
                        ),
                        new ConditionAll(
                                new ConditionSpawnOnSurface(),
                                new ConditionPopulationControl(EntitySpider.class, new int[]{8, 0, 10})
                        )
                ));
    }

}
