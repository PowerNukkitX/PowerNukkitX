package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionHeightFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.tags.BlockTags;

public class SpawnRuleTurtle extends SpawnRule {

    public SpawnRuleTurtle() {
        super(Entity.TURTLE, 2, 6, 8,
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.SAND).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionHeightFilter(60, 67),
                new ConditionBiomeFilter(BiomeTags.BEACH),
                new ConditionBiomeFilter(BiomeTags.WARM),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        );
    }

}
