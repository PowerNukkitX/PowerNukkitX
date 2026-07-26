package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionNot;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.tags.BlockTags;

public class SpawnRulePanda extends MultiSpawnRule {

    public SpawnRulePanda() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.JUNGLE),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
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
