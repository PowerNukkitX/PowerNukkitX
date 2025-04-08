package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionAll;
import cn.nukkit.level.entity.condition.ConditionAny;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionDifficultyFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderwater;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleDrowned extends SpawnRule {

    public SpawnRuleDrowned() {
        super(Entity.DROWNED, 2, 4,
                new ConditionSpawnOnGround(true),
                new ConditionDifficultyFilter(),
                new ConditionSpawnUnderwater(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionAny(
                        new ConditionAll(
                                new ConditionBiomeFilter(BiomeTags.OCEAN),
                                new ConditionDensityLimit(Entity.DROWNED, 5, 128)
                        ),
                        new ConditionAll(
                                new ConditionBiomeFilter(BiomeTags.RIVER, BiomeTags.DRIPSTONE_CAVES),
                                new ConditionDensityLimit(Entity.DROWNED, 2, 128)
                        )
                ),
                new ConditionDensityLimit(Entity.DROWNED, 4)
        );

    }

}
