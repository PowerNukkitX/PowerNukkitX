package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionDifficultyFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleMagmaCube extends MultiSpawnRule {

    public SpawnRuleMagmaCube() {
        super(new Condition[]{
                new ConditionDifficultyFilter(),
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
        }, new SpawnRuleMagmaCubeLess(), new SpawnRuleMagmaCubeMany());
    }

    private static class SpawnRuleMagmaCubeLess extends SpawnRule {

        public SpawnRuleMagmaCubeLess() {
            super(Entity.MAGMA_CUBE, 1 ,4,
                    new ConditionBiomeFilter(BiomeTags.SPAWN_MAGMA_CUBES),
                    new ConditionDensityLimit(Entity.MAGMA_CUBE, 4));
        }
    }

    private static class SpawnRuleMagmaCubeMany extends SpawnRule {

        public SpawnRuleMagmaCubeMany() {
            super(Entity.MAGMA_CUBE, 2, 5,
                    new ConditionBiomeFilter(BiomeTags.SPAWN_MANY_MAGMA_CUBES),
                    new ConditionDensityLimit(Entity.MAGMA_CUBE, 5));
        }
    }

}
