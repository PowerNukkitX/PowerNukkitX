package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;

/**
 * 实体看向最近携带食物的玩家<br/>
 * 此执行器与 {@link LookAtTargetExecutor} 最大的一点不同是，它会设置实体的DATA_FLAG_INTERESTED为true
 * <p>
 * Entity looks at the nearest player carrying the food<br/>
 * The biggest difference between this executor and {@link LookAtTargetExecutor} is that it will set the entity's DATA_FLAG_INTERESTED to true
 */


public class LookAtFeedingPlayerExecutor implements EntityControl, IBehaviorExecutor {
    @Override
    public boolean execute(EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        var vector3 = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_FEEDING_PLAYER);
        if (vector3 != null) {
            setLookTarget(entity, vector3);
            entity.setDataFlag(EntityFlag.INTERESTED, true);
            return true;
        } else return false;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(EntityFlag.INTERESTED, false);
        }
        removeLookTarget(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(EntityFlag.INTERESTED, false);
        }
        removeLookTarget(entity);
    }
}
