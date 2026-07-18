package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionDifficultyFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleGhast extends SpawnRule {

    public SpawnRuleGhast() {
        super(Entity.GHAST, 40,
                new ConditionInAir(),
                new GhastCondition(),
                new ConditionDifficultyFilter(),
                new ConditionBiomeFilter(BiomeTags.SPAWN_GHAST),
                new ConditionSpawnOnBlockFilter(Block.AIR),
                new ConditionDensityLimit(Entity.GHAST, 2, 128),
                new ConditionPopulationControl(ConditionPopulationControl.Category.MONSTER)
        );
    }

    private static class GhastCondition extends ConditionInAir {
        @Override
        public boolean evaluate(Block block) {
            return block.up(3).isAir();
        }
    }

}
