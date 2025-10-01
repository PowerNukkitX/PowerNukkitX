package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.item.EntitySplashPotion;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.InternalPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class PotionThrowExecutor implements EntityControl, IBehaviorExecutor {
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
     *
     * @param memory            <br>Used to read the memory of the attack target
     * @param speed             <br>The speed of movement towards the attacking target
     * @param maxShootDistance  <br>The maximum distance at which it is permissible to shoot, and only at this distance can be fired
     * @param clearDataWhenLose <br>Clear your memory when you lose your target
     * @param coolDownTick      <br>Attack cooldown (tick)
     * @param attackDelay          <br>Attack Animation time(tick)
     */
    public PotionThrowExecutor(MemoryType<? extends Entity> memory, float speed, int maxShootDistance, boolean clearDataWhenLose, int coolDownTick, int attackDelay) {
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
            if (player.isCreative() || player.isSpectator() || !player.isOnline() || !entity.level.getName().equals(player.level.getName())) {
                return false;
            }
        }

        if (!this.target.getPosition().equals(newTarget.getPosition())) {
            target = newTarget;
        }

        if (entity.getMovementSpeed() != speed) entity.setMovementSpeed(speed);
        Location clone = this.target.getLocation();

        if (entity.distanceSquared(target) > maxShootDistanceSquared) {
            setRouteTarget(entity, clone);
        } else {
            setRouteTarget(entity, null);
        }
        setLookTarget(entity, clone);

        if (tick2 == 0 && tick1 > coolDownTick) {
            if (entity.distanceSquared(target) <= maxShootDistanceSquared) {
                this.tick1 = 0;
                this.tick2++;
                startShootSequence(entity);
            }
        } else if (tick2 != 0) {
            tick2++;
            if (tick2 > attackDelay) {
                throwPotion(entity);
                entity.getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> endShootSequence(entity), 20);
                tick2 = 0;
                return target.getHealth() != 0;
            }
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getDefaultSpeed());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        endShootSequence(entity);
        this.target = null;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getDefaultSpeed());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        endShootSequence(entity);
        this.target = null;
    }

    protected void throwPotion(EntityLiving entity) {

        Location potionLocation = entity.getLocation();
        Vector3 directionVector = entity.getDirectionVector();
        potionLocation.setY(entity.y + entity.getEyeHeight() + directionVector.getY());
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(potionLocation.x))
                        .add(new DoubleTag(potionLocation.y))
                        .add(new DoubleTag(potionLocation.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(-Math.sin(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI)))
                        .add(new DoubleTag(-Math.sin(entity.pitch / 180 * Math.PI)))
                        .add(new DoubleTag(Math.cos(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI))))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag((entity.headYaw > 180 ? 360 : 0) - (float) entity.headYaw))
                        .add(new FloatTag((float) -entity.pitch)))
                .putInt("PotionId", getPotionEffect(entity));

        Entity projectile = Entity.createEntity(EntityID.SPLASH_POTION, entity.level.getChunk(entity.getChunkX(), entity.getChunkZ()), nbt);

        if (projectile == null) {
            return;
        }

        if(projectile instanceof EntitySplashPotion fireball) {
            fireball.shootingEntity = entity;
        }

        ProjectileLaunchEvent projectev = new ProjectileLaunchEvent((EntityProjectile) projectile, entity);
        Server.getInstance().getPluginManager().callEvent(projectev);
        if (projectev.isCancelled()) {
            projectile.kill();
        } else {
            projectile.spawnToAll();
        }
    }

    private void startShootSequence(Entity entity) {
        entity.setDataProperty(EntityDataTypes.TARGET_EID, this.target.getId());
    }

    private void endShootSequence(Entity entity) {
        entity.setDataProperty(EntityDataTypes.TARGET_EID, 0L);
    }

    public int getPotionEffect(Entity entity) {
        if(entity instanceof EntityIntelligent intelligent) {
            if(intelligent.getMemoryStorage().notEmpty(memory)) {
                Entity target = intelligent.getMemoryStorage().get(memory);
                double distance = target.distance(entity);
                if(distance > 8 && !target.hasEffect(EffectType.SLOWNESS)) {
                    return 17; //SLOWNESS
                } else if(distance < 3 && !target.hasEffect(EffectType.WEAKNESS) && ThreadLocalRandom.current().nextInt(4) == 0) {
                    return 34; //WEAKNESS
                } else if(target.getHealth() > 8 && !target.hasEffect(EffectType.POISON)) {
                    return 25; //POISON
                }
            }
        }
        return 23; //INSTANT_DAMAGE
    }
}
