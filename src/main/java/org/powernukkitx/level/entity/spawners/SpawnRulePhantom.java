package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionDifficultyFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnSurface;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRulePhantom extends SpawnRule {

    public SpawnRulePhantom() {
        super(Entity.PHANTOM, 100,
                new ConditionDifficultyFilter(),
                new ConditionNoSleep(),
                new ConditionInAir(),
                new ConditionSpawnOnSurface(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionBiomeFilter(BiomeTags.MONSTER),
                new ConditionDensityLimit(Entity.PHANTOM, 1, 128),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
        );
    }

    private static class ConditionNoSleep extends Condition {

        public ConditionNoSleep() {
            super("pnx:phantom_no_sleep");
        }

        @Override
        public boolean evaluate(Block block) {
            return block.getLevel().noSleepNights >= 3;
        }
    }

}
