package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.LookTargetMemory;
import cn.nukkit.entity.ai.memory.Vector3Memory;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class LookAtTargetExecutor implements IBehaviorExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<? extends Vector3Memory> memoryClazz;

    public LookAtTargetExecutor(Class<? extends Vector3Memory> memoryClazz) {
        this.memoryClazz = memoryClazz;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        Vector3Memory vector3Memory = entity.getMemoryStorage().get(memoryClazz);
        if (vector3Memory != null){
            Vector3 vector3 = vector3Memory.getData();
            setLookTarget(entity,vector3);
        }
        return false;
    }

    protected void setLookTarget(@NotNull EntityIntelligent entity, Vector3 vector3){
        entity.getMemoryStorage().put(new LookTargetMemory(vector3));
    }
}
