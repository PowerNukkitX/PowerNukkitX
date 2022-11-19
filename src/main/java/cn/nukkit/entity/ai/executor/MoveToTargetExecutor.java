package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class MoveToTargetExecutor extends AboutControlExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected MemoryType<? extends Vector3> memory;
    protected float speed;
    protected Vector3 oldTarget;
    protected boolean updateRouteImmediatelyWhenTargetChange;
    protected boolean enableRangeTest = false;
    protected float maxFollowRangeSquared;
    protected float minFollowRangeSquared;
    protected boolean clearDataWhenLose;

    public MoveToTargetExecutor(MemoryType<? extends Vector3> memory, float speed) {
        this(memory, speed, false);
    }

    public MoveToTargetExecutor(MemoryType<? extends Vector3> memory, float speed, boolean updateRouteImmediatelyWhenTargetChange) {
        this(memory, speed, updateRouteImmediatelyWhenTargetChange, -1, -1);
    }

    public MoveToTargetExecutor(MemoryType<? extends Vector3> memory, float speed, boolean updateRouteImmediatelyWhenTargetChange, float maxFollowRange, float minFollowRange) {
        this(memory, speed, updateRouteImmediatelyWhenTargetChange, maxFollowRange, minFollowRange, false);
    }

    public MoveToTargetExecutor(MemoryType<? extends Vector3> memory, float speed, boolean updateRouteImmediatelyWhenTargetChange, float maxFollowRange, float minFollowRange, boolean clearDataWhenLose) {
        this.memory = memory;
        this.speed = speed;
        this.updateRouteImmediatelyWhenTargetChange = updateRouteImmediatelyWhenTargetChange;
        if (maxFollowRange >= 0 && minFollowRange >= 0) {
            this.maxFollowRangeSquared = maxFollowRange * maxFollowRange;
            this.minFollowRangeSquared = minFollowRange * minFollowRange;
            enableRangeTest = true;
        }
        this.clearDataWhenLose = clearDataWhenLose;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memory)) {
            return false;
        }
        //获取目标位置（这个clone很重要）
        Vector3 target = entity.getBehaviorGroup().getMemoryStorage().get(memory).clone();

        if (target instanceof Position position && !position.level.getName().equals(entity.level.getName()))
            return false;

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
        if (clearDataWhenLose)
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        //目标丢失
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(0.1f);
        entity.setEnablePitch(false);
        if (clearDataWhenLose)
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
    }
}
