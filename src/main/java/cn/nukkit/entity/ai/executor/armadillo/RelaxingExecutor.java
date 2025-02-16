package cn.nukkit.entity.ai.executor.armadillo;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.passive.EntityArmadillo;
import cn.nukkit.utils.Utils;


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
