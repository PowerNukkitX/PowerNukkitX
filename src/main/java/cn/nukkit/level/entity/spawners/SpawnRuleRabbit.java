package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
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
        }, new SpawnRuleRabbitTaigaOrSnow(), new SpawnRuleRabbitDesert(), new SpawnRuleRabbitFlowerOrSlopes(), new SpawnRuleRabbitGrove(), new SpawnRuleRabbitMeadow());
    }

    private static class SpawnRuleRabbitTaigaOrSnow extends SpawnRule {

        public SpawnRuleRabbitTaigaOrSnow() {
            super(Entity.RABBIT, 2 ,3, 4,
                    new ConditionAny(
                            new ConditionBiomeFilter(BiomeTags.FROZEN),
                            new ConditionAll(new ConditionBiomeFilter(BiomeTags.TAIGA), new ConditionNot(new ConditionBiomeFilter(BiomeTags.MEGA)))
                    ),
                    new ConditionPopulationControl(EntityRabbit.class, new int[]{3, 0, 0})
            );
        }
    }

    private static class SpawnRuleRabbitDesert extends SpawnRule {

        public SpawnRuleRabbitDesert() {
            super(Entity.RABBIT, 2, 3, 12,
                    new ConditionBiomeFilter(BiomeTags.DESERT),
                    new ConditionPopulationControl(EntityRabbit.class, new int[]{3, 0, 0})
            );
        }
    }

    private static class SpawnRuleRabbitFlowerOrSlopes extends SpawnRule {

        public SpawnRuleRabbitFlowerOrSlopes() {
            super(Entity.RABBIT, 2, 3, 4,
                    new ConditionBiomeFilter(BiomeTags.FLOWER_FOREST, BiomeTags.SNOWY_SLOPES),
                    new ConditionPopulationControl(EntityRabbit.class, new int[]{3, 0, 0})
            );
        }
    }

    private static class SpawnRuleRabbitGrove extends SpawnRule {

        public SpawnRuleRabbitGrove() {
            super(Entity.RABBIT, 2, 3, 8,
                    new ConditionBiomeFilter(BiomeTags.GROVE),
                    new ConditionPopulationControl(EntityRabbit.class, new int[]{3, 0, 0})
            );
        }
    }

    private static class SpawnRuleRabbitMeadow extends SpawnRule {

        public SpawnRuleRabbitMeadow() {
            super(Entity.RABBIT, 2, 6, 2,
                    new ConditionBiomeFilter(BiomeTags.MEADOW, BiomeTags.CHERRY_GROVE),
                    new ConditionPopulationControl(EntityRabbit.class, new int[]{6, 0, 0})
            );
        }
    }

}
