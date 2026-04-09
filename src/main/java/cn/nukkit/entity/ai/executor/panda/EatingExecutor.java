package cn.nukkit.entity.ai.executor.panda;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Sound;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

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
        entity.setDataFlag(ActorFlags.SITTING);
        entity.setDataFlag(ActorFlags.EATING);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT, 1.05f);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT_PREVIOUS, 1.05f);
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
        entity.setDataFlag(ActorFlags.SITTING, false);
        entity.setDataFlag(ActorFlags.EATING, false);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT, 0);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT_PREVIOUS, 0);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        if(entity instanceof InventoryHolder holder) holder.getInventory().clearAll();
    }
}
