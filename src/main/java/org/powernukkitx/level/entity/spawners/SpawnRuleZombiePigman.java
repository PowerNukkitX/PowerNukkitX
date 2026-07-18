package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleZombiePigman extends MultiSpawnRule {

    public SpawnRuleZombiePigman() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionBrightnessFilter(0, 11),
                new ConditionNot(new ConditionSpawnOnBlockFilter(Block.NETHER_WART_BLOCK, Block.SHROOMLIGHT)),
                new ConditionDifficultyFilter(),
                new ConditionSpawnOnGround(),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
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
