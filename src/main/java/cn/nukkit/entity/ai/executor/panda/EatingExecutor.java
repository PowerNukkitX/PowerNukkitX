package cn.nukkit.entity.ai.executor.panda;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Sound;

public class EatingExecutor implements EntityControl, IBehaviorExecutor {

    //Values represent ticks
    private int ticks = 0;

    @Override
    public boolean execute(EntityIntelligent entity) {
        if(ticks % 10 == 0) entity.getLevel().addSound(entity, Sound.MOB_PANDA_EAT);
        return ticks-- >= 0;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        ticks = 100;
        entity.setDataFlag(EntityFlag.SITTING);
        entity.setDataFlag(EntityFlag.EATING);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT, 1.05f);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT_PREVIOUS, 1.05f);
        entity.setMovementSpeed(0);
        removeRouteTarget(entity);
        removeLookTarget(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    public void stop(EntityIntelligent entity) {
        entity.setDataFlag(EntityFlag.SITTING, false);
        entity.setDataFlag(EntityFlag.EATING, false);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT, 0);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT_PREVIOUS, 0);
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        if(entity instanceof InventoryHolder holder) holder.getInventory().clearAll();
    }
}
