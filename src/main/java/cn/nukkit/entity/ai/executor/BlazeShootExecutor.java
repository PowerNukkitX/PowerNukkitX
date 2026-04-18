package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.InternalPlugin;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class BlazeShootExecutor implements EntityControl, IBehaviorExecutor {
    protected MemoryType<? extends Entity> memory;
    protected float speed;
    protected int maxShootDistanceSquared;
    protected boolean clearDataWhenLose;
    protected final int coolDownTick;
    protected final int fireTick;
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
     * @param fireTick          <br>Attack Animation time(tick)
     */
    public BlazeShootExecutor(MemoryType<? extends Entity> memory, float speed, int maxShootDistance, boolean clearDataWhenLose, int coolDownTick, int fireTick) {
        this.memory = memory;
        this.speed = speed;
        this.maxShootDistanceSquared = maxShootDistance * maxShootDistance;
        this.clearDataWhenLose = clearDataWhenLose;
        this.coolDownTick = coolDownTick;
        this.fireTick = fireTick;
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
                startOnFire(entity);
            }
        } else if (tick2 != 0) {
            tick2++;
            if (tick2 > fireTick) {
                for (int i = 0; i < 3; i++) {
                    entity.getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> shootFireball(entity), i * 6);
                }
                entity.getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> stopOnFire(entity), 20);
                tick2 = 0;
                return target.getHealthCurrent() != 0;
            }
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        stopOnFire(entity);
        this.target = null;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        stopOnFire(entity);
        this.target = null;
    }

    protected void shootFireball(EntityLiving entity) {

        Location fireballLocation = entity.getLocation();
        Vector3 directionVector = entity.getDirectionVector().multiply(1 + ThreadLocalRandom.current().nextFloat(0.2f));
        fireballLocation.setY(entity.y + entity.getEyeHeight() + directionVector.getY());
        final NbtMap nbt = NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                fireballLocation.x,
                                fireballLocation.y,
                                fireballLocation.z
                        )
                ).putList("Motion", NbtType.DOUBLE, Arrays.asList(
                                -Math.sin(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI),
                                -Math.sin(entity.pitch / 180 * Math.PI),
                                Math.cos(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI)
                        )
                ).putList("Rotation", NbtType.FLOAT, Arrays.asList(
                                (entity.headYaw > 180 ? 360 : 0) - (float) entity.headYaw,
                                (float) -entity.pitch
                        )
                ).putDouble("damage", 2)
                .build();
        double p = 1;
        double f = Math.min((p * p + p * 2) / 3, 1) * 3;

        Entity projectile = Entity.createEntity(EntityID.SMALL_FIREBALL, entity.level.getChunk(entity.getChunkX(), entity.getChunkZ()), nbt);

        if (projectile == null) {
            return;
        }
        if (projectile instanceof EntitySmallFireball fireball) {
            fireball.shootingEntity = entity;
        }

        ProjectileLaunchEvent projectev = new ProjectileLaunchEvent((EntityProjectile) projectile, entity);
        Server.getInstance().getPluginManager().callEvent(projectev);
        if (projectev.isCancelled()) {
            projectile.kill();
        } else {
            projectile.spawnToAll();
            entity.level.addLevelEvent(entity, LevelEvent.SOUND_BLAZE_FIREBALL);
        }
    }

    private void startOnFire(Entity entity) {
        entity.setDataProperty(ActorDataTypes.TARGET, this.target.getId());
        entity.setDataFlag(ActorFlags.CHARGED, true);
    }

    private void stopOnFire(Entity entity) {
        entity.setDataProperty(ActorDataTypes.TARGET, 0L);
        entity.setDataFlag(ActorFlags.CHARGED, false);
    }
}
