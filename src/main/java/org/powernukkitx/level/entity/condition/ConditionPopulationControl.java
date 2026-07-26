package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntitySwimmable;
import org.powernukkitx.entity.mob.EntityMob;
import org.powernukkitx.entity.mob.EntityPillager;
import org.powernukkitx.entity.passive.EntityAnimal;
import org.powernukkitx.level.Level;
import lombok.AllArgsConstructor;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class ConditionPopulationControl extends Condition {

    public Category category;


    public ConditionPopulationControl(Category category) {
        super("minecraft:population_control");
        this.category = category;
    }

    @Override
    public boolean evaluate(Block block) {
        Level level = block.getLevel();
        int chunkX = block.getChunkX();
        int chunkZ = block.getChunkZ();
        boolean surface = block.getY() >= block.getLevel().getHeightMap(block.getFloorX(), block.getFloorZ());
        int entityDensity = 0;
        for(int x = -4; x <= 4; x++) {
            for(int z = -4; z <= 4; z++) {
                for(Entity entity : level.getChunkEntities(chunkX + x, chunkZ + z, false).values()) {
                    if(category.superClass.isAssignableFrom(entity.getClass())) {
                        if(entity.getFloorY() >= SEA_LEVEL - 2 && surface) entityDensity++;
                        if(entity.getFloorY() < SEA_LEVEL - 2 && !surface) entityDensity++;
                    }
                }
            }
        }
        return entityDensity < category.densityCap[surface ? 0 : 1][level.getDimension()];
    }

    @AllArgsConstructor
    public enum Category {

        AMBIENT(Entity.class, new int[][]{
                {0 ,0, 0},
                {2, 0, 2}
        }),
        ANIMAL(EntityAnimal.class, new int[][]{
                {4, 0, 4},
                {0, 4, 0}
        }),
        MONSTER(EntityMob.class, new int[][]{
                {8, 0, 10},
                {8, 16, 8}
        }),
        PILLAGER(EntityPillager.class, new int[][]{
                {8, 0, 8},
                {8, 0, 8}
        }),
        WATER_ANIMAL(EntitySwimmable.class, new int[][]{
                {36, 0, 36},
                {0, 0, 0}
        });

        protected Class<?> superClass;
        protected int[][] densityCap;
    }
}
