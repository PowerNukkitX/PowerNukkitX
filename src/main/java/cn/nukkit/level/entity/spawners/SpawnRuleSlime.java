package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleSlime extends SpawnRule {

    public SpawnRuleSlime() {
        super(Entity.SLIME,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBiomeFilter(BiomeTags.SWAMP, BiomeTags.MANGROVE_SWAMP),
                new ConditionAny(
                        new ConditionAll(
                                new ConditionSpawnUnderground(),
                                new ConditionPopulationControl(EntityMob.class, new int[]{8, 16, 8})
                        ),
                        new ConditionAll(
                                new ConditionSpawnOnSurface(),
                                new ConditionPopulationControl(EntityMob.class, new int[]{8, 0, 10})
                        )
                )
        );
    }

}
