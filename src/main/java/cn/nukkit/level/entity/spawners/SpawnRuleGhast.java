package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionDifficultyFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleGhast extends SpawnRule {

    public SpawnRuleGhast() {
        super(Entity.GHAST,
                new ConditionInAir(),
                new GhastCondition(),
                new ConditionDifficultyFilter(),
                new ConditionBiomeFilter(BiomeTags.SPAWN_GHAST),
                new ConditionSpawnOnBlockFilter(Block.AIR),
                new ConditionDensityLimit(Entity.GHAST, 2, 128));
    }

    private static class GhastCondition extends ConditionInAir {
        @Override
        public boolean evaluate(Block block) {
            return block.up(3).isAir();
        }
    }

}
