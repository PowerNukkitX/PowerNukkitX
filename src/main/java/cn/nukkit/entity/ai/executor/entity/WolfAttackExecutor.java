package cn.nukkit.entity.ai.executor.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.EntityMemory;
import cn.nukkit.entity.ai.memory.NearestFeedingPlayerMemory;
import cn.nukkit.entity.ai.memory.Vector3Memory;
import cn.nukkit.entity.passive.EntityWolf;

public class WolfAttackExecutor extends MeleeAttackExecutor {
    protected Class<? extends EntityMemory<?>> minorMemoryTarget;

    public WolfAttackExecutor(Class<? extends EntityMemory<?>> mainMemoryTarget, Class<? extends EntityMemory<?>> minorMemoryTarget, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
        super(mainMemoryTarget, speed, maxSenseRange, clearDataWhenLose, coolDown);
        this.minorMemoryTarget = minorMemoryTarget;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;

        target = entity.getBehaviorGroup().getMemoryStorage().get(memoryClazz).getData();
        if (target != null && !target.isAlive()) return false;

        wolf.setAngry(true);

        if (minorMemoryTarget != null) {
            if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memoryClazz)) {
                target = entity.getBehaviorGroup().getMemoryStorage().get(minorMemoryTarget).getData();
            }
        }

        if (entity.getMemoryStorage().notEmpty(NearestFeedingPlayerMemory.class)) {
            if (!entity.isEnablePitch()) entity.setEnablePitch(true);
            Vector3Memory<?> vector3Memory = entity.getMemoryStorage().get(NearestFeedingPlayerMemory.class);
            if (vector3Memory.hasData()) {
                this.lookTarget = vector3Memory.getData();
                entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, true);
            }
        }
        return super.execute(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;
        entity.getServer().getScheduler().scheduleDelayedTask(null, () -> wolf.setAngry(false), 10);
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        if (clearDataWhenLose && minorMemoryTarget != null) {
            entity.getBehaviorGroup().getMemoryStorage().clear(minorMemoryTarget);
        }
        super.onStop(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;
        entity.getServer().getScheduler().scheduleDelayedTask(null, () -> wolf.setAngry(false), 10);
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        if (clearDataWhenLose && minorMemoryTarget != null) {
            entity.getBehaviorGroup().getMemoryStorage().clear(minorMemoryTarget);
        }
        super.onInterrupt(entity);
    }
}
