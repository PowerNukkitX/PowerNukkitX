package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleFox extends SpawnRule {

    public SpawnRuleFox() {
        super(Entity.FOX, 2, 4, 8,
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.TAIGA, BiomeTags.GROVE),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        );
    }

}
