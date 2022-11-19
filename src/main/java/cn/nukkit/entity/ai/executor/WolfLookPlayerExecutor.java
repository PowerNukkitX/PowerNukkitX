package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.AboutControlExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

/**
 * 狼看向携带食物的玩家.
 * <p>
 * Wolf looks at the player carrying the food.
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public class WolfLookPlayerExecutor extends AboutControlExecutor {
    @Override
    public boolean execute(EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        var vector3 = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_FEEDING_PLAYER);
        if (vector3 != null) {
            setLookTarget(entity, vector3);
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, true);
            return true;
        } else return false;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        }
        removeLookTarget(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        }
        removeLookTarget(entity);
    }
}
