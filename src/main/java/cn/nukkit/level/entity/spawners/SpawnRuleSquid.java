package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionAll;
import cn.nukkit.level.entity.condition.ConditionAny;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderwater;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleSquid extends SpawnRule {

    public SpawnRuleSquid() {
        super(Entity.SQUID, 2, 4,
                new ConditionSpawnUnderwater(),
                new ConditionAny(
                        new ConditionAll(
                                new ConditionBiomeFilter(BiomeTags.OCEAN),
                                new ConditionDensityLimit(Entity.SQUID, 4, 128)
                        ),
                        new ConditionAll(
                                new ConditionBiomeFilter(BiomeTags.RIVER),
                                new ConditionDensityLimit(Entity.SQUID, 2, 64)
                        )
                )
                );
    }

}
