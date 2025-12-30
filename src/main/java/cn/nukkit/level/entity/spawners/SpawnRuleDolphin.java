package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionHeightFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionNot;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderwater;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleDolphin extends SpawnRule {

    public SpawnRuleDolphin() {
        super(Entity.DOLPHIN, 3, 5,
                new ConditionInAir(),
                new ConditionSpawnUnderwater(),
                new ConditionHeightFilter(0, 64),
                new ConditionDensityLimit(Entity.DOLPHIN, 5, 128),
                new ConditionBiomeFilter(BiomeTags.OCEAN),
                new ConditionNot(new ConditionBiomeFilter(BiomeTags.FROZEN)));
    }

}
