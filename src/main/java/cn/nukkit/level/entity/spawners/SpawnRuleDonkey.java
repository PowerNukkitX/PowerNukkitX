package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;

public class SpawnRuleDonkey extends MultiSpawnRule {

    public SpawnRuleDonkey() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15)
        }, new SpawnRuleDonkeyPlains(), new SpawnRuleDonkeyMeadow());
    }

    private static class SpawnRuleDonkeyPlains extends SpawnRule {

        public SpawnRuleDonkeyPlains() {
            super(Entity.DONKEY, 2 ,6,
                    new ConditionBiomeFilter(BiomeTags.PLAINS),
                    new ConditionDensityLimit(Entity.DONKEY, 6)
            );
        }
    }

    private static class SpawnRuleDonkeyMeadow extends SpawnRule {

        public SpawnRuleDonkeyMeadow() {
            super(Entity.DONKEY, 1, 2,
                    new ConditionBiomeFilter(BiomeTags.MEADOW),
                    new ConditionDensityLimit(Entity.DONKEY, 2)
            );
        }
    }

}
