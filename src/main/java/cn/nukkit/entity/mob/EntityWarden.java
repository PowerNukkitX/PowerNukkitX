package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AllMatchEvaluator;
import cn.nukkit.entity.ai.evaluator.AttackTargetChangedMemory;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomTimeRangeEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.AttackTargetMemory;
import cn.nukkit.entity.ai.memory.WardenAngerValueMemory;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationListener;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;

import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityWarden extends EntityWalkingMob implements VibrationListener {

    public static final int NETWORK_ID = 131;

    private IBehaviorGroup behaviorGroup;

    protected int lastDetectTime = Server.getInstance().getTick();

    protected int lastCollideTime = Server.getInstance().getTick();

    protected boolean waitForVibration = false;

    public EntityWarden(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null) {
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(
                            new Behavior((entity) -> {
                                //刷新anger数值
                                var angerValueMap = this.getMemoryStorage().get(WardenAngerValueMemory.class).getData();
                                var iterator = angerValueMap.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Map.Entry<Entity, Integer> next = iterator.next();
                                    if (!isValidAngerEntity(next.getKey())) {
                                        iterator.remove();
                                        var attackTargetMemory = this.getMemoryStorage().get(AttackTargetMemory.class);
                                        if (attackTargetMemory.hasData() && attackTargetMemory.getData().equals(next.getKey()))
                                            attackTargetMemory.setData(null);
                                        continue;
                                    }
                                    var newAnger = next.getValue() - 1;
                                    if (newAnger == 0) iterator.remove();
                                    else next.setValue(newAnger);
                                }
                                return false;
                            },
                                    (entity) -> true, 1, 1, 20),
                            new Behavior((entity) -> {
                                //为玩家附加黑暗效果
                                for (var player : entity.level.getPlayers().values()) {
                                    if (!player.isCreative() && !player.isSpectator()
                                            && entity.distanceSquared(player) <= 400) {
                                        var effect = player.getEffect(Effect.DARKNESS);
                                        if (effect == null) {
                                            effect = Effect.getEffect(Effect.DARKNESS);
                                            effect.setDuration(260);
                                            player.addEffect(effect);
                                            continue;
                                        }
                                        effect.setDuration(effect.getDuration() + 260);
                                    }
                                }
                                return false;
                            }, (entity) -> true, 1, 1, 120),
                            new Behavior((entity) -> {
                                //计算心跳间隔
                                this.setDataProperty(new IntEntityData(Entity.DATA_HEARTBEAT_INTERVAL_TICKS, this.calHeartBeatDelay()));
                                return false;
                            }, (entity) -> true, 1, 1, 20)),
                    Set.of(
                            new Behavior(
                                    new WardenViolentAnimationExecutor((int) (4.2 * 20)),
                                    new AllMatchEvaluator(
                                            (entity) -> entity.getMemoryStorage().checkData(AttackTargetChangedMemory.class, true),
                                            new MemoryCheckNotEmptyEvaluator(AttackTargetMemory.class)
                                    ), 5),
                            new Behavior(
                                    new WardenRangedAttackExecutor((int) (1.7 * 20), (int) (3.0 * 20), switch (this.getServer().getDifficulty()) {
                                        case 1 -> 6;
                                        case 2 -> 10;
                                        case 3 -> 15;
                                        default -> 0;
                                    }),
                                    //                                                                                                                   加这行判断是为了防止过于频繁的发射远程攻击
                                    (entity) -> !this.getBehaviorGroup().getRouteFinder().isReachable() && (!(this.getMoveTarget() instanceof Entity) || ((Entity)this.getMoveTarget()).isOnGround()) && this.getMemoryStorage().notEmpty(AttackTargetMemory.class), 4, 1, 20
                            ),
                            new Behavior(
                                    new WardenMeleeAttackExecutor(AttackTargetMemory.class, switch (this.getServer().getDifficulty()) {
                                        case 1 -> 16;
                                        case 2 -> 30;
                                        case 3 -> 45;
                                        default -> 0;
                                    }, 0.5f),
                                    new MemoryCheckNotEmptyEvaluator(AttackTargetMemory.class)
                                    , 3, 1
                            ),
                            new Behavior(new WardenSniffExecutor((int) (4.2 * 20), 35), new RandomTimeRangeEvaluator(5 * 20, 10 * 20), 2),
                            new Behavior(new RandomRoamExecutor(0.1f, 12, 100, true, -1, true, 10), (entity -> true), 1)
                    ),
                    Set.of(),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
            );
        }
        return this.behaviorGroup;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(500);
        this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_FIRE_IMMUNE, true);
        this.setDataProperty(new IntEntityData(Entity.DATA_HEARTBEAT_INTERVAL_TICKS, 40));
        this.setDataProperty(new IntEntityData(Entity.DATA_HEARTBEAT_SOUND_EVENT, LevelSoundEventPacket.SOUND_HEARTBEAT));
        this.level.getVibrationManager().addListener(this);
    }

    @Override
    public String getOriginalName() {
        return "Warden";
    }

    @Override
    public Vector3 getListenerVector() {
        return this.clone();
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        if (Server.getInstance().getTick() - this.lastDetectTime >= 40 && !waitForVibration && !(event.initiator() instanceof EntityWarden)) {
            this.waitForVibration = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        this.waitForVibration = false;
        this.lastDetectTime = Server.getInstance().getTick();
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
        if (Server.getInstance().getTick() - this.lastCollideTime > 20) {
            for (Entity collidingEntity : collidingEntities) {
                if (isValidAngerEntity(collidingEntity))
                    addEntityAngerValue(collidingEntity, 35);
            }
            this.lastCollideTime = Server.getInstance().getTick();
        }
        return super.onCollide(currentTick, collidingEntities);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        var cause = source.getCause();
        if (cause == EntityDamageEvent.DamageCause.LAVA || cause == EntityDamageEvent.DamageCause.HOT_FLOOR || cause == EntityDamageEvent.DamageCause.DROWNING)
            return false;
        if (source instanceof EntityDamageByEntityEvent damageByEntity && isValidAngerEntity(damageByEntity.getDamager())) {
            addEntityAngerValue(damageByEntity.getDamager(), 100);
        }
        return super.attack(source);
    }

    public void addEntityAngerValue(Entity entity, int addition) {
        var angerValueMap = this.getMemoryStorage().get(WardenAngerValueMemory.class).getData();
        var attackTargetMemory = this.getMemoryStorage().get(AttackTargetMemory.class);
        var origin = angerValueMap.containsKey(entity) ? angerValueMap.get(entity) : 0;
        int added = NukkitMath.clamp(origin + addition, 0, 150);
        if (added == 0) angerValueMap.remove(entity);
        else if (added >= 80) {
            added += 20;
            added = NukkitMath.clamp(added, 0, 150);
            angerValueMap.put(entity, added);
            boolean changed = false;
            if (!attackTargetMemory.hasData()) {
                attackTargetMemory.setData(entity);
                changed = true;
            } else if (entity instanceof Player && !(attackTargetMemory.getData() instanceof Player)) {
                attackTargetMemory.setData(entity);
                changed = true;
            } /*else if (added > angerValueMap.get(attackTargetMemory.getData())) {
                attackTargetMemory.setData(entity);
                changed = true;
            }*/
            if (changed) {
                this.getMemoryStorage().setData(AttackTargetChangedMemory.class, true);
            }
        } else angerValueMap.put(entity, added);
    }

    public void removeEntityAngerValue(Entity entity) {
        this.getMemoryStorage()
                .get(WardenAngerValueMemory.class)
                .getData()
                .remove(entity);
    }

    public boolean isValidAngerEntity(Entity entity) {
        return isValidAngerEntity(entity, false);
    }

    public boolean isValidAngerEntity(Entity entity, boolean sniff) {
        if (entity.isClosed()) return false;
        if (!(sniff ? isInSniffRange(entity) : isInAngerRange(entity))) return false;
        if (!(entity instanceof EntityCreature)) return false;
        if (entity instanceof Player player && (!player.isSurvival() && !player.isAdventure())) return false;
        if (entity instanceof EntityWarden) return false;
        return true;
    }

    public boolean isInSniffRange(Entity entity) {
        double deltaX = this.x - entity.x;
        double deltaZ = this.z - entity.z;
        var distanceXZSqrt = deltaX * deltaX + deltaZ * deltaZ;
        var deltaY = Math.abs(this.y - entity.y);
        return distanceXZSqrt <= 36
                && deltaY <= 400;
    }

    public boolean isInAngerRange(Entity entity) {
        var distanceSqrt = this.distanceSquared(entity);
        return distanceSqrt <= 625;
    }

    public int calHeartBeatDelay() {
        var target = this.getMemoryStorage().getData(AttackTargetMemory.class);
        if (target == null) return 40;
        var anger = this.getMemoryStorage().getData(WardenAngerValueMemory.class).getOrDefault(target, 0);
        return (int) (40 - NukkitMath.clamp((anger / 80f), 0, 1) * 30f);
    }
}
