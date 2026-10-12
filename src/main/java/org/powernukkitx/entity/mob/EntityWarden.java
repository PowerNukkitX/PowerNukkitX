package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCreature;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.WardenDiggingExecutor;
import org.powernukkitx.entity.ai.executor.WardenEmergingAnimationExecutor;
import org.powernukkitx.entity.ai.executor.WardenInvestigateSuspiciousLocationExecutor;
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
import org.powernukkitx.entity.data.warden.WardenNuisance;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationListener;
import org.powernukkitx.level.vibration.VibrationListenerStorage;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class EntityWarden extends EntityMob implements EntityWalkable, VibrationListener {
    public EntityWarden(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.loadNuisances();
        this.loadSonicBoomCooldown();
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return WARDEN;
    }

    private static final int DARKNESS_DURATION_TICKS = 260;
    private static final int DARKNESS_RANGE = 20;
    private static final int VIBRATION_COOLDOWN_TICKS = 40;
    private static final int ANGER_AGITATED_THRESHOLD = 40;
    private static final int ANGER_ANGRY_THRESHOLD = 80;
    private static final int ANGER_BOOST = 20;
    private static final int MAX_ANGER = 150;
    private static final int EMERGING_DURATION_TICKS = 140;
    private static final int DIG_IDLE_TICKS = 1200;
    private static final int DIG_DURATION_TICKS = 110;
    private static final int SONIC_BOOM_TARGET_COOLDOWN_TICKS = 200;
    private static final String TAG_NUISANCES = "Nuisances";
    private static final String TAG_COOLDOWN = "Cooldown";

    private final LinkedHashMap<Long, WardenNuisance> nuisances = new LinkedHashMap<>();
    protected int lastDetectTime = getLevel().getTick();
    protected int lastCollideTime = getLevel().getTick();
    private int lastDisturbanceTick = getLevel().getTick();
    private int sonicBoomCooldownEndTick = Integer.MIN_VALUE;
    private int emergingEndTick;
    private long roarTargetId;
    private Vector3 suspiciousLocation;

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        WardenRangedAttackExecutor sonicBoom = new WardenRangedAttackExecutor((int) (1.7 * 20), (int) (3.0 * 20));
        WardenSniffExecutor sniff = new WardenSniffExecutor((int) (4.16 * 20), 5 * 20, 10 * 20, 35);
        return BehaviorGroup.builder(this)
                .coreBehaviors(
                        new Behavior((entity) -> {
                            // Refresh and play sound
                            int anger = this.getAngerLevel();
                            if (anger >= ANGER_ANGRY_THRESHOLD)
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_ANGRY);
                            else if (anger >= ANGER_AGITATED_THRESHOLD)
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_AGITATED);
                            else
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_IDLE);
                            return false;
                        }, (entity) -> true, 1, 1, 20),
                        new Behavior((entity) -> {
                            this.tickNuisances();
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
                        }, (entity) -> true, 1, 1, 20))
                .behaviors(
                        new Behavior(new WardenEmergingAnimationExecutor(EMERGING_DURATION_TICKS), entity -> this.isEmerging(), 8),
                        new Behavior(new WardenDiggingExecutor(DIG_DURATION_TICKS), entity -> this.canStartDigging(), 7, 1, 1, false),
                        new Behavior(
                                new WardenViolentAnimationExecutor((int) (4.2 * 20)),
                                entity -> this.hasRoarTarget(), 6
                        ),
                        new Behavior(
                                sonicBoom,
                                (entity) -> sonicBoom.canRun(entity)
                                        && this.getMemoryStorage().get(CoreMemoryTypes.ROUTE_UNREACHABLE_TIME) > 20 //1s
                                        && this.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET)
                                        && isInRangedAttackRange(this.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET))
                                , 5, 1, 1
                        ),
                        new Behavior(
                                new WardenMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET,
                                        switch (this.getServer().getDifficulty()) {
                                            case 1 -> 16;
                                            case 2 -> 30;
                                            case 3 -> 45;
                                            default -> 0;
                                        }, 0.36f),
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
                                }, 4, 1
                        ),
                        new Behavior(
                                new WardenInvestigateSuspiciousLocationExecutor(0.21f, 1.5),
                                entity -> this.hasSuspiciousLocation()
                                        && this.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET), 3
                        ),
                        new Behavior(sniff, entity -> sniff.canRun(entity), 2, 1, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.15f, 12, 100, true, -1, true, 10), (entity -> true), 1)
                )
                .sensors(new RouteUnreachableTimeSensor(CoreMemoryTypes.ROUTE_UNREACHABLE_TIME))
                .controllers(new WalkController(), new LookController(true, true), new FluctuateController())
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
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
        this.setAmbientSoundInterval(2.0f);
        this.setAmbientSoundIntervalRange(4.0f);
        if (!this.nbt.containsCompound(VibrationListenerStorage.TAG_VIBRATION_LISTENER)) {
            this.nbt.putCompound(VibrationListenerStorage.TAG_VIBRATION_LISTENER, VibrationListenerStorage.createIdle());
        }
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
    public void saveNBT() {
        super.saveNBT();
        this.saveNuisances();
        this.saveSonicBoomCooldown();
    }

    private void loadSonicBoomCooldown() {
        if (!this.nbt.containsShort(TAG_COOLDOWN)) return;
        this.sonicBoomCooldownEndTick = getLevel().getTick() + this.nbt.getShort(TAG_COOLDOWN);
    }

    private void saveSonicBoomCooldown() {
        if (this.sonicBoomCooldownEndTick == Integer.MIN_VALUE) {
            this.nbt.remove(TAG_COOLDOWN);
            return;
        }

        int cooldown = this.sonicBoomCooldownEndTick - getLevel().getTick();
        if (cooldown >= 0) this.nbt.putShort(TAG_COOLDOWN, cooldown);
        else this.nbt.remove(TAG_COOLDOWN);
    }

    /**
     * Returns whether the Warden's sonic boom cooldown has expired.
     *
     * @return whether sonic boom can be used
     */
    public boolean canUseSonicBoom() {
        return this.sonicBoomCooldownEndTick == Integer.MIN_VALUE || getLevel().getTick() > this.sonicBoomCooldownEndTick;
    }

    /**
     * Starts the Warden's sonic boom cooldown.
     *
     * @param ticks cooldown duration in ticks
     */
    public void setSonicBoomCooldown(int ticks) {
        this.sonicBoomCooldownEndTick = getLevel().getTick() + ticks;
    }

    private void loadNuisances() {
        this.nuisances.clear();
        if (!this.nbt.containsList(TAG_NUISANCES)) return;

        ListTag<CompoundTag> list = this.nbt.getList(TAG_NUISANCES, CompoundTag.class);
        for (CompoundTag tag : list.getAll()) {
            WardenNuisance nuisance = WardenNuisance.fromTag(tag);
            this.nuisances.put(nuisance.actorId(), nuisance);
        }
    }

    private void saveNuisances() {
        List<WardenNuisance> ordered = new ArrayList<>(this.nuisances.values());
        ordered.sort(this::compareNuisances);

        ListTag<CompoundTag> list = new ListTag<>();
        for (WardenNuisance nuisance : ordered) {
            list.add(nuisance.toTag());
        }
        this.nbt.putList(TAG_NUISANCES, list);
    }

    private int compareNuisances(WardenNuisance first, WardenNuisance second) {
        boolean firstAngry = first.anger() >= ANGER_ANGRY_THRESHOLD;
        boolean secondAngry = second.anger() >= ANGER_ANGRY_THRESHOLD;
        if (firstAngry != secondAngry) return firstAngry ? -1 : 1;

        int priority = Byte.compare(first.priority(), second.priority());
        if (priority != 0) return priority;

        return Integer.compare(second.anger(), first.anger());
    }

    private Entity resolveNuisance(WardenNuisance nuisance) {
        return nuisance == null ? null : this.level.getEntityByUniqueId(nuisance.actorId());
    }

    private WardenNuisance getBestAngryNuisance() {
        List<WardenNuisance> ordered = new ArrayList<>(this.nuisances.values());
        ordered.sort(this::compareNuisances);

        for (WardenNuisance nuisance : ordered) {
            if (nuisance.anger() < ANGER_ANGRY_THRESHOLD) break;
            Entity entity = this.resolveNuisance(nuisance);
            if (entity != null && this.isValidNuisanceEntity(entity)) return nuisance;
        }
        return null;
    }

    private void tickNuisances() {
        var iterator = this.nuisances.values().iterator();
        while (iterator.hasNext()) {
            WardenNuisance nuisance = iterator.next();
            int anger = nuisance.anger() - 1;
            if (anger <= 0) iterator.remove();
            else nuisance.setAnger(anger);
        }

        Entity attackTarget = this.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
        if (attackTarget != null) {
            WardenNuisance nuisance = this.nuisances.get(attackTarget.uniqueIdLong());
            if (nuisance == null || nuisance.anger() < ANGER_ANGRY_THRESHOLD || !this.isValidNuisanceEntity(attackTarget)) {
                this.getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
            }
        }

        this.refreshRoarTarget();
    }

    private void refreshRoarTarget() {
        if (this.isEmerging() || this.isDigging() || this.getDataFlag(ActorFlags.ROARING)) return;

        Entity attackTarget = this.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
        WardenNuisance best = this.getBestAngryNuisance();

        if (best == null || attackTarget != null && attackTarget.uniqueIdLong() == best.actorId()) {
            this.roarTargetId = 0;
            return;
        }

        this.roarTargetId = best.actorId();
    }

    /**
     * Returns whether the Warden currently has a valid roar target.
     *
     * @return whether a valid roar target exists
     */
    public boolean hasRoarTarget() {
        if (this.roarTargetId == 0) return false;
        if (this.getRoarTarget() != null) return true;
        this.roarTargetId = 0;
        return false;
    }

    /**
     * Returns the current valid roar target.
     *
     * @return roar target, or {@code null} when unavailable
     */
    public Entity getRoarTarget() {
        Entity target = this.level.getEntityByUniqueId(this.roarTargetId);
        return target != null && this.isValidNuisanceEntity(target) ? target : null;
    }

    /**
     * Completes the roar transition and promotes its target to the attack target.
     */
    public void finishRoar() {
        long actorId = this.roarTargetId;
        this.roarTargetId = 0;

        WardenNuisance nuisance = this.nuisances.get(actorId);
        Entity target = this.resolveNuisance(nuisance);
        if (nuisance == null || target == null || !this.isValidNuisanceEntity(target)) return;

        nuisance.setAnger(NukkitMath.clamp(nuisance.anger() + ANGER_BOOST, 0, MAX_ANGER));
        this.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, target);
        this.setSonicBoomCooldown(SONIC_BOOM_TARGET_COOLDOWN_TICKS);
    }

    /**
     * Returns whether the Warden currently meets the conditions to begin digging.
     *
     * @return whether digging can start
     */
    public boolean canStartDigging() {
        return !this.isEmerging()
                && !this.isDigging()
                && !this.hasCustomName()
                && this.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)
                && getLevel().getTick() - this.lastDisturbanceTick >= DIG_IDLE_TICKS;
    }

    /**
     * Returns whether the Warden is currently digging.
     *
     * @return whether the Warden is digging
     */
    public boolean isDigging() {
        return this.getDataFlag(ActorFlags.DIGGING);
    }

    private void markDisturbance() {
        this.lastDisturbanceTick = getLevel().getTick();
    }

    /**
     * Returns whether the Warden has a suspicious location to investigate.
     *
     * @return whether a suspicious location exists
     */
    public boolean hasSuspiciousLocation() {
        return this.suspiciousLocation != null;
    }

    /**
     * Returns a copy of the current suspicious location.
     *
     * @return suspicious location, or {@code null} when absent
     */
    public Vector3 getSuspiciousLocation() {
        return this.suspiciousLocation == null ? null : this.suspiciousLocation.clone();
    }

    /**
     * Sets the location the Warden should investigate.
     *
     * @param location suspicious location
     */
    public void setSuspiciousLocation(Vector3 location) {
        if (location == null) return;
        this.suspiciousLocation = location.floor();
        this.markDisturbance();
    }

    /**
     * Clears the suspicious location when it matches the expected position.
     *
     * @param expected expected suspicious location
     */
    public void clearSuspiciousLocation(Vector3 expected) {
        if (expected == null || this.suspiciousLocation == null) return;
        if (this.suspiciousLocation.equals(expected.floor())) this.suspiciousLocation = null;
    }

    @Override
    public boolean canBePushedByEntities() {
        return !this.isEmerging() && !this.isDigging();
    }

    @Override
    public boolean canBePushedByPiston() {
        return !this.isEmerging() && !this.isDigging();
    }

    /**
     * Starts the Warden emerging state.
     */
    public void startEmerging() {
        this.emergingEndTick = getLevel().getTick() + EMERGING_DURATION_TICKS;
        this.setDataFlag(ActorFlags.EMERGING, true);
    }

    /**
     * Returns whether the Warden is currently emerging.
     *
     * @return whether the Warden is emerging
     */
    public boolean isEmerging() {
        return this.emergingEndTick > getLevel().getTick();
    }

    /**
     * Completes the Warden emerging state.
     */
    public void finishEmerging() {
        this.emergingEndTick = 0;
        this.setDataFlag(ActorFlags.EMERGING, false);
    }

    @Override
    public Vector3 getListenerVector() {
        return this.getVector3();
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        boolean listenable = event.type() == VibrationType.SHRIEK || event.type() != VibrationType.FLAP && event.type().isVibration();
        return listenable && getLevel().getTick() - this.lastDetectTime >= VIBRATION_COOLDOWN_TICKS && !(event.initiator() instanceof EntityWarden);
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        this.lastDetectTime = getLevel().getTick();
        this.setSuspiciousLocation(event.source());
        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(this.runtimeId());
        pk.setType(ActorEvent.VIBRATION_DETECTED);
        Server.broadcastPacket(this.getViewers().values(), pk);

        //handle anger value
        Entity annoyanceTarget = null;
        int annoyance = 35;
        if (event.projectileOwnerUniqueId() != 0) {
            annoyanceTarget = this.level.getEntityByUniqueId(event.projectileOwnerUniqueId());
            annoyance = 10;
        } else if (event.initiator() instanceof Entity entity) {
            annoyanceTarget = entity;
        }
        if (annoyanceTarget != null && isValidAngerEntity(annoyanceTarget)) {
            addEntityAngerValue(annoyanceTarget, annoyance);
        }

        if (this.getAngerLevel() >= ANGER_AGITATED_THRESHOLD)
            this.level.addSound(this, Sound.MOB_WARDEN_LISTENING_ANGRY);
        else this.level.addSound(this, Sound.MOB_WARDEN_LISTENING);
    }

    @Override
    public double getListenRange() {
        return 16;
    }

    @Override
    public void close() {
        if (this.level != null) this.level.getVibrationManager().removeListener(this);
        super.close();
    }

    @Override
    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        //anti-kb
    }

    @Override
    protected boolean onCollide(int currentTick, List<Entity> collidingEntities) {
        if (getLevel().getTick() - this.lastCollideTime > 20) {
            for (Entity collidingEntity : collidingEntities) {
                if (isValidAngerEntity(collidingEntity)) {
                    this.setSuspiciousLocation(collidingEntity.getVector3());
                    addEntityAngerValue(collidingEntity, 35);
                }
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

        Entity previousTarget = this.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
        if (source instanceof EntityDamageByEntityEvent damageByEntity) {
            Entity damager = damageByEntity.getDamager();
            Entity realDamager = damager instanceof EntityProjectile projectile ? projectile.shootingEntity : damager;
            if (realDamager != null && this.isValidNuisanceEntity(realDamager)) {
                this.addEntityAngerValue(realDamager, 100);
            }
        }

        boolean result = super.attack(source);
        if (previousTarget == null) this.getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
        else this.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, previousTarget);
        this.refreshRoarTarget();
        return result;
    }

    public void addEntityAngerValue(Entity entity, int addition) {
        if (!this.isValidNuisanceEntity(entity)) return;

        this.markDisturbance();
        long actorId = entity.uniqueIdLong();
        if (actorId == 0) return;

        WardenNuisance nuisance = this.nuisances.get(actorId);
        int anger = NukkitMath.clamp((nuisance == null ? 0 : nuisance.anger()) + addition, 0, MAX_ANGER);

        if (anger <= 0) {
            this.nuisances.remove(actorId);
        } else if (nuisance == null) {
            this.nuisances.put(actorId, new WardenNuisance(actorId, anger, (byte) (entity instanceof Player ? 0 : 1)));
        } else {
            nuisance.setAnger(anger);
        }

        this.refreshRoarTarget();
    }

    private boolean isValidNuisanceEntity(Entity entity) {
        if (entity == null || entity.isClosed()) return false;
        if (entity.getHealthCurrent() <= 0) return false;
        if (!(entity instanceof EntityCreature)) return false;
        if (entity instanceof Player player && (!player.isSurvival() && !player.isAdventure())) return false;
        return !(entity instanceof EntityWarden);
    }

    public boolean isValidAngerEntity(Entity entity) {
        return isValidAngerEntity(entity, false);
    }

    public boolean isValidAngerEntity(Entity entity, boolean sniff) {
        return this.isValidNuisanceEntity(entity) && (sniff ? this.isInSuspicionRange(entity) : this.isInAngerRange(entity));
    }

    /**
     * Returns whether a player is a valid sniff target for this Warden.
     *
     * @param player player to test
     * @return whether the player is a valid sniff target
     */
    public boolean isValidSniffTarget(Player player) {
        return this.isValidNuisanceEntity(player) && !player.isIgnoredByEntities() && this.isInSniffRange(player);
    }

    public boolean isInSniffRange(Entity entity) {
        return this.distanceSquared(entity) <= 24 * 24;
    }

    /**
     * Returns whether an entity is within the Warden's suspicion range.
     *
     * @param entity entity to test
     * @return whether the entity is within suspicion range
     */
    public boolean isInSuspicionRange(Entity entity) {
        double deltaX = this.x - entity.x;
        double deltaZ = this.z - entity.z;
        var distanceXZSqrt = deltaX * deltaX + deltaZ * deltaZ;
        var deltaY = Math.abs(this.y - entity.y);
        return distanceXZSqrt <= 36 && deltaY <= 20;
    }

    public boolean isInRangedAttackRange(Entity entity) {
        double deltaX = this.x - entity.x;
        double deltaZ = this.z - entity.z;
        var distanceXZSqrt = deltaX * deltaX + deltaZ * deltaZ;
        var deltaY = Math.abs(this.y - entity.y);
        return distanceXZSqrt <= 225 && deltaY <= 20;
    }

    public boolean isInAngerRange(Entity entity) {
        var distanceSqrt = this.distanceSquared(entity);
        return distanceSqrt <= 625;
    }

    /**
     * Returns the highest current anger value among tracked nuisances.
     *
     * @return current anger level
     */
    public int getAngerLevel() {
        int anger = 0;
        for (WardenNuisance nuisance : this.nuisances.values()) {
            anger = Math.max(anger, nuisance.anger());
        }
        return anger;
    }

    public int calHeartBeatDelay() {
        var anger = this.getAngerLevel();
        return (int) (40 - NukkitMath.clamp((anger / 80f), 0, 1) * 30f);
    }

    @Override
    public void setOnFire(int seconds) {
        //against fire
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[]{Item.get(BlockID.SCULK_CATALYST)};
    }
}
