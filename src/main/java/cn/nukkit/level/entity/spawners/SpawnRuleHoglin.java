package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionDifficultyFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionNot;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderground;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleHoglin extends SpawnRule {

    public SpawnRuleHoglin() {
        super(Entity.HOGLIN, 4, 4,
                new ConditionSpawnOnGround(),
                new ConditionInAir(),
                new ConditionSpawnUnderground(),
                new ConditionDifficultyFilter(),
                new ConditionNot(new ConditionSpawnOnBlockFilter(BlockID.NETHER_WART_BLOCK, BlockID.SHROOMLIGHT)),
                new ConditionBiomeFilter(BiomeTags.CRIMSON_FOREST),
                new ConditionDensityLimit(Entity.HOGLIN, 4, 64));
    }

}
