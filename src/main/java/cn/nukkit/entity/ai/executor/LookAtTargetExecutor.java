package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.Vector3Memory;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class LookAtTargetExecutor implements IBehaviorExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<? extends Vector3Memory> memoryClazz;
    protected int duration;
    protected int currentTick;

    public LookAtTargetExecutor(Class<? extends Vector3Memory> memoryClazz, int duration) {
        this.memoryClazz = memoryClazz;
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        Vector3Memory vector3Memory = entity.getMemoryStorage().get(memoryClazz);
        if (vector3Memory.hasData()){
            Vector3 vector3 = vector3Memory.getData();
            setLookTarget(entity,vector3);
        }
        if (currentTick <= duration) {
            return true;
        } else {
            currentTick = 0;
            return false;
        }
    }

    protected void setLookTarget(@NotNull EntityIntelligent entity, Vector3 vector3){
        entity.setLookTarget(vector3);
    }
}
