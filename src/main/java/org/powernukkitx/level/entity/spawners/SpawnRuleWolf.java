package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionBrightnessFilter;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionNot;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnBlockFilter;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.tags.BlockTags;
import org.powernukkitx.utils.Utils;

public class SpawnRuleWolf extends MultiSpawnRule {

    public SpawnRuleWolf() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionBrightnessFilter(7, 15),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(Utils.concatArray(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new),
                        BlockTags.getBlockSet(BlockTags.DIRT).toArray(String[]::new),
                        new String[] {BlockID.PODZOL})),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        }, new SpawnRuleMagmaCubeLess(), new SpawnRuleMagmaCubeMany());
    }

    private static class SpawnRuleMagmaCubeLess extends SpawnRule {

        public SpawnRuleMagmaCubeLess() {
            super(Entity.WOLF, 4 ,4, 8,
                    new ConditionBiomeFilter(BiomeTags.TAIGA));
        }
    }

    private static class SpawnRuleMagmaCubeMany extends SpawnRule {

        public SpawnRuleMagmaCubeMany() {
            super(Entity.WOLF, 2, 4, 5,
                    new ConditionBiomeFilter(BiomeTags.FOREST),
                    new ConditionNot(new ConditionBiomeFilter(BiomeTags.MUTATED, BiomeTags.BIRCH, BiomeTags.ROOFED, BiomeTags.MOUNTAIN))
            );
        }
    }

}
