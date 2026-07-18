package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderground;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderwater;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleAxolotl extends SpawnRule {

    public SpawnRuleAxolotl() {
        super(Entity.AXOLOTL, 4, 6, 10,
                new ConditionSpawnUnderground(),
                new ConditionSpawnUnderwater(),
                //new ConditionDisallowSpawnInBubble(), //Will never happen since spawning on clay is required
                new ConditionSpawnOnBlockFilter(BlockID.CLAY),
                new ConditionBiomeFilter(BiomeTags.LUSH_CAVES),
                new ConditionPopulationControl(ConditionPopulationControl.Category.WATER_ANIMAL),
                new ConditionDensityLimit(Entity.AXOLOTL,5));
    }

}
