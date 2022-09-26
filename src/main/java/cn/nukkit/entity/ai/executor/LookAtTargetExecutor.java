package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.Vector3Memory;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class LookAtTargetExecutor extends AboutControlExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<? extends Vector3Memory<?>> memoryClazz;
    protected int duration;
    protected int currentTick;

    public LookAtTargetExecutor(Class<? extends Vector3Memory<?>> memoryClazz, int duration) {
        this.memoryClazz = memoryClazz;
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        Vector3Memory<?> vector3Memory = entity.getMemoryStorage().get(memoryClazz);
        if (vector3Memory.hasData()) {
            setLookTarget(entity, vector3Memory.getData());
        }
        if (currentTick <= duration) {
            return true;
        } else {
            return false;
        }
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
