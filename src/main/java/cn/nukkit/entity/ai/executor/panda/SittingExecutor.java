package cn.nukkit.entity.ai.executor.panda;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.utils.Utils;

public class SittingExecutor implements EntityControl, IBehaviorExecutor {

    //Values represent ticks
    private final int MIN_SITTING;

    private int ticks = 0;

    public SittingExecutor(int minSitting) {
        MIN_SITTING = minSitting;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        return ticks-- >= 0;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        ticks = Utils.rand(MIN_SITTING, MIN_SITTING+10) * 20;
        entity.setDataFlag(EntityFlag.SITTING);
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
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT, 0);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT_PREVIOUS, 0);
        entity.setMovementSpeed(entity.getDefaultSpeed());
    }
}
