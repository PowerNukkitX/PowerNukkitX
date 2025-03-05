package cn.nukkit.entity.ai.executor.villager;

import cn.nukkit.block.BlockWoodenDoor;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;


public class DoorExecutor implements EntityControl, IBehaviorExecutor {


    public DoorExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        return entity.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_DOOR) && entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_DOOR).asVector3f().equals(entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK_2).asVector3f());
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        if(entity.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_BLOCK_2)) {
            if(entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK_2) instanceof BlockWoodenDoor door) {
                if(!door.isAir() && !door.isOpen()) door.toggle(null);
                entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_DOOR, door);
            }
        }
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if(entity.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_DOOR)) {
            BlockWoodenDoor door = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_DOOR);
            if(door.getLevelBlock() instanceof BlockWoodenDoor) //Can fail when the door gets broken
                door.toggle(null);
            entity.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_DOOR);
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }
}
