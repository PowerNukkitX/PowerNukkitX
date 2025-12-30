package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;

public class ConditionPopulationControl extends Condition {

    public int[] densityCap;
    private final Class<? extends EntityIntelligent> checkForClass;

    public ConditionPopulationControl(Class<? extends EntityIntelligent> checkForClass, int[] densityCap) {
        super("minecraft:population_control");
        this.checkForClass = checkForClass;
        if(densityCap.length != DimensionEnum.values().length) throw new IllegalArgumentException("Density Cap array does not match dimensions");
        this.densityCap = densityCap;
    }

    @Override
    public boolean evaluate(Block block) {
        Level level = block.getLevel();
        int chunkX = block.getChunkX();
        int chunkZ = block.getChunkZ();
        int entityDensity = 0;
        boolean surface = block.getY() >= block.getLevel().getHeightMap(block.getFloorX(), block.getFloorZ());
        for(int x = -5; x <=5; x++) {
            for(int z = -5; z <=5; z++) {
                for(Entity entity : level.getChunkEntities(chunkX + x, chunkZ + z, false).values()) {
                    if(checkForClass.isAssignableFrom(entity.getClass())) {
                        if(entity.getLevel().getHeightMap(block.getFloorX(), block.getFloorZ()) == block.getFloorY()-1) {
                            if(surface) entityDensity++;
                        } else if(!surface) entityDensity++;
                    }
                }
            }
        }
        return entityDensity < densityCap[level.getDimension()];
    }
}
