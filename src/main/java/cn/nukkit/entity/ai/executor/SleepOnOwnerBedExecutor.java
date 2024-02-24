package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityOwnable;
import cn.nukkit.entity.data.EntityFlag;

/**
 * 使有主人的生物在主人睡觉时睡到主人床上<br/>
 * 只能在实现了接口 {@link EntityOwnable} 的实体上使用<br/>
 * 需要保证实体的getOwner()方法返回非空
 */
public class SleepOnOwnerBedExecutor implements IBehaviorExecutor {
    @Override
    public boolean execute(EntityIntelligent entity) {
        Player owner = ((EntityOwnable) entity).getOwner();
        if (entity.distanceSquared(owner) <= 4) {
            setSleeping(entity, true);
        }
        return owner.isSleeping();
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        Player owner = ((EntityOwnable) entity).getOwner();
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
