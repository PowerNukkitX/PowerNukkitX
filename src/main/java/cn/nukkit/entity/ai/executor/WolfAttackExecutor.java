package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.passive.EntityWolf;

/**
 * The wolf performs an attack with a wolf animation.
 */
// TODO: wolves should use regular melee attack, the setAngry state on BDS is handled by the sensor and not by the behavior.
public class WolfAttackExecutor extends MeleeAttackExecutor {

    /**
     * Melee attack executor
     *
     * @param memory            Memory
     * @param speed             Movement speed towards attack target
     * @param maxSenseRange     Maximum attack target range
     * @param clearDataWhenLose Clear memory when losing target
     * @param coolDown          Attack cooldown time (unit tick)
     */
    public WolfAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
        super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;
        wolf.setAngry(true);
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
        entity.getLevel().getScheduler().scheduleDelayedTask(null, () -> wolf.setAngry(false), 5);
    }
}
