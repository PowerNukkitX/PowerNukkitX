package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SheepBreedingExecutor implements IBehaviorExecutor{
    @Override
    public boolean execute(EntityIntelligent entity) {
        return false;
    }
}
