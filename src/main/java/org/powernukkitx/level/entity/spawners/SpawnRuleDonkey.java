package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.tags.BlockTags;

public class SpawnRuleDonkey extends MultiSpawnRule {

    public SpawnRuleDonkey() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        }, new SpawnRuleDonkeyPlains(), new SpawnRuleDonkeyMeadow());
    }

    private static class SpawnRuleDonkeyPlains extends SpawnRule {

        public SpawnRuleDonkeyPlains() {
            super(Entity.DONKEY, 1 ,3, 1,
                    new ConditionBiomeFilter(BiomeTags.PLAINS),
                    new ConditionDensityLimit(Entity.DONKEY, 6)
            );
        }
    }

    private static class SpawnRuleDonkeyMeadow extends SpawnRule {

        public SpawnRuleDonkeyMeadow() {
            super(Entity.DONKEY, 1, 2, 1,
                    new ConditionBiomeFilter(BiomeTags.MEADOW),
                    new ConditionDensityLimit(Entity.DONKEY, 2)
            );
        }
    }

}
