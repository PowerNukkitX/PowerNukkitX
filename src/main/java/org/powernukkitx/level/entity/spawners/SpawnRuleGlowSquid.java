package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionHeightFilter;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderground;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderwater;

public class SpawnRuleGlowSquid extends SpawnRule {

    public SpawnRuleGlowSquid() {
        super(Entity.GLOW_SQUID, 2, 4, 10,
                new ConditionSpawnUnderwater(),
                new ConditionSpawnUnderground(),
                new ConditionHeightFilter(-64, 30),
                new ConditionDensityLimit(Entity.GLOW_SQUID, 2, 128),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        );
    }

}
