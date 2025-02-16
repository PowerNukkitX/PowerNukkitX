package cn.nukkit.entity.ai.executor.armadillo;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.passive.EntityArmadillo;


public class RollUpExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;

    private static final int STAY_TICKS = 60;


    public RollUpExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        if(tick < STAY_TICKS) {
            tick++;
            return true;
        }
        return false;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.tick = 0;
        removeLookTarget(entity);
        removeRouteTarget(entity);
        if(entity instanceof EntityArmadillo armadillo) {
            armadillo.setRollState(EntityArmadillo.RollState.ROLLED_UP);
        }
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if(entity instanceof EntityArmadillo armadillo) {
            armadillo.setRollState(EntityArmadillo.RollState.ROLLED_UP_PEEKING);
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }
}
