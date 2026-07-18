package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionHeightFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionNot;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderwater;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleDolphin extends SpawnRule {

    public SpawnRuleDolphin() {
        super(Entity.DOLPHIN, 3, 5, 7,
                new ConditionInAir(),
                new ConditionSpawnUnderwater(),
                new ConditionHeightFilter(0, 64),
                new ConditionDensityLimit(Entity.DOLPHIN, 5, 128),
                new ConditionBiomeFilter(BiomeTags.OCEAN),
                new ConditionNot(new ConditionBiomeFilter(BiomeTags.FROZEN)),
                new ConditionPopulationControl(ConditionPopulationControl.Category.WATER_ANIMAL)
        );
    }

}
