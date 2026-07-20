package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleBat extends SpawnRule {

    public SpawnRuleBat() {
        super(Entity.BAT, 8, 8, 10,
                new ConditionInAir(),
                new ConditionSpawnUnderground(),
                new ConditionBrightnessFilter(0, 4),
                new ConditionHeightFilter(-63, 63),
                new ConditionBiomeFilter(BiomeTags.ANIMAL),
                new ConditionPopulationControl(ConditionPopulationControl.Category.AMBIENT)
        );
    }

}
