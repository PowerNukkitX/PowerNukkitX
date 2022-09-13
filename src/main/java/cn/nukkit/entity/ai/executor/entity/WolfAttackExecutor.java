package cn.nukkit.entity.ai.executor.entity;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.EntityMemory;
import cn.nukkit.entity.passive.EntityWolf;

public class WolfAttackExecutor extends MeleeAttackExecutor {

    public WolfAttackExecutor(Class<? extends EntityMemory<?>> memoryClazz, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
        super(memoryClazz, speed, maxSenseRange, clearDataWhenLose, coolDown);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;
        wolf.setAngry(true);
        return super.execute(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;
        wolf.setAngry(false);
        super.onStop(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;
        wolf.setAngry(false);
        super.onInterrupt(entity);
    }
}
