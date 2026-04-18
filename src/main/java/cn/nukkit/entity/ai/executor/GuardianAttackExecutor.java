package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Location;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;

public class GuardianAttackExecutor implements EntityControl, IBehaviorExecutor {
    protected MemoryType<? extends Entity> memory;
    protected float speed;
    protected int maxShootDistanceSquared;
    protected boolean clearDataWhenLose;
    protected final int coolDownTick;
    protected final int attackDelay;
    /**
     * <p>
     * Used to specify a specific attack target.
     **/
    protected Entity target;

    private int tick1;//control the coolDownTick
    private int tick2;//control the pullBowTick

    /**
     * @param memory            <br>Used to read the memory of the attack target
     * @param speed             <br>The speed of movement towards the attacking target
     * @param maxShootDistance  <br>The maximum distance at which it is permissible to shoot, and only at this distance can be fired
     * @param clearDataWhenLose <br>Clear your memory when you lose your target
     * @param coolDownTick      <br>Attack cooldown (tick)
     * @param attackDelay       <br>Attack Animation time(tick)
     */
    public GuardianAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxShootDistance, boolean clearDataWhenLose, int coolDownTick, int attackDelay) {
        this.memory = memory;
        this.speed = speed;
        this.maxShootDistanceSquared = maxShootDistance * maxShootDistance;
        this.clearDataWhenLose = clearDataWhenLose;
        this.coolDownTick = coolDownTick;
        this.attackDelay = attackDelay;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (tick2 == 0) {
            tick1++;
        }
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memory)) return false;
        Entity newTarget = entity.getBehaviorGroup().getMemoryStorage().get(memory);
        if (this.target == null) target = newTarget;

        if (!target.isAlive()) return false;
        else if (target instanceof Player player) {
            if (player.isIgnoredByEntities() || !entity.level.getName().equals(player.level.getName())) {
                return false;
            }
        }

        if (!this.target.getPosition().equals(newTarget.getPosition())) {
            target = newTarget;
        }

        if (entity.getMovementSpeed() != speed) entity.setMovementSpeed(speed);
        Location clone = this.target.getLocation();

        setLookTarget(entity, clone);

        if (tick2 == 0 && tick1 > coolDownTick) {
            if (entity.distanceSquared(target) <= maxShootDistanceSquared) {
                this.tick1 = 0;
                this.tick2++;
                startSequence(entity);
            }
        } else if (tick2 != 0) {
            tick2++;
            if (tick2 > attackDelay) {
                if (entity.getDataProperty(ActorDataTypes.TARGET, 0L) == target.getId()) {
                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, ((EntityMob) entity).getDiffHandDamage(Server.getInstance().getDifficulty()));
                    target.attack(event);
                    if (Server.getInstance().getDifficulty() >= 2) {
                        EntityDamageByEntityEvent event2 = new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.MAGIC, 1);
                        target.attack(event2);
                    }
                }
                endSequence(entity);
                tick2 = 0;
                return target.getHealthCurrent() != 0;
            }
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        endSequence(entity);
        this.target = null;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        endSequence(entity);
        this.target = null;
    }

    private void startSequence(Entity entity) {
        entity.setDataProperty(ActorDataTypes.TARGET, this.target.getId());
        entity.level.addLevelSoundEvent(entity, SoundEvent.MOB_WARNING, -1, entity.getIdentifier(), false, false);
    }

    private void endSequence(Entity entity) {
        entity.setDataProperty(ActorDataTypes.TARGET, 0L);
        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(entity.getId());
        pk.setType(ActorEvent.GUARDIAN_ATTACK_SOUND);
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
