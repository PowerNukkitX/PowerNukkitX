package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.data.EntityFlag;

/**
 * Allows a creature with an owner to sleep in the owner's bed while the owner sleeps. <p>
 * It is necessary to ensure that the entity's getOwner() method returns a non-null value.
 */
public class SleepOnOwnerBedExecutor implements IBehaviorExecutor {
    @Override
    public boolean execute(EntityIntelligent entity) {
        Player owner = entity.getOwner();
        if (entity.distanceSquared(owner) <= 4) {
            setSleeping(entity, true);
        }
        return owner.isSleeping();
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        Player owner = entity.getOwner();
        entity.setMoveTarget(owner);
        entity.setLookTarget(owner);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stop(entity);
    }

    protected void stop(EntityIntelligent entity) {
        setSleeping(entity, false);
    }

    protected void setSleeping(EntityIntelligent entity, boolean sleeping) {
        entity.setDataFlag(EntityFlag.RESTING, sleeping);
        entity.setDataFlagExtend(EntityFlag.RESTING, sleeping);
    }
}
