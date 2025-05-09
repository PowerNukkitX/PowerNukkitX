package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomTimeRangeEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.WardenMeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.WardenRangedAttackExecutor;
import cn.nukkit.entity.ai.executor.WardenSniffExecutor;
import cn.nukkit.entity.ai.executor.WardenViolentAnimationExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.RouteUnreachableTimeSensor;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationListener;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityWarden extends EntityMob implements EntityWalkable, VibrationListener {

    protected int lastDetectTime = getLevel().getTick();
    protected int lastCollideTime = getLevel().getTick();
    protected boolean waitForVibration = false;
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
                            //刷新随机播放音效
                            if (this.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET))
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_ANGRY);
                            else if (this.getMemoryStorage().notEmpty(CoreMemoryTypes.WARDEN_ANGER_VALUE))
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_AGITATED);
                            else
                                this.setAmbientSoundEvent(Sound.MOB_WARDEN_IDLE);
                            return false;
                        }, (entity) -> true, 1, 1, 20),
                        new Behavior((entity) -> {
                            //刷新anger数值
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
                            //为玩家附加黑暗效果
                            for (var player : entity.level.getPlayers().values()) {
                                if (!player.isCreative() && !player.isSpectator()
                                        && entity.distanceSquared(player) <= 400) {
                                    var effect = player.getEffect(EffectType.DARKNESS);
                                    if (effect == null) {
                                        effect = Effect.get(EffectType.DARKNESS);
                                        effect.setDuration(260);
                                        player.addEffect(effect);
                                        continue;
                                    }
                                    effect.setDuration(effect.getDuration() + 260);
                                    player.addEffect(effect);
                                }
                            }
                            return false;
                        }, (entity) -> true, 1, 1, 120),
                        new Behavior((entity) -> {
                            //计算心跳间隔
                            this.setDataProperty(Entity.HEARTBEAT_INTERVAL_TICKS, this.calHeartBeatDelay());
                            return false;
                        }, (entity) -> true, 1, 1, 20)),
                Set.of(
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
                                            return player.isSurvival() || player.isAdventure();
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
    protected void initEntity() {
        this.setMaxHealth(500);
        super.initEntity();
        this.setDataProperty(Entity.HEARTBEAT_INTERVAL_TICKS, 40);
        this.setDataProperty(Entity.HEARTBEAT_SOUND_EVENT, LevelSoundEventPacket.SOUND_HEARTBEAT);
        //空闲声音
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
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.VIBRATION_DETECTED;
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
        if (getLevel().getTick()- this.lastCollideTime > 20) {
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
        if (entity.getHealth() <= 0) return false;
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
