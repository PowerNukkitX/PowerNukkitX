package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class LookAtTargetExecutor implements EntityControl, IBehaviorExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected MemoryType<? extends Vector3> memory;
    protected int duration;
    protected int currentTick;

    public LookAtTargetExecutor(MemoryType<? extends Vector3> memory, int duration) {
        this.memory = memory;
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        var vector3Memory = entity.getMemoryStorage().get(memory);
        if (vector3Memory != null) {
            setLookTarget(entity, vector3Memory);
        }
        return currentTick <= duration;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        currentTick = 0;
        entity.setEnablePitch(false);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        currentTick = 0;
        entity.setEnablePitch(false);
    }
}
