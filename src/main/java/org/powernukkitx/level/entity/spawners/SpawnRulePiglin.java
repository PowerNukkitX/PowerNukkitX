package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionNot;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRulePiglin extends MultiSpawnRule {

    public SpawnRulePiglin() {
        super(new Condition[]{
                new ConditionBiomeFilter(BiomeTags.SPAWN_PIGLIN, BiomeTags.SPAWN_FEW_PIGLINS),
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionNot(new ConditionSpawnOnBlockFilter(Block.NETHER_WART_BLOCK, Block.SHROOMLIGHT)),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
        }, new SpawnRulePiglinLess(), new SpawnRulePiglinFew());
    }

    private static class SpawnRulePiglinLess extends SpawnRule {

        public SpawnRulePiglinLess() {
            super(Entity.PIGLIN, 2 ,4, 5,
                    new ConditionBiomeFilter(BiomeTags.SPAWN_PIGLIN));
        }
    }

    private static class SpawnRulePiglinFew extends SpawnRule {

        public SpawnRulePiglinFew() {
            super(Entity.PIGLIN, 4, 4, 15,
                    new ConditionBiomeFilter(BiomeTags.SPAWN_FEW_PIGLINS));
        }
    }

}
