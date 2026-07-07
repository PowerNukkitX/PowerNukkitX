package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.tags.BlockTags;

public class SpawnRuleSheep extends MultiSpawnRule {

    public SpawnRuleSheep() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL),
        }, new SpawnRuleSheepDefault(), new SpawnRuleSheepMeadow());
    }

    private static class SpawnRuleSheepDefault extends SpawnRule {

        public SpawnRuleSheepDefault() {
            super(Entity.SHEEP, 2 ,3, 12,
                    new ConditionBiomeFilter(BiomeTags.ANIMAL)
            );
        }
    }

    private static class SpawnRuleSheepMeadow extends SpawnRule {

        public SpawnRuleSheepMeadow() {
            super(Entity.SHEEP, 2, 4, 2,
                    new ConditionBiomeFilter(BiomeTags.MEADOW, BiomeTags.CHERRY_GROVE));
        }
    }

}
