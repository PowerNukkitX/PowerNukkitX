package cn.nukkit.entity.ai.executor.panda;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

public class ShakeExecutor implements EntityControl, IBehaviorExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        return true;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.setDataFlag(ActorFlags.SITTING);
        entity.setDataFlag(ActorFlags.SCARED);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT, 2f);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT_PREVIOUS, 2f);
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
        entity.setDataFlag(ActorFlags.SCARED, false);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT, 0);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT_PREVIOUS, 0);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
    }
}
