package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleSkeleton extends SpawnRule {

    public SpawnRuleSkeleton() {
        super(Entity.SKELETON, 1, 2, 80,
                new ConditionInAir(),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionNot(new ConditionSpawnOnBlockFilter(Block.NETHER_WART_BLOCK, Block.SHROOMLIGHT)),
                new ConditionBrightnessFilter(0, 7),
                new ConditionAny(
                        new ConditionAll(
                                new ConditionBiomeFilter(BiomeTags.MONSTER),
                                new ConditionNot(new ConditionBiomeFilter(BiomeTags.FROZEN))
                        ),
                        new ConditionBiomeFilter(BiomeTags.SOULSAND_VALLEY)
                ),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
        );
    }

}
