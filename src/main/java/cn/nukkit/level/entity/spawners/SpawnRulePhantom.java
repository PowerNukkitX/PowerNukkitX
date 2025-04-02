package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionDifficultyFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionSpawnOnSurface;
import cn.nukkit.tags.BiomeTags;

public class SpawnRulePhantom extends SpawnRule {

    public SpawnRulePhantom() {
        super(Entity.PHANTOM,
                new ConditionDifficultyFilter(),
                new ConditionNoSleep(),
                new ConditionInAir(),
                new ConditionSpawnOnSurface(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionBiomeFilter(BiomeTags.MONSTER),
                new ConditionDensityLimit(Entity.PHANTOM, 1, 128));
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
