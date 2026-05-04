package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityZombiePigman;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleZombiePigman extends MultiSpawnRule {

    public SpawnRuleZombiePigman() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionBrightnessFilter(0, 11),
                new ConditionNot(new ConditionSpawnOnBlockFilter(Block.NETHER_WART_BLOCK, Block.SHROOMLIGHT)),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionPopulationControl(EntityZombiePigman.class, new int[]{8, 16, 8})
        }, new SpawnRuleZombiePigmanMany(), new SpawnRuleZombiePigmanFew());
    }

    private static class SpawnRuleZombiePigmanMany extends SpawnRule {
        public SpawnRuleZombiePigmanMany() {
            super(Entity.ZOMBIE_PIGMAN, 2, 4, 100,
                    new ConditionBiomeFilter(BiomeTags.SPAWN_ZOMBIFIED_PIGLIN));
        }
    }

    private static class SpawnRuleZombiePigmanFew extends SpawnRule {
        public SpawnRuleZombiePigmanFew() {
            super(Entity.ZOMBIE_PIGMAN, 2, 4, 1,
                    new ConditionBiomeFilter(BiomeTags.SPAWN_FEW_ZOMBIFIED_PIGLINS));
        }
    }

}
