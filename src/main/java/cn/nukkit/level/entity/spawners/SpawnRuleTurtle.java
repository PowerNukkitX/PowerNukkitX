package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionHeightFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionPopulationControl;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;

public class SpawnRuleTurtle extends SpawnRule {

    public SpawnRuleTurtle() {
        super(Entity.TURTLE, 2, 6,
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.SAND).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionHeightFilter(60, 67),
                new ConditionBiomeFilter(BiomeTags.BEACH),
                new ConditionBiomeFilter(BiomeTags.WARM),
                new ConditionPopulationControl(EntityAnimal.class, new int[]{4, 0, 4})
        );
    }

}
