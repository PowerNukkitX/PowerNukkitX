package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionDifficultyFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionNot;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.Utils;

public class SpawnRuleWolf extends MultiSpawnRule {

    public SpawnRuleWolf() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionBrightnessFilter(7, 15),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(Utils.concatArray(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new),
                        BlockTags.getBlockSet(BlockTags.DIRT).toArray(String[]::new),
                        new String[] {BlockID.PODZOL})),
                new ConditionDensityLimit(Entity.WOLF, 4)
        }, new SpawnRuleMagmaCubeLess(), new SpawnRuleMagmaCubeMany());
    }

    private static class SpawnRuleMagmaCubeLess extends SpawnRule {

        public SpawnRuleMagmaCubeLess() {
            super(Entity.WOLF, 4 ,4,
                    new ConditionBiomeFilter(BiomeTags.TAIGA));
        }
    }

    private static class SpawnRuleMagmaCubeMany extends SpawnRule {

        public SpawnRuleMagmaCubeMany() {
            super(Entity.WOLF, 2, 4,
                    new ConditionBiomeFilter(BiomeTags.FOREST),
                    new ConditionNot(new ConditionBiomeFilter(BiomeTags.MUTATED, BiomeTags.BIRCH, BiomeTags.ROOFED, BiomeTags.MOUNTAIN))
            );
        }
    }

}
