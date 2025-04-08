package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleStrider extends SpawnRule {

    public SpawnRuleStrider() {
        super(Entity.STRIDER, 2, 4,
                new ConditionInAir(),
                new ConditionSpawnOnBlockFilter(BlockID.LAVA),
                new ConditionDensityLimit(Entity.STRIDER, 3),
                new ConditionBiomeFilter(BiomeTags.NETHER),
                new ConditionDensityLimit(Entity.STRIDER, 4)
        );
    }

}
