package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionAll;
import cn.nukkit.level.entity.condition.ConditionAny;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionHeightFilter;
import cn.nukkit.level.entity.condition.ConditionNot;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderwater;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleSalmon extends SpawnRule {

    public SpawnRuleSalmon() {
        super(Entity.SALMON, 4, 7,
                new ConditionSpawnUnderwater(),
                new ConditionDensityLimit(Entity.SALMON, 20, 128),
                new ConditionAny(
                        new ConditionAll(
                                new ConditionHeightFilter(0, 64),
                                new ConditionBiomeFilter(BiomeTags.OCEAN),
                                new ConditionNot(new ConditionBiomeFilter(BiomeTags.WARM))
                        ),
                        new ConditionAll(
                                new ConditionHeightFilter(50, 64),
                                new ConditionBiomeFilter(BiomeTags.RIVER)
                        )
                )
                );
    }

}
