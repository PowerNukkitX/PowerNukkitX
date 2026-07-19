package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.tags.BlockTags;

public class SpawnRuleMooshroom extends SpawnRule {

    public SpawnRuleMooshroom() {
        super(Entity.MOOSHROOM, 4, 8, 8,
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.GRASS),
                new ConditionBrightnessFilter(9, 15),
                new ConditionBiomeFilter(BiomeTags.MOOSHROOM_ISLAND),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        );
    }

}
