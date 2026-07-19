package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleFrog extends SpawnRule {

    public SpawnRuleFrog() {
        super(Entity.FROG, 2, 5, 10,
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionBiomeFilter(BiomeTags.SWAMP, BiomeTags.MANGROVE_SWAMP),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        );
    }

}
