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

public class SpawnRulePolarBear extends MultiSpawnRule {

    public SpawnRulePolarBear() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.FROZEN),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL)
        }, new SpawnRulePolarBearLand(), new SpawnRulePolarBearOcean());
    }

    private static class SpawnRulePolarBearLand extends SpawnRule {
        public SpawnRulePolarBearLand() {
            super(Entity.POLAR_BEAR, 1, 2, 1,
                    new ConditionNot(new ConditionBiomeFilter(BiomeTags.OCEAN)));
        }
    }

    private static class SpawnRulePolarBearOcean extends SpawnRule {
        public SpawnRulePolarBearOcean() {
            super(Entity.POLAR_BEAR, 1, 2, 5,
                    new ConditionSpawnOnBlockFilter(BlockID.ICE),
                    new ConditionBiomeFilter(BiomeTags.OCEAN));
        }
    }

}
