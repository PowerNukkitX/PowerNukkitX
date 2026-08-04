package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.entity.condition.ConditionAny;
import org.powernukkitx.level.entity.condition.ConditionBiomeFilter;
import org.powernukkitx.level.entity.condition.ConditionDifficultyFilter;
import org.powernukkitx.level.entity.condition.ConditionPopulationControl;
import org.powernukkitx.level.entity.condition.ConditionSpawnOnSurface;
import org.powernukkitx.level.entity.condition.ConditionSpawnUnderground;
import org.powernukkitx.tags.BiomeTags;

public class SpawnRuleSulfurCube extends SpawnRule {

    public SpawnRuleSulfurCube() {
        super(Entity.SULFUR_CUBE, 2, 4, 150,
                new ConditionAny(new ConditionSpawnOnSurface(), new ConditionSpawnUnderground()),
                new ConditionDifficultyFilter(0, 3),
                new ConditionBiomeFilter(BiomeTags.SULFUR_CAVES),
                new ConditionPopulationControl(ConditionPopulationControl.Category.ANIMAL));
    }
}
