package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionPopulationControl;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderground;
import cn.nukkit.level.entity.condition.ConditionSpawnUnderwater;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleAxolotl extends SpawnRule {

    public SpawnRuleAxolotl() {
        super(Entity.AXOLOTL, 4, 6,
                new ConditionSpawnUnderground(),
                new ConditionSpawnUnderwater(),
                //new ConditionDisallowSpawnInBubble(), //Will never happen since spawning on clay is required
                new ConditionSpawnOnBlockFilter(BlockID.CLAY),
                new ConditionBiomeFilter(BiomeTags.LUSH_CAVES),
                new ConditionPopulationControl(EntityAnimal.class, new int[]{4, 0, 4}),
                new ConditionDensityLimit(Entity.AXOLOTL,5));
    }

}
