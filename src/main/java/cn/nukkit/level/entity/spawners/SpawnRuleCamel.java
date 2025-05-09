package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleCamel extends SpawnRule {

    public SpawnRuleCamel() {
        super(Entity.CAMEL,
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockID.SAND, BlockID.RED_SAND, BlockID.SANDSTONE),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.DESERT),
                new ConditionDensityLimit(Entity.CAMEL, 1, 128));
    }

}
