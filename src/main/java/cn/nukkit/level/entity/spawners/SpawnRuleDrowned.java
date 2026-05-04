package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityDrowned;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleDrowned extends MultiSpawnRule {

    public SpawnRuleDrowned() {
        super(new Condition[]{
                new ConditionDifficultyFilter(),
                new ConditionSpawnUnderwater(),
                new ConditionBrightnessFilter(0, 7),
                new ConditionPopulationControl(EntityDrowned.class, new int[]{8, 0, 10})
        }, new SpawnRuleDrownedOcean(), new SpawnRuleDrownedRiver(), new SpawnRuleDrownedDripstoneCaves());
    }

    private static class SpawnRuleDrownedOcean extends SpawnRule {
        public SpawnRuleDrownedOcean() {
            super(Entity.DROWNED, 2, 4, 100,
                    new ConditionSpawnOnGround(true),
                    new ConditionBiomeFilter(BiomeTags.OCEAN),
                    new ConditionDensityLimit(Entity.DROWNED, 5, 128));
        }
    }

    private static class SpawnRuleDrownedRiver extends SpawnRule {
        public SpawnRuleDrownedRiver() {
            super(Entity.DROWNED, 1, 1, 5,
                    new ConditionSpawnOnGround(true),
                    new ConditionBiomeFilter(BiomeTags.RIVER),
                    new ConditionDensityLimit(Entity.DROWNED, 2, 128));
        }
    }

    private static class SpawnRuleDrownedDripstoneCaves extends SpawnRule {
        public SpawnRuleDrownedDripstoneCaves() {
            super(Entity.DROWNED, 2, 4, 95,
                    new ConditionSpawnUnderground(),
                    new ConditionBiomeFilter(BiomeTags.DRIPSTONE_CAVES),
                    new ConditionDensityLimit(Entity.DROWNED, 5, 128));
        }
    }

}
