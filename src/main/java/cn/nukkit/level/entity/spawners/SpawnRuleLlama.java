package cn.nukkit.level.entity.spawners;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionBiomeFilter;
import cn.nukkit.level.entity.condition.ConditionBrightnessFilter;
import cn.nukkit.level.entity.condition.ConditionDensityLimit;
import cn.nukkit.level.entity.condition.ConditionInAir;
import cn.nukkit.level.entity.condition.ConditionPopulationControl;
import cn.nukkit.level.entity.condition.ConditionSpawnOnBlockFilter;
import cn.nukkit.level.entity.condition.ConditionSpawnOnGround;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;

public class SpawnRuleLlama extends MultiSpawnRule {

    public SpawnRuleLlama() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new)),
                new ConditionBrightnessFilter(7, 15),
                new ConditionPopulationControl(EntityAnimal.class, new int[]{4, 0, 4})
        }, new SpawnRuleLlamaExtremeHills(), new SpawnRuleLlamaSavanna());
    }

    private static class SpawnRuleLlamaExtremeHills extends SpawnRule {

        public SpawnRuleLlamaExtremeHills() {
            super(Entity.LLAMA, 4 ,6,
                    new ConditionBiomeFilter(BiomeTags.EXTREME_HILLS),
                    new ConditionDensityLimit(Entity.LLAMA, 6, 96));
        }
    }

    private static class SpawnRuleLlamaSavanna extends SpawnRule {

        public SpawnRuleLlamaSavanna() {
            super(Entity.LLAMA, 4, 4,
                    new ConditionBiomeFilter(BiomeTags.SAVANNA),
                    new ConditionDensityLimit(Entity.LLAMA, 4, 96));
        }
    }

}
