package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntitySkeleton;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

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
                new ConditionAny(
                        new ConditionAll(
                                new ConditionSpawnUnderground(),
                                new ConditionPopulationControl(EntitySkeleton.class, new int[]{8, 16, 8})
                        ),
                        new ConditionAll(
                                new ConditionSpawnOnSurface(),
                                new ConditionPopulationControl(EntitySkeleton.class, new int[]{8, 0, 10})
                        )
                )
        );
    }

}
