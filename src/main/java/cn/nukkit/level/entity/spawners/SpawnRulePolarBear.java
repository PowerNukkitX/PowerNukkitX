package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityPolarBear;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionNot;
import cn.nukkit.level.entity.condition.ConditionPopulationControl;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;

public class SpawnRulePolarBear extends MultiSpawnRule {

    public SpawnRulePolarBear() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.FROZEN),
                new ConditionPopulationControl(EntityPolarBear.class, new int[]{4, 0, 4})
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
