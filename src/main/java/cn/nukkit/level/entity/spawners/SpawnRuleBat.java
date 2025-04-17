package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleBat extends SpawnRule {

    public SpawnRuleBat() {
        super(Entity.BAT, 8, 8,
                new ConditionInAir(),
                new ConditionSpawnUnderground(),
                new ConditionBrightnessFilter(0, 4),
                new ConditionHeightFilter(-63, 63),
                new ConditionBiomeFilter(BiomeTags.ANIMAL),
                new ConditionPopulationControl(EntityAnimal.class, new int[]{2, 0, 2})
        );
    }

}
