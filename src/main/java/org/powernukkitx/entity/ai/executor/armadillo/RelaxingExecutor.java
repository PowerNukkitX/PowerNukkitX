package org.powernukkitx.entity.ai.executor.armadillo;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.EntityControl;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.passive.EntityArmadillo;


public class RelaxingExecutor implements EntityControl, IBehaviorExecutor {


    public RelaxingExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        return false;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        removeLookTarget(entity);
        removeRouteTarget(entity);
        if(entity instanceof EntityArmadillo armadillo) {
            armadillo.setRollState(EntityArmadillo.RollState.ROLLED_UP_RELAXING);
        }
    }

}
