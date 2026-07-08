package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.level.entity.condition.*;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleArmadillo extends MultiSpawnRule {

    public SpawnRuleArmadillo() {
        super(new Condition[]{
                new ConditionBrightnessFilter(7, 15),
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(Block.GRASS_BLOCK,
                                                Block.RED_SAND,
                                                Block.COARSE_DIRT,
                                                Block.BROWN_TERRACOTTA,
                                                Block.HARDENED_CLAY,
                                                Block.ORANGE_TERRACOTTA,
                                                Block.LIGHT_GRAY_TERRACOTTA,
                                                Block.RED_TERRACOTTA,
                                                Block.WHITE_TERRACOTTA,
                                                Block.YELLOW_TERRACOTTA
                ),
                new ConditionBiomeFilter(BiomeTags.SAVANNA, BiomeTags.MESA),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        }, new SpawnRuleArmadilloSavanna(), new SpawnRuleArmadilloMesa());
    }

    private static class SpawnRuleArmadilloSavanna extends SpawnRule {
        public SpawnRuleArmadilloSavanna() {
            super(Entity.ARMADILLO, 2 ,3, 10,
                    new ConditionBiomeFilter(BiomeTags.SAVANNA));
        }
    }

    private static class SpawnRuleArmadilloMesa extends SpawnRule {
        public SpawnRuleArmadilloMesa() {
            super(Entity.ARMADILLO, 1, 2, 6,
                    new ConditionBiomeFilter(BiomeTags.MESA),
                    new ConditionAny(
                            new ConditionAll(
                                    new ConditionBiomeFilter(BiomeTags.PLATEAU),
                                    new ConditionProbability(2, 5)
                            ),
                            new ConditionAll(
                                    new ConditionNot(new ConditionBiomeFilter(BiomeTags.PLATEAU)),
                                    new ConditionProbability(3, 10)
                            )
                    )
            );
        }
    }
}
