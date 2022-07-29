package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.entity.passive.EntityChicken;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class AnimalGrowExecutor implements IBehaviorExecutor{

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (entity instanceof EntityAnimal animal){
            animal.setBaby(false);
        }
        return false;
    }
}
