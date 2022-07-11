package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
import cn.nukkit.entity.ai.memory.NeedUpdateTargetMemory;
import cn.nukkit.math.Vector3;
import lombok.Getter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class WalkToTargetExecutor implements IBehaviorExecutor{

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<?> memoryClazz;

    protected Vector3 oldPos;

    public WalkToTargetExecutor(Class<?> memoryClazz){
        this.memoryClazz = memoryClazz;
    }

    /**
     * 因为生物AI是全异步的，所以说我们需要判很多的空:(
     */
    @Override
    public boolean execute(EntityIntelligent entity) {
        //获取目标位置
        Vector3 target = (Vector3) entity.getBehaviorGroup().getMemory().get(memoryClazz).getData();
        if (target == null){
            //玩家下线
            oldPos = null;
            return false;
        }
        //检查是否需要init寻路target
        if (needInitTarget(entity)){
            setTargetAndUpdateRoute(entity,target);
        }

        //检查目标是否发生坐标变化，或者是第一次需要初始化路径
        boolean needUpdateTarget = (oldPos == null || oldPos.x != target.x || oldPos.y != target.y || oldPos.z != target.z);
        if(needUpdateTarget){
            //若变化，就通知需要更新路线
            setTargetAndUpdateRoute(entity,target);
        }

        entity.setMovementSpeed(0.25f);
        oldPos = target;
        //我们并不一定需要下次继续运行，所以说返回false即可
        return false;
    }

    protected boolean needInitTarget(EntityIntelligent entity){
        return !entity.getMemoryStorage().contains(MoveTargetMemory.class) || entity.getMemoryStorage().get(MoveTargetMemory.class).getData() == null;
    }

    protected void setTargetAndUpdateRoute(EntityIntelligent entity, Vector3 vector3){
        entity.getMemoryStorage().put(new MoveTargetMemory(vector3));
        entity.getMemoryStorage().put(new NeedUpdateTargetMemory(true));
    }
}
