package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.entity.passive.EntityRabbit;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.Utils;

public class SpawnRuleRabbit extends MultiSpawnRule {

    public SpawnRuleRabbit() {
        super(new Condition[]{
                new ConditionInAir(),
                new ConditionSpawnOnBlockFilter(Utils.concatArray(BlockTags.getBlockSet(BlockTags.GRASS).toArray(String[]::new),
                        BlockTags.getBlockSet(BlockTags.SAND).toArray(String[]::new),
                        new String[] {BlockID.SNOW, BlockID.SNOW_LAYER})),
                new ConditionSpawnOnGround(),
        }, new SpawnRuleRabbitTaiga(), new SpawnRuleRabbitMeadow());
    }

    private static class SpawnRuleRabbitTaiga extends SpawnRule {

        public SpawnRuleRabbitTaiga() {
            super(Entity.RABBIT, 2 ,3,
                    new ConditionAny(
                            new ConditionBiomeFilter(BiomeTags.FLOWER_FOREST, BiomeTags.GROVE, BiomeTags.SNOWY_SLOPES, BiomeTags.DESERT),
                            new ConditionAll(
                                    new ConditionBiomeFilter(BiomeTags.TAIGA),
                                    new ConditionNot(new ConditionBiomeFilter(BiomeTags.MEGA))
                            )
                    ),
                    new ConditionPopulationControl(EntityAnimal.class, new int[]{3, 0, 0})
            );
        }
    }

    private static class SpawnRuleRabbitMeadow extends SpawnRule {

        public SpawnRuleRabbitMeadow() {
            super(Entity.RABBIT, 2, 6,
                    new ConditionBiomeFilter(BiomeTags.MEADOW, BiomeTags.CHERRY_GROVE),
                    new ConditionPopulationControl(EntityAnimal.class, new int[]{6, 0, 0})
            );
        }
    }

}
