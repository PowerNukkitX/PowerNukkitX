package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.Vector3Memory;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class MoveToTargetExecutor implements IBehaviorExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<? extends Vector3Memory<?>> memoryClazz;
    protected float speed;
    protected Vector3 oldTarget;
    protected boolean updateRouteImmediatelyWhenTargetChange;
    protected boolean enableRangeTest = false;
    protected int maxFollowRangeSquared;
    protected int minFollowRangeSquared;

    public MoveToTargetExecutor(Class<? extends Vector3Memory<?>> memoryClazz, float speed) {
        this(memoryClazz,speed,false);
    }

    public MoveToTargetExecutor(Class<? extends Vector3Memory<?>> memoryClazz, float speed, boolean updateRouteImmediatelyWhenTargetChange) {
        this(memoryClazz, speed, updateRouteImmediatelyWhenTargetChange, -1, -1);
    }

    public MoveToTargetExecutor(Class<? extends Vector3Memory<?>> memoryClazz, float speed, boolean updateRouteImmediatelyWhenTargetChange, int maxFollowRange, int minFollowRange){
        this.memoryClazz = memoryClazz;
        this.speed = speed;
        this.updateRouteImmediatelyWhenTargetChange = updateRouteImmediatelyWhenTargetChange;
        if (maxFollowRange >= 0 && minFollowRange >= 0) {
            this.maxFollowRangeSquared = maxFollowRange * maxFollowRange;
            this.minFollowRangeSquared = minFollowRange * minFollowRange;
            enableRangeTest = true;
        }
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memoryClazz)) {
            return false;
        }
        //获取目标位置（这个clone很重要）
        Vector3 target = entity.getBehaviorGroup().getMemoryStorage().get(memoryClazz).getData().clone();

        if (enableRangeTest) {
            var distanceSquared = target.distanceSquared(entity);
            if (distanceSquared > maxFollowRangeSquared || distanceSquared < minFollowRangeSquared) {
                return false;
            }
        }

        //更新寻路target
        setRouteTarget(entity, target);
        //更新视线target
        setLookTarget(entity, target);

        if (updateRouteImmediatelyWhenTargetChange) {
            var floor = target.floor();

            if (oldTarget == null || oldTarget.equals(floor))
                entity.getBehaviorGroup().setForceUpdateRoute(true);

            oldTarget = floor;
        }

        if (entity.getMovementSpeed() != speed)
            entity.setMovementSpeed(speed);

        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        //目标丢失
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(0.1f);
        entity.setEnablePitch(false);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        //目标丢失
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(0.1f);
        entity.setEnablePitch(false);
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
