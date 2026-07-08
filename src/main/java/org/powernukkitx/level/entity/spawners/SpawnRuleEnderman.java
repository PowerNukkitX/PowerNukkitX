package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionDifficultyFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleEnderman extends MultiSpawnRule {

    public SpawnRuleEnderman() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionBiomeFilter(BiomeTags.THE_END),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
        },
                new SpawnRuleEndermanOverworld(),
                new SpawnRuleEndermanNether(),
                new SpawnRuleEndermanEnd()
        );
    }

    private static class SpawnRuleEndermanOverworld extends SpawnRule {

        public SpawnRuleEndermanOverworld() {
            super(Entity.ENDERMAN, 1, 2, 10,
                    new ConditionDifficultyFilter(),
                    new ConditionBrightnessFilter(0, 7),
                    new ConditionBiomeFilter(BiomeTags.MONSTER),
                    new ConditionDensityLimit(Entity.ENDERMAN, 2, 64));
        }
    }

    private static class SpawnRuleEndermanNether extends SpawnRule {

        public SpawnRuleEndermanNether() {
            super(Entity.ENDERMAN, 1, 1, 6,
                    new ConditionDifficultyFilter(),
                    new ConditionBiomeFilter(BiomeTags.SPAWN_ENDERMEN),
                    new ConditionDensityLimit(Entity.ENDERMAN, 1));
        }
    }

    private static class SpawnRuleEndermanEnd extends SpawnRule {

        public SpawnRuleEndermanEnd() {
            super(Entity.ENDERMAN, 4, 4, 10,
                    new ConditionBiomeFilter(BiomeTags.THE_END));
        }
    }

}
