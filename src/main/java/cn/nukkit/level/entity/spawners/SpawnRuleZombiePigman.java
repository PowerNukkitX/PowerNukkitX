package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleZombiePigman extends SpawnRule {

    public SpawnRuleZombiePigman() {
        super(Entity.ZOMBIE_PIGMAN, 2, 4,
                new ConditionInAir(),
                new ConditionBrightnessFilter(0, 11),
                new ConditionNot(new ConditionSpawnOnBlockFilter(Block.NETHER_WART_BLOCK, Block.SHROOMLIGHT)),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionBiomeFilter(BiomeTags.SPAWN_ZOMBIFIED_PIGLIN, BiomeTags.SPAWN_FEW_ZOMBIFIED_PIGLINS),
                new ConditionPopulationControl(EntityMob.class, new int[]{8, 16, 8})
        );
    }

}
