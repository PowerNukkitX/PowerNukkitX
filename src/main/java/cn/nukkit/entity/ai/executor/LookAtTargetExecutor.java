package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.math.Vector3;


public class LookAtTargetExecutor implements EntityControl, IBehaviorExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected MemoryType<? extends Vector3> memory;
    protected int duration;
    protected int currentTick;
    /**
     * @deprecated 
     */
    

    public LookAtTargetExecutor(MemoryType<? extends Vector3> memory, int duration) {
        this.memory = memory;
        this.duration = duration;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        var $1 = entity.getMemoryStorage().get(memory);
        if (vector3Memory != null) {
            setLookTarget(entity, vector3Memory);
        }
        return currentTick <= duration;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onInterrupt(EntityIntelligent entity) {
        currentTick = 0;
        entity.setEnablePitch(false);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onStop(EntityIntelligent entity) {
        currentTick = 0;
        entity.setEnablePitch(false);
    }
}
