package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.mob.EntityWarden;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.EnumMap;
import java.util.Map;


public class WardenMeleeAttackExecutor implements EntityControl, IBehaviorExecutor {

    protected int attackTick;
    protected MemoryType<? extends Entity> memory;
    protected float damage;
    protected float speed;
    protected int coolDown;
    protected Vector3 oldTarget;

    public WardenMeleeAttackExecutor(MemoryType<? extends Entity> memory, float damage, float speed) {
        this.memory = memory;
        this.damage = damage;
        this.speed = speed;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        attackTick++;
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memory)) return false;
        if (entity.getMovementSpeed() != speed)
            entity.setMovementSpeed(speed);
        //获取目标位置（这个clone很重要）
        Entity target = entity.getBehaviorGroup().getMemoryStorage().get(memory);
        if (!target.isAlive()) return false;
        this.coolDown = calCoolDown(entity, target);
        Vector3 clonedTarget = target.getLocation();
        //更新寻路target
        setRouteTarget(entity, clonedTarget);
        //更新视线target
        setLookTarget(entity, clonedTarget);

        var floor = clonedTarget.floor();

        if (oldTarget == null || !oldTarget.equals(floor))
            entity.getBehaviorGroup().setForceUpdateRoute(true);

        oldTarget = floor;

        if (entity.distanceSquared(target) <= 4 && attackTick > coolDown) {
            Map<EntityDamageEvent.DamageModifier, Float> damages = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
            damages.put(EntityDamageEvent.DamageModifier.BASE, this.damage);

            EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damages, 0.6f, null);

            ev.setBreakShield(true);
            target.attack(ev);
            playAttackAnimation(entity);
            entity.level.addSound(target, Sound.MOB_WARDEN_ATTACK);
            attackTick = 0;
            return target.isUndead();
        }
        return true;
    }

    protected int calCoolDown(EntityIntelligent entity, Entity target) {
        if (entity instanceof EntityWarden warden) {
            var anger = warden.getMemoryStorage().get(CoreMemoryTypes.WARDEN_ANGER_VALUE).getOrDefault(target, 0);
            return anger >= 145 ? 18 : 36;
        } else {
            return 20;
        }
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(0.1f);
        entity.setEnablePitch(false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(0.1f);
        entity.setEnablePitch(false);
    }

    protected void playAttackAnimation(EntityIntelligent entity) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.ARM_SWING;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
