package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionNot;
import cn.nukkit.level.entity.condition.ConditionPopulationControl;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;

public class SpawnRulePiglin extends MultiSpawnRule {

    public SpawnRulePiglin() {
        super(new Condition[]{
                new ConditionBiomeFilter(BiomeTags.SPAWN_PIGLIN, BiomeTags.SPAWN_FEW_PIGLINS),
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionNot(new ConditionSpawnOnBlockFilter(Block.NETHER_WART_BLOCK, Block.SHROOMLIGHT)),
                new ConditionPopulationControl(EntityMob.class, new int[]{8, 16, 8})
        }, new SpawnRulePiglinLess(), new SpawnRulePiglinFew());
    }

    private static class SpawnRulePiglinLess extends SpawnRule {

        public SpawnRulePiglinLess() {
            super(Entity.PIGLIN, 2 ,4,
                    new ConditionBiomeFilter(BiomeTags.SPAWN_PIGLIN));
        }
    }

    private static class SpawnRulePiglinFew extends SpawnRule {

        public SpawnRulePiglinFew() {
            super(Entity.PIGLIN, 4, 4,
                    new ConditionBiomeFilter(BiomeTags.SPAWN_FEW_PIGLINS));
        }
    }

}
