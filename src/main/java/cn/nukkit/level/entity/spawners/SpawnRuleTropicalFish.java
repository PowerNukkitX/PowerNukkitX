package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionAll;
import cn.nukkit.level.entity.condition.ConditionAny;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionHeightFilter;
import cn.nukkit.level.entity.condition.ConditionNot;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderwater;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleTropicalFish extends SpawnRule {

    public SpawnRuleTropicalFish() {
        super(Entity.TROPICALFISH, 3, 5,
                new ConditionSpawnUnderwater(),
                new ConditionDensityLimit(Entity.TROPICALFISH, 20, 128),
                new ConditionBiomeFilter(BiomeTags.OCEAN),
                new ConditionBiomeFilter(BiomeTags.WARM, BiomeTags.LUKEWARM)
        );
    }

}
