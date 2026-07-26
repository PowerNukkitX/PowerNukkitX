package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionDifficultyFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionNot;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderground;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleHoglin extends SpawnRule {

    public SpawnRuleHoglin() {
        super(Entity.HOGLIN, 4, 4, 20,
                new ConditionSpawnOnGround(),
                new ConditionInAir(),
                new ConditionSpawnUnderground(),
                new ConditionDifficultyFilter(),
                new ConditionNot(new ConditionSpawnOnBlockFilter(BlockID.NETHER_WART_BLOCK, BlockID.SHROOMLIGHT)),
                new ConditionBiomeFilter(BiomeTags.CRIMSON_FOREST),
                new ConditionDensityLimit(Entity.HOGLIN, 4, 64),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        );
    }

}
