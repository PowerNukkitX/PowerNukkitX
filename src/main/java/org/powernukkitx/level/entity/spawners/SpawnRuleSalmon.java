package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionHeightFilter;
import org.powernukkitx.level.entity.condition.ConditionNot;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderwater;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleSalmon extends MultiSpawnRule {

    public SpawnRuleSalmon() {
        super(new Condition[]{
                new ConditionSpawnUnderwater(),
                new ConditionDensityLimit(Entity.SALMON, 20, 128),
                new ConditionPopulationControl(ConditionPopulationControl.Category.WATER_ANIMAL)
        }, new SpawnRuleSalmonOcean(), new SpawnRuleSalmonRiver());
    }

    private static class SpawnRuleSalmonOcean extends SpawnRule {
        public SpawnRuleSalmonOcean() {
            super(Entity.SALMON, 3, 5, 26,
                    new ConditionHeightFilter(0, 64),
                    new ConditionBiomeFilter(BiomeTags.OCEAN),
                    new ConditionNot(new ConditionBiomeFilter(BiomeTags.WARM)));
        }
    }

    private static class SpawnRuleSalmonRiver extends SpawnRule {
        public SpawnRuleSalmonRiver() {
            super(Entity.SALMON, 3, 5, 16,
                    new ConditionHeightFilter(50, 64),
                    new ConditionBiomeFilter(BiomeTags.RIVER));
        }
    }

}
