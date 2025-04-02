package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleSlime extends SpawnRule {

    public SpawnRuleSlime() {
        super(Entity.SLIME,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBiomeFilter(BiomeTags.SWAMP, BiomeTags.MANGROVE_SWAMP),
                new ConditionDensityLimit(Entity.POLAR_BEAR, 1)
        );
    }

}
