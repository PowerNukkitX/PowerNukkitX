package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleSkeleton extends SpawnRule {

    public SpawnRuleSkeleton() {
        super(Entity.SKELETON, 1, 2,
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
                new ConditionDensityLimit(Entity.SKELETON, 2));
    }

}
