package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.passive.EntityBee;

import static org.powernukkitx.Server.getInstance;

public class BeeAttackExecutor extends MeleeAttackExecutor {

    public BeeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
        super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if(entity instanceof EntityBee bee) {
            if (entity.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET)) {
                if (!entity.isEnablePitch()) entity.setEnablePitch(true);
                Entity entity1 = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
                if (entity1 != null) {
                    this.lookTarget = entity1.clone();
                    if (getInstance().getDifficulty() == 2) {
                        entity1.addEffect(Effect.get(EffectType.POISON).setDuration(200));
                    } else if (getInstance().getDifficulty() == 3) {
                        entity1.addEffect(Effect.get(EffectType.POISON).setDuration(360));
                    }
                }
            }

            if (entity.distanceSquared(entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET)) <= 2.5 && attackTick > coolDown && bee.hasSting()) {
                bee.dieInTicks = 700;
            }
            return super.execute(entity);
        }
        return false;
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
        var bee = (EntityBee) entity;
        bee.setAngry(false);
    }
}
