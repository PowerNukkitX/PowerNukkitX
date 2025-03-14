package cn.nukkit.entity.ai.executor.panda;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

public class RollExecutor implements EntityControl, IBehaviorExecutor {

    //Values represent ticks
    private int ticks = 0;

    @Override
    public boolean execute(EntityIntelligent entity) {
        return ticks-- >= 0;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        ticks = 40;
        entity.setDataFlag(EntityFlag.ROLLING);
        entity.setMovementSpeed(0.3f);
        Vector3 target = entity.getDirectionVector().add(entity.x, entity.y, entity.z).multiply(10);
        setLookTarget(entity, target);
        setRouteTarget(entity, target);
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
        entity.setDataFlag(EntityFlag.ROLLING, false);
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        removeLookTarget(entity);
        removeRouteTarget(entity);
    }
}
