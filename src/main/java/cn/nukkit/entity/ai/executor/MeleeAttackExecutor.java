package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.EntityMemory;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class MeleeAttackExecutor implements IBehaviorExecutor{

    protected Class<? extends EntityMemory<?>> memoryClazz;
    protected float speed;
    protected float damage;

    public MeleeAttackExecutor(Class<? extends EntityMemory<?>> memoryClazz, float speed, float damage) {
        this.memoryClazz = memoryClazz;
        this.speed = speed;
        this.damage = damage;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memoryClazz)) {
            //目标丢失
            removeRouteTarget(entity);
            removeLookTarget(entity);
            //重置速度
            entity.setMovementSpeed(0.1f);
            return false;
        }
        if (entity.getMovementSpeed() != speed)
            entity.setMovementSpeed(speed);

        //获取目标位置（这个clone很重要）
        Entity target = entity.getBehaviorGroup().getMemoryStorage().get(memoryClazz).getData();
        Vector3 clonedTarget = target.clone();
        //更新寻路target
        setRouteTarget(entity, clonedTarget);
        //更新视线target
        setLookTarget(entity, clonedTarget);

        if (entity.canCollideWith(target)) {
            target.attack(damage);
            playAttackAnimation(entity);
        }
        return true;
    }

    protected void playAttackAnimation(EntityIntelligent entity) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.event = EntityEventPacket.ARM_SWING;
    }

    protected void setRouteTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.setMoveTarget(vector3);
    }

    protected void setLookTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.setLookTarget(vector3);
    }

    protected void removeRouteTarget(@NotNull EntityIntelligent entity) {
        entity.setMoveTarget(null);
    }

    protected void removeLookTarget(@NotNull EntityIntelligent entity) {
        entity.setLookTarget(null);
    }

}
