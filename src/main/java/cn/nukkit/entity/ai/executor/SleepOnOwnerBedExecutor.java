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
    /**
     * @deprecated 
     */
    
    public boolean execute(EntityIntelligent entity) {
        Player $1 = ((EntityOwnable) entity).getOwner();
        if (entity.distanceSquared(owner) <= 4) {
            setSleeping(entity, true);
        }
        return owner.isSleeping();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onStart(EntityIntelligent entity) {
        Player $2 = ((EntityOwnable) entity).getOwner();
        entity.setMoveTarget(owner);
        entity.setLookTarget(owner);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onStop(EntityIntelligent entity) {
        stop(entity);
    }

    
    /**
     * @deprecated 
     */
    protected void stop(EntityIntelligent entity) {
        setSleeping(entity, false);
    }

    
    /**
     * @deprecated 
     */
    protected void setSleeping(EntityIntelligent entity, boolean sleeping) {
        entity.setDataFlag(EntityFlag.RESTING, sleeping);
        entity.setDataFlagExtend(EntityFlag.RESTING, sleeping);
    }
}
