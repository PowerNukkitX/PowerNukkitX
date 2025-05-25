package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.entity.condition.*;
import cn.nukkit.tags.BiomeTags;

public class SpawnRuleGoat extends SpawnRule {

    public SpawnRuleGoat() {
        super(Entity.GOAT, 1, 3,
                new ConditionSpawnOnGround(),
                new ConditionSpawnOnBlockFilter(BlockID.STONE, BlockID.SNOW, BlockID.POWDER_SNOW, BlockID.SNOW_LAYER, BlockID.PACKED_ICE, BlockID.GRAVEL),
                new ConditionBrightnessFilter(7, 15),
                new ConditionBiomeFilter(BiomeTags.SNOWY_SLOPES, BiomeTags.JAGGED_PEAKS, BiomeTags.FROZEN_PEAKS),
                new ConditionPopulationControl(EntityAnimal.class, new int[]{4, 0, 4})
        );
    }

}
