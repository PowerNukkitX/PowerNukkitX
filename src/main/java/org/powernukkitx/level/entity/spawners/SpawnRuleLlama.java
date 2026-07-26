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

public class SpawnRuleLlama extends MultiSpawnRule {

    public SpawnRuleLlama() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        }, new SpawnRuleLlamaExtremeHills(), new SpawnRuleLlamaSavanna());
    }

    private static class SpawnRuleLlamaExtremeHills extends SpawnRule {

        public SpawnRuleLlamaExtremeHills() {
            super(Entity.LLAMA, 4 ,6, 5,
                    new ConditionBiomeFilter(BiomeTags.EXTREME_HILLS),
                    new ConditionDensityLimit(Entity.LLAMA, 6, 96));
        }
    }

    private static class SpawnRuleLlamaSavanna extends SpawnRule {

        public SpawnRuleLlamaSavanna() {
            super(Entity.LLAMA, 4, 4, 8,
                    new ConditionBiomeFilter(BiomeTags.SAVANNA),
                    new ConditionDensityLimit(Entity.LLAMA, 4, 96));
        }
    }

}
