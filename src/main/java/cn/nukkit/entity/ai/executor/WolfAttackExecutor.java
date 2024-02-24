package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.passive.EntityWolf;

/**
 * 狼执行攻击，会带有狼的动画，以及攻击过程中狼还会看向携带食物的玩家.
 * <p>
 * The wolf performs an attack with a wolf animation, as well as during the attack the wolf will also look at the player carrying food.
 */


public class WolfAttackExecutor extends MeleeAttackExecutor {

    /**
     * 近战攻击执行器
     *
     * @param memory            记忆
     * @param speed             移动向攻击目标的速度
     * @param maxSenseRange     最大获取攻击目标范围
     * @param clearDataWhenLose 失去目标时清空记忆
     * @param coolDown          攻击冷却时间(单位tick)
     */

    public WolfAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
        super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        var wolf = (EntityWolf) entity;

//        target = entity.getBehaviorGroup().getMemoryStorage().get(memory);
//        if ((target != null && !target.isAlive()) || (target != null && target.equals(entity))) return false;

        wolf.setAngry(true);

        if (entity.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            if (!entity.isEnablePitch()) entity.setEnablePitch(true);
            var vector3 = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_FEEDING_PLAYER);
            if (vector3 != null) {
                this.lookTarget = vector3.clone();
                entity.setDataFlag(EntityFlag.INTERESTED, true);
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

        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(EntityFlag.INTERESTED, false);
        }
    }
}
