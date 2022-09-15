package cn.nukkit.entity.ai.executor.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.EntityMemory;
import cn.nukkit.entity.ai.memory.NearestFeedingPlayerMemory;
import cn.nukkit.entity.ai.memory.Vector3Memory;
import cn.nukkit.entity.passive.EntityWolf;

/**
 * 狼执行攻击，会带有狼的动画，以及攻击过程中狼还会看向携带食物的玩家.
 * <p>
 * The wolf performs an attack with a wolf animation, as well as during the attack the wolf will also look at the player carrying food.
 */
@PowerNukkitXOnly
@Since("1.19.21-r5")
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
        if ((target != null && !target.isAlive()) || (target != null && target.equals(entity))) return false;

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
        stop(entity);
        super.onStop(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
        super.onInterrupt(entity);
    }

    private void stop(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;
        entity.getServer().getScheduler().scheduleDelayedTask(null, () -> wolf.setAngry(false), 5);

        if (entity.getMemoryStorage().isEmpty(NearestFeedingPlayerMemory.class)) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        }

        if (clearDataWhenLose && minorMemoryTarget != null) {
            entity.getBehaviorGroup().getMemoryStorage().clear(minorMemoryTarget);
        }
    }
}
