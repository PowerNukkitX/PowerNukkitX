package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.entity.condition.ConditionBiomeIdFilter;
import org.powernukkitx.level.entity.condition.ConditionDensityLimit;
import org.powernukkitx.level.entity.condition.ConditionInAir;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnGround;

public class SpawnRuleSulfurCube extends SpawnRule {

    public SpawnRuleSulfurCube() {
        super(Entity.SULFUR_CUBE, 2, 4, 150,
                new ConditionInAir(),
                new ConditionSpawnOnGround(),
                new ConditionBiomeIdFilter(BiomeID.SULFUR_CAVES),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL),
                new ConditionDensityLimit(Entity.SULFUR_CUBE, 4));
    }
}
