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

    public MoveToTargetExecutor(Class<? extends Vector3Memory<?>> memoryClazz, float speed) {
        this.memoryClazz = memoryClazz;
        this.speed = speed;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memoryClazz)) {
            //目标丢失
            removeRouteTarget(entity);
            removeLookTarget(entity);
            //重置速度
            entity.setMovementSpeed(0.1f);
            return false;
        }
        //获取目标位置（这个clone很重要）
        Vector3 target = entity.getBehaviorGroup().getMemoryStorage().get(memoryClazz).getData().clone();
        //更新寻路target
        setRouteTarget(entity, target);
        //更新视线target
        setLookTarget(entity, target);

        if (entity.getMovementSpeed() != speed)
            entity.setMovementSpeed(speed);

        return true;
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
