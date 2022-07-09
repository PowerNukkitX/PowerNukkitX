package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.PlayerMemory;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TestBaseMoveExecutor extends BaseMoveExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        Vector3 player = (Vector3) entity.getBehaviorGroup().getMemory().get(PlayerMemory.class).getData();
        if (player == null)
            return false;
        lookAt(entity,player);
        return false;
    }
}
