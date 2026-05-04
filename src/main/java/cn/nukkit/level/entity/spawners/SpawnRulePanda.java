package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityPanda;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionNot;
import cn.nukkit.level.entity.condition.ConditionPopulationControl;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;

public class SpawnRulePanda extends MultiSpawnRule {

    public SpawnRulePanda() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.JUNGLE),
                new ConditionPopulationControl(EntityPanda.class, new int[]{4, 0, 4})
        }, new SpawnRulePandaJungle(), new SpawnRulePandaBamboo());
    }

    private static class SpawnRulePandaJungle extends SpawnRule {
        public SpawnRulePandaJungle() {
            super(Entity.PANDA, 1, 2, 10,
                    new ConditionNot(new ConditionBiomeFilter(BiomeTags.BAMBOO)));
        }
    }

    private static class SpawnRulePandaBamboo extends SpawnRule {
        public SpawnRulePandaBamboo() {
            super(Entity.PANDA, 1, 2, 40,
                    new ConditionBiomeFilter(BiomeTags.BAMBOO));
        }
    }

}
