package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.passive.EntityAnimal;


public class AnimalGrowExecutor implements IBehaviorExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (entity instanceof EntityAnimal animal) {
            animal.setBaby(false);
        }
        return false;
    }
}
