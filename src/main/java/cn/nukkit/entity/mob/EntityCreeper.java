package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.executor.EntityExplosionExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.passive.EntityCat;
import cn.nukkit.entity.passive.EntityOcelot;
import cn.nukkit.entity.weather.EntityLightningStrike;
import cn.nukkit.event.entity.CreeperPowerEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Box.
 */
public class EntityCreeper extends EntityMob implements EntityWalkable, EntityInteractable {
    @Override
    @NotNull
    public String getIdentifier() {
        return CREEPER;
    }

    public EntityCreeper(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.3f, true, 4), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY), 5, 1),
                        new Behavior(
                                new EntityExplosionExecutor(30, 3, CoreMemoryTypes.SHOULD_EXPLODE),
                                all(
                                        entity -> entity.getMemoryStorage().compareDataTo(CoreMemoryTypes.SHOULD_EXPLODE, true),
                                        any(
                                                entity -> getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER) != null && getLevel().raycastBlocks(this, getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER)).isEmpty(),
                                                entity -> getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) != null && getLevel().raycastBlocks(this, getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET)).isEmpty()
                                        )
                                ), 4, 1
                        ),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, true, 16f, 3f, true), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                entity -> !entity.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET) || !(entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof Player player) || player.isSurvival()
                        ), 3, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, true, 16f, 3f), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                entity -> {
                                    if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_PLAYER)) return true;
                                    Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
                                    return player.isSurvival();
                                }
                        ), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(16, 0, 20),
                        new NearestEntitySensor(EntityCat.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 42, 0),
                        new NearestEntitySensor(EntityOcelot.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 42, 0),
                        entity -> {
                    var memoryStorage = entity.getMemoryStorage();
                    Entity attacker = memoryStorage.get(CoreMemoryTypes.ATTACK_TARGET);
                    if (attacker == null)
                        attacker = memoryStorage.get(CoreMemoryTypes.NEAREST_PLAYER);
                    if (attacker != null && (!(attacker instanceof Player player) || player.isSurvival()) && attacker.distanceSquared(entity) <= 3 * 3 && (memoryStorage.isEmpty(CoreMemoryTypes.SHOULD_EXPLODE) || memoryStorage.compareDataTo(CoreMemoryTypes.SHOULD_EXPLODE, false))) {
                        memoryStorage.put(CoreMemoryTypes.SHOULD_EXPLODE, true);
                        return;
                    }
                    if ((attacker == null || (attacker instanceof Player player && !player.isSurvival()) || attacker.distanceSquared(entity) >= 7 * 7) && memoryStorage.compareDataTo(CoreMemoryTypes.SHOULD_EXPLODE, true) && memoryStorage.get(CoreMemoryTypes.EXPLODE_CANCELLABLE)) {
                        memoryStorage.put(CoreMemoryTypes.SHOULD_EXPLODE, false);
                    }
                }),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }


    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public float getFloatingHeight() {
        return 0.6f;
    }

    public boolean isPowered() {
        return getDataProperty(HORSE_TYPE) > 0;
    }

    public void setPowered(EntityLightningStrike bolt) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, bolt, CreeperPowerEvent.PowerCause.LIGHTNING);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.setDataProperty(HORSE_TYPE, 1);
            this.namedTag.putBoolean("powered", true);
        }
    }

    public void setPowered(boolean powered) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, powered ? CreeperPowerEvent.PowerCause.SET_ON : CreeperPowerEvent.PowerCause.SET_OFF);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.setDataProperty(HORSE_TYPE, powered ? 1 : 0);
            this.namedTag.putBoolean("powered", powered);
        }
    }

    @Override
    public void onStruckByLightning(Entity entity) {
        this.setPowered(true);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();

        if (this.namedTag.getBoolean("powered") || this.namedTag.getBoolean("IsPowered")) {
            this.entityDataMap.put(HORSE_TYPE, 1);
        }
    }

    @Override
    public String getOriginalName() {
        return "Creeper";
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(Item.GUNPOWDER, 0, ThreadLocalRandom.current().nextInt(2) + 1)};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        var memoryStorage = this.getMemoryStorage();
        if (item.getId() == Item.FLINT_AND_STEEL && (memoryStorage.isEmpty(CoreMemoryTypes.SHOULD_EXPLODE) || memoryStorage.compareDataTo(CoreMemoryTypes.SHOULD_EXPLODE, false))) {
            memoryStorage.put(CoreMemoryTypes.SHOULD_EXPLODE, true);
            memoryStorage.put(CoreMemoryTypes.EXPLODE_CANCELLABLE, false);
            this.level.addSound(this, Sound.FIRE_IGNITE);
            return true;
        }
        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public String getInteractButtonText(Player player) {
        return "action.interact.creeper";
    }

    @Override
    public boolean canDoInteraction() {
        return true;
    }
}
