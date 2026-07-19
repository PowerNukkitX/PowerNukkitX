package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCreature;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomTimeRangeEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.WardenEmergingAnimationExecutor;
import org.powernukkitx.entity.ai.executor.WardenMeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.WardenRangedAttackExecutor;
import org.powernukkitx.entity.ai.executor.WardenSniffExecutor;
import org.powernukkitx.entity.ai.executor.WardenViolentAnimationExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.RouteUnreachableTimeSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationListener;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityWarden extends EntityMob implements EntityWalkable, VibrationListener {

    protected int lastDetectTime = getLevel().getTick();
    protected int lastCollideTime = getLevel().getTick();
    protected boolean waitForVibration = false;

    private static final int DARKNESS_DURATION_TICKS = 260;
    private static final int DARKNESS_RANGE = 20;

    public EntityWarden(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return WARDEN;
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior((entity) -> {
                            // Refresh and play sound
                            if (this.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET))
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_ANGRY);
                            else if (this.getMemoryStorage().notEmpty(CoreMemoryTypes.WARDEN_ANGER_VALUE))
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_AGITATED);
                            else
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_IDLE);
                            return false;
                        }, (entity) -> true, 1, 1, 20),
                        new Behavior((entity) -> {
                            // Refresh Anger Value
                            var angerValueMap = this.getMemoryStorage().get(CoreMemoryTypes.WARDEN_ANGER_VALUE);
                            var iterator = angerValueMap.entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<Entity, Integer> next = iterator.next();
                                if (!entity.level.getName().equals(this.level.getName()) || !isValidAngerEntity(next.getKey())) {
                                    iterator.remove();
                                    var attackTarget = this.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
                                    if (attackTarget != null && attackTarget.equals(next.getKey()))
                                        this.getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
                                    continue;
                                }
                                var newAnger = next.getValue() - 1;
                                if (newAnger == 0) iterator.remove();
                                else next.setValue(newAnger);
                            }
                            return false;
                        }, (entity) -> true, 1, 1, 20),
                        new Behavior((entity) -> {
                            // Apply Darkness to nearby players
                            for (var player : entity.level.getPlayers().values()) {
                                if (!player.isIgnoredByEntities() && entity.distanceSquared(player) <= DARKNESS_RANGE * DARKNESS_RANGE) {
                                    player.addEffect(Effect.get(EffectType.DARKNESS).setDuration(DARKNESS_DURATION_TICKS));
                                }
                            }
                            return false;
                        }, (entity) -> true, 1, 1, 120),
                        new Behavior((entity) -> {
                            // Calculate Heartbeat Interval
                            this.setDataProperty(ActorDataTypes.HEARTBEAT_INTERVAL_TICKS, this.calHeartBeatDelay());
                            return false;
                        }, (entity) -> true, 1, 1, 20)),
                Set.of(
                        new Behavior(new WardenEmergingAnimationExecutor(20 * 6), entity -> entity.getAge() < 20 * 6, 6),
                        new Behavior(
                                new WardenViolentAnimationExecutor((int) (4.2 * 20)), all(
                                (entity) -> entity.getMemoryStorage().compareDataTo(CoreMemoryTypes.IS_ATTACK_TARGET_CHANGED, true),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ATTACK_TARGET)), 5
                        ),
                        new Behavior(
                                new WardenRangedAttackExecutor((int) (1.7 * 20), (int) (3.0 * 20)),
                                (entity) -> this.getMemoryStorage().get(CoreMemoryTypes.ROUTE_UNREACHABLE_TIME) > 20 //1s
                                        && this.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET)
                                        && isInRangedAttackRange(this.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET))
                                , 4, 1, 20
                        ),
                        new Behavior(
                                new WardenMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET,
                                        switch (this.getServer().getDifficulty()) {
                                            case 1 -> 16;
                                            case 2 -> 30;
                                            case 3 -> 45;
                                            default -> 0;
                                        }, 0.7f),
                                entity -> {
                                    if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)) {
                                        return false;
                                    } else {
                                        Entity e = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
                                        if (e instanceof Player player) {
                                            return !player.isIgnoredByEntities();
                                        }
                                        return true;
                                    }
                                }, 3, 1
                        ),
                        new Behavior(new WardenSniffExecutor((int) (4.2 * 20), 35), new RandomTimeRangeEvaluator(5 * 20, 10 * 20), 2),
                        new Behavior(new FlatRandomRoamExecutor(0.1f, 12, 100, true, -1, true, 10), (entity -> true), 1)
                ),
                Set.of(new RouteUnreachableTimeSensor(CoreMemoryTypes.ROUTE_UNREACHABLE_TIME)),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }


    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public float getFloatingHeight() {
        return 0.8f;
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(500);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.3f);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setDataProperty(ActorDataTypes.HEARTBEAT_INTERVAL_TICKS, 40);
        this.setDataProperty(ActorDataTypes.HEARTBEAT_SOUND_EVENT, SoundEvent.HEARTBEAT);
        this.setAmbientSoundEvent(Sound.MOB_WARDEN_IDLE);
        this.setAmbientSoundInterval(8.0f);
        this.setAmbientSoundIntervalRange(16.0f);
        this.level.getVibrationManager().addListener(this);
        this.diffHandDamage = new float[]{16, 30, 45};
    }

    @Override
    public String getOriginalName() {
        return "Warden";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("warden", "monster", "mob");
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public Vector3 getListenerVector() {
        return this.getVector3();
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        if (getLevel().getTick() - this.lastDetectTime >= 40 && !waitForVibration && !(event.initiator() instanceof EntityWarden)) {
            this.waitForVibration = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        this.waitForVibration = false;
        this.lastDetectTime = getLevel().getTick();
        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(this.getId());
        pk.setType(ActorEvent.VIBRATION_DETECTED);
        Server.broadcastPacket(this.getViewers().values(), pk);

        //handle anger value
        var initiator = event.initiator();
        if (initiator instanceof Entity entity) {
            if (isValidAngerEntity(entity)) {
                var addition = entity instanceof EntityProjectile ? 10 : 35;
                addEntityAngerValue((Entity) initiator, addition);
            }
        }

        if (this.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET))
            this.level.addSound(this, Sound.MOB_WARDEN_LISTENING_ANGRY);
        else this.level.addSound(this, Sound.MOB_WARDEN_LISTENING);
    }

    @Override
    public double getListenRange() {
        return 16;
    }

    @Override
    public void close() {
        super.close();
        this.level.getVibrationManager().removeListener(this);
    }

    @Override
    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        //anti-kb
    }

    @Override
    protected boolean onCollide(int currentTick, List<Entity> collidingEntities) {
        if (getLevel().getTick() - this.lastCollideTime > 20) {
            for (Entity collidingEntity : collidingEntities) {
                if (isValidAngerEntity(collidingEntity))
                    addEntityAngerValue(collidingEntity, 35);
            }
            this.lastCollideTime = getLevel().getTick();
        }
        return super.onCollide(currentTick, collidingEntities);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        var cause = source.getCause();
        if (cause == EntityDamageEvent.DamageCause.LAVA
                || cause == EntityDamageEvent.DamageCause.HOT_FLOOR
                || cause == EntityDamageEvent.DamageCause.FIRE
                || cause == EntityDamageEvent.DamageCause.FIRE_TICK
                || cause == EntityDamageEvent.DamageCause.DROWNING)
            return false;
        if (source instanceof EntityDamageByEntityEvent damageByEntity && isValidAngerEntity(damageByEntity.getDamager())) {
            var damager = damageByEntity.getDamager();
            var realDamager = damager instanceof EntityProjectile projectile ? projectile.shootingEntity : damager;
            addEntityAngerValue(realDamager, 100);
        }
        return super.attack(source);
    }

    public void addEntityAngerValue(Entity entity, int addition) {
        var angerValueMap = this.getMemoryStorage().get(CoreMemoryTypes.WARDEN_ANGER_VALUE);
        var attackTarget = this.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
        var origin = angerValueMap.getOrDefault(entity, 0);
        int added = NukkitMath.clamp(origin + addition, 0, 150);
        if (added == 0) angerValueMap.remove(entity);
        else if (added >= 80) {
            added += 20;
            added = NukkitMath.clamp(added, 0, 150);
            angerValueMap.put(entity, added);
            boolean changed = attackTarget == null ||
                    (entity instanceof Player && !(attackTarget instanceof Player));
            if (changed) {
                this.getMemoryStorage().put(CoreMemoryTypes.IS_ATTACK_TARGET_CHANGED, true);
                this.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity);
            }
        } else angerValueMap.put(entity, added);
    }

    public boolean isValidAngerEntity(Entity entity) {
        return isValidAngerEntity(entity, false);
    }

    public boolean isValidAngerEntity(Entity entity, boolean sniff) {
        if (entity.isClosed()) return false;
        if (entity.getHealthCurrent() <= 0) return false;
        if (!(sniff ? isInSniffRange(entity) : isInAngerRange(entity))) return false;
        if (!(entity instanceof EntityCreature)) return false;
        if (entity instanceof Player player && (!player.isSurvival() && !player.isAdventure())) return false;
        return !(entity instanceof EntityWarden);
    }

    public boolean isInSniffRange(Entity entity) {
        double deltaX = this.x - entity.x;
        double deltaZ = this.z - entity.z;
        var distanceXZSqrt = deltaX * deltaX + deltaZ * deltaZ;
        var deltaY = Math.abs(this.y - entity.y);
        return distanceXZSqrt <= 36
                && deltaY <= 400;
    }

    public boolean isInRangedAttackRange(Entity entity) {
        double deltaX = this.x - entity.x;
        double deltaZ = this.z - entity.z;
        var distanceXZSqrt = deltaX * deltaX + deltaZ * deltaZ;
        var deltaY = Math.abs(this.y - entity.y);
        return distanceXZSqrt <= 225
                && deltaY <= 400;
    }

    public boolean isInAngerRange(Entity entity) {
        var distanceSqrt = this.distanceSquared(entity);
        return distanceSqrt <= 625;
    }

    public int calHeartBeatDelay() {
        var target = this.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
        if (target == null) return 40;
        var anger = this.getMemoryStorage().get(CoreMemoryTypes.WARDEN_ANGER_VALUE).getOrDefault(target, 0);
        return (int) (40 - NukkitMath.clamp((anger / 80f), 0, 1) * 30f);
    }

    @Override
    public void setOnFire(int seconds) {
        //against fire
    }
}
