package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionHeightFilter;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderwater;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRulePufferfish extends SpawnRule {

    public SpawnRulePufferfish() {
        super(Entity.PUFFERFISH, 3, 5, 25,
                new ConditionSpawnUnderwater(),
                new ConditionHeightFilter(0, 64),
                new ConditionDensityLimit(Entity.PUFFERFISH, 20, 128),
                new ConditionBiomeFilter(BiomeTags.OCEAN, BiomeTags.WARM),
                new ConditionPopulationControl(ConditionPopulationControl.Category.WATER_ANIMAL)
        );
    }

}
