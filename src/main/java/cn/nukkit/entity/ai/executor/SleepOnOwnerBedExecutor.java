package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityTamable;

/**
 * 使有主人的生物在主人睡觉时睡到主人床上<br/>
 * 只能在实现了接口 {@link EntityTamable} 的实体上使用<br/>
 * 需要保证实体的getOwner()方法返回非空
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public class SleepOnOwnerBedExecutor implements IBehaviorExecutor {
    @Override
    public boolean execute(EntityIntelligent entity) {
        Player owner = ((EntityTamable) entity).getOwner();
        if (entity.distanceSquared(owner) <= 4) {
            setSleeping(entity, true);
        }
        return owner.isSleeping();
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        Player owner = ((EntityTamable) entity).getOwner();
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
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_RESTING, sleeping);
        entity.setDataFlag(Entity.DATA_FLAGS_EXTENDED, Entity.DATA_FLAG_RESTING, sleeping);
    }
}
