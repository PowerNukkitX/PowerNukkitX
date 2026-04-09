package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class SnowGolemShootExecutor implements EntityControl, IBehaviorExecutor {
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
    public SnowGolemShootExecutor(MemoryType<? extends Entity> memory, float speed, int maxShootDistance, boolean clearDataWhenLose, int coolDownTick, int fireTick) {
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
            }
        } else if (tick2 != 0) {
            tick2++;
            if (tick2 > fireTick) {
                shootSnowball(entity);
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
        this.target = null;
    }

    protected void shootSnowball(EntityLiving entity) {

        Location snowballLocation = entity.getLocation();
        Vector3 directionVector = entity.getDirectionVector().multiply(1 + ThreadLocalRandom.current().nextFloat(0.2f)).normalize();
        snowballLocation.setY(entity.y + entity.getEyeHeight() + directionVector.getY() + 1.0f);

        final NbtMap nbt = NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                snowballLocation.x,
                                snowballLocation.y,
                                snowballLocation.z
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
                ).build();

        EntitySnowball projectile = new EntitySnowball(entity.level.getChunk(entity.getChunkX(), entity.getChunkZ()), nbt);

        projectile.shootingEntity = entity;
        ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(projectile, entity);
        Server.getInstance().getPluginManager().callEvent(projectev);
        if (projectev.isCancelled()) {
            projectile.kill();
        } else {
            projectile.spawnToAll();
            entity.level.addSound(entity, Sound.MOB_SNOWGOLEM_SHOOT);
        }
    }

}
