package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderwater;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleTropicalFish extends SpawnRule {

    public SpawnRuleTropicalFish() {
        super(Entity.TROPICALFISH, 3, 5, 75,
                new ConditionSpawnUnderwater(),
                new ConditionDensityLimit(Entity.TROPICALFISH, 20, 128),
                new ConditionBiomeFilter(BiomeTags.OCEAN),
                new ConditionBiomeFilter(BiomeTags.WARM, BiomeTags.LUKEWARM),
                new ConditionPopulationControl(ConditionPopulationControl.Category.WATER_ANIMAL)
        );
    }

}
