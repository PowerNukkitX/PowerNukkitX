package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionHeightFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderground;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderwater;

public class SpawnRuleGlowSquid extends SpawnRule {

    public SpawnRuleGlowSquid() {
        super(Entity.GLOW_SQUID, 2, 4,
                new ConditionSpawnUnderwater(),
                new ConditionSpawnUnderground(),
                new ConditionHeightFilter(-64, 30),
                new ConditionDensityLimit(Entity.GLOW_SQUID, 2, 128));
    }

}
