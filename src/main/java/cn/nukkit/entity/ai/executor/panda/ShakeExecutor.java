package cn.nukkit.entity.ai.executor.panda;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.utils.Utils;

public class ShakeExecutor implements EntityControl, IBehaviorExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        return true;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.setDataFlag(EntityFlag.SITTING);
        entity.setDataFlag(EntityFlag.SCARED);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT, 2f);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT_PREVIOUS, 2f);
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
        entity.setDataFlag(EntityFlag.SCARED, false);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT, 0);
        entity.setDataProperty(EntityDataTypes.SITTING_AMOUNT_PREVIOUS, 0);
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
    }
}
