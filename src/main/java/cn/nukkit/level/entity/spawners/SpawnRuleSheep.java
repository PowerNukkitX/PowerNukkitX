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

public class SpawnRuleSheep extends MultiSpawnRule {

    public SpawnRuleSheep() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionDensityLimit(Entity.SHEEP, 2, 3)
        }, new SpawnRuleSheepDefault(), new SpawnRuleSheepMeadow());
    }

    private static class SpawnRuleSheepDefault extends SpawnRule {

        public SpawnRuleSheepDefault() {
            super(Entity.SHEEP, 2 ,3,
                    new ConditionBiomeFilter(BiomeTags.ANIMAL)
            );
        }
    }

    private static class SpawnRuleSheepMeadow extends SpawnRule {

        public SpawnRuleSheepMeadow() {
            super(Entity.SHEEP, 2, 4,
                    new ConditionBiomeFilter(BiomeTags.MEADOW, BiomeTags.CHERRY_GROVE));
        }
    }

}
