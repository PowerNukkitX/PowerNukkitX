package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionAll;
import org.powernukkitx.level.entity.condition.ConditionAny;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderwater;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleSquid extends SpawnRule {

    public SpawnRuleSquid() {
        super(Entity.SQUID, 2, 4, 8,
                new ConditionSpawnUnderwater(),
                new ConditionAny(
                        new ConditionAll(
                                new ConditionBiomeFilter(BiomeTags.OCEAN),
                                new ConditionDensityLimit(Entity.SQUID, 4, 128)
                        ),
                        new ConditionAll(
                                new ConditionBiomeFilter(BiomeTags.RIVER),
                                new ConditionDensityLimit(Entity.SQUID, 2, 64)
                        )
                ),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        );
    }

}
