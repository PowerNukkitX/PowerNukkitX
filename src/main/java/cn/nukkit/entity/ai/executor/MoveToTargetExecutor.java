package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
import cn.nukkit.entity.ai.memory.Vector3Memory;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class MoveToTargetExecutor implements IBehaviorExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<? extends Vector3Memory> memoryClazz;

    public MoveToTargetExecutor(Class<? extends Vector3Memory> memoryClazz) {
        this.memoryClazz = memoryClazz;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (!entity.getBehaviorGroup().getMemoryStorage().contains(memoryClazz)) {
            //目标丢失
            removeRouteTarget(entity);
            return false;
        }
        //获取目标位置（这个clone很重要）
        Vector3 target = entity.getBehaviorGroup().getMemoryStorage().get(memoryClazz).getData().clone();
        //更新寻路target
        setRouteTarget(entity, target);

        entity.setMovementSpeed(0.3f);

        return true;
    }

    protected void setRouteTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.getMemoryStorage().put(new MoveTargetMemory(vector3));
    }

    protected void removeRouteTarget(@NotNull EntityIntelligent entity) {
        entity.getMemoryStorage().remove(MoveTargetMemory.class);
    }
}
