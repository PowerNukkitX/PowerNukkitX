package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.mob.EntityWarden;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventGenericPacket;


public class WardenRangedAttackExecutor implements IBehaviorExecutor {
    private static final int ATTACK_COOLDOWN_TICKS = 40;
    private static final float ATTACK_DAMAGE = 10f;
    private static final float ATTACK_MOVEMENT_SPEED = 0.36f;
    private static final float BASE_MOVEMENT_SPEED = 0.3f;
    private static final double HORIZONTAL_KNOCKBACK = 1.0d;
    private static final double VERTICAL_KNOCKBACK = 0.24d;
    private static final double VERTICAL_KNOCKBACK_CAP = 0.5d;
    private static final double MOTION_SCALE = 0.5d;

    protected int chargingTime;
    protected int totalRunningTime;
    protected int currentTick;
    protected boolean running;

    public WardenRangedAttackExecutor(int chargingTime, int totalRunningTime) {
        this.chargingTime = chargingTime;
        this.totalRunningTime = totalRunningTime;
    }

    /**
     * Returns whether the ranged attack behavior can currently run.
     *
     * @param entity behavior owner
     * @return whether the behavior can run
     */
    public boolean canRun(EntityIntelligent entity) {
        return running || ((EntityWarden) entity).canUseSonicBoom();
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET))
            return false;
        if (currentTick == this.chargingTime) {
            var target = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);

            if (!target.isAlive()) return false;

            Vector3 from = entity.add(0, entity.getHeight() / 2);
            Vector3 to = target.add(0, target.getEyeHeight());

            //particle
            sendAttackParticle(entity, from, to);

            //sound
            entity.level.addSound(entity, Sound.MOB_WARDEN_SONIC_BOOM);

            //attack
            EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.SONIC_BOOM, ATTACK_DAMAGE, 0f);
            entity.level.addSound(target, Sound.MOB_WARDEN_ATTACK);
            if (target.attack(ev)) applySonicKnockback(target, from, to);
        }
        if (currentTick > this.totalRunningTime) {
            return false;
        } else {
            var target = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
            //update the look target
            entity.setLookTarget(target.getLocation());
            entity.setMoveTarget(target.getLocation());
            return true;
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.currentTick = 0;
        this.running = false;
        ((EntityWarden) entity).setSonicBoomCooldown(ATTACK_COOLDOWN_TICKS);
        entity.setMovementSpeed(BASE_MOVEMENT_SPEED);
        entity.setDataFlag(ActorFlags.SONIC_BOOM, false);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.running = true;
        entity.setMovementSpeed(ATTACK_MOVEMENT_SPEED);
        entity.setDataFlag(ActorFlags.SONIC_BOOM, true);
        entity.level.addSound(entity, Sound.MOB_WARDEN_SONIC_CHARGE);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.currentTick = 0;
        this.running = false;
        ((EntityWarden) entity).setSonicBoomCooldown(ATTACK_COOLDOWN_TICKS);
        entity.setMovementSpeed(BASE_MOVEMENT_SPEED);
        entity.setDataFlag(ActorFlags.SONIC_BOOM, false);
    }

    protected void applySonicKnockback(Entity target, Vector3 from, Vector3 to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double horizontalLength = Math.sqrt(dx * dx + dz * dz);
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double knockbackResistance = target.getKnockbackResistance();
        if (target instanceof Player player) {
            for (var armor : player.getInventory().getArmorInventory().getContents().values()) {
                if (!armor.isNull()) knockbackResistance += armor.getKnockbackResistance();
            }
        }
        double resistanceMultiplier = Math.max(0d, 1d - knockbackResistance);

        Vector3 motion = target.getMotion();
        double motionX = motion.x * MOTION_SCALE;
        double motionY = motion.y * MOTION_SCALE;
        double motionZ = motion.z * MOTION_SCALE;

        if (horizontalLength > 1.0e-6) {
            motionX += dx / horizontalLength * HORIZONTAL_KNOCKBACK * resistanceMultiplier;
            motionZ += dz / horizontalLength * HORIZONTAL_KNOCKBACK * resistanceMultiplier;
        }
        if (length > 1.0e-6) {
            motionY += dy / length * VERTICAL_KNOCKBACK * resistanceMultiplier;
        }

        target.setMotion(new Vector3(motionX, Math.min(motionY, VERTICAL_KNOCKBACK_CAP), motionZ));
    }

    protected void sendAttackParticle(EntityIntelligent entity, Vector3 from, Vector3 to) {
        var length = from.distance(to);
        var relativeVector = new Vector3(to.x - from.x, to.y - from.y, to.z - from.z);
        for (int i = 1; i <= (length + 4); i++) {
            var pk = new LevelEventGenericPacket();
            pk.setType(LevelEvent.SONIC_EXPLOSION);
            pk.setTag(createVec3fTag(from.add(relativeVector.multiply(i / length)).asVector3f()));
            Server.broadcastPacket(entity.getViewers().values(), pk);
        }
    }

    protected NbtMap createVec3fTag(Vector3f vec3f) {
        return NbtMap.builder()
                .putFloat("x", vec3f.x)
                .putFloat("y", vec3f.y)
                .putFloat("z", vec3f.z)
                .build();
    }
}
