package cn.nukkit.entity.ai.executor.panda;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

public class LayingExecutor implements EntityControl, IBehaviorExecutor {

    //Values represent ticks
    private final int MIN_SITTING;

    private int ticks = 0;

    public LayingExecutor(int minSitting) {
        MIN_SITTING = minSitting;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        return ticks-- >= 0;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        ticks = Utils.rand(MIN_SITTING, MIN_SITTING+10) * 20;
        entity.setDataFlag(ActorFlags.LAYING_DOWN);
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
        entity.setDataFlag(ActorFlags.LAYING_DOWN, false);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT, 0);
        entity.setDataProperty(ActorDataTypes.SITTING_AMOUNT_PREVIOUS, 0);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
    }
}
