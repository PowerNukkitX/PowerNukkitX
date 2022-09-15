package cn.nukkit.entity.ai.executor.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.AboutControlExecutor;
import cn.nukkit.entity.ai.memory.NearestFeedingPlayerMemory;
import cn.nukkit.entity.ai.memory.Vector3Memory;

/**
 * 狼看向携带食物的玩家.
 * <p>
 * Wolf looks at the player carrying the food.
 */
@PowerNukkitXOnly
@Since("1.19.21-r5")
public class WolfLookPlayerExecutor extends AboutControlExecutor {
    @Override
    public boolean execute(EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        Vector3Memory<?> vector3Memory = entity.getMemoryStorage().get(NearestFeedingPlayerMemory.class);
        if (vector3Memory.hasData()) {
            setLookTarget(entity, vector3Memory.getData());
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, true);
            return true;
        } else return false;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(NearestFeedingPlayerMemory.class)) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        }
        removeLookTarget(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(NearestFeedingPlayerMemory.class)) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        }
        removeLookTarget(entity);
    }
}
