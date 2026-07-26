package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.MoveToRiderTargetExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.executor.camel.CamelSittingExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.EquippableComponent;
import org.powernukkitx.entity.components.HealableComponent;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.entity.mob.EntityHusk;
import org.powernukkitx.entity.mob.EntityParched;
import org.powernukkitx.inventory.HorseInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class EntityCamelHusk extends EntityCamel {
    @Override
    @NotNull
    public String getIdentifier() {
        return CAMEL_HUSK;
    }

    public EntityCamelHusk(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private static final String NBT_RIDER_SPAWNED = "StriderInitialRiderSpawned";
    private static final String NBT_HUSK_RIDER = "IsHuskRider";
    private boolean pendingInitialRiderSpawn;


    @Override
    public float getWidth() {
        return 1.7f;
    }

    @Override
    public float getHeight() {
        if (isSitting()) return 0.945f;
        return 2.375f;
    }

    @Override
    public boolean isRideable() {
        return !this.isBaby();
    }

    @Override
    public RideableComponent getComponentRideable() {
        return new RideableComponent(
                0,
                true,
                RideableComponent.DismountMode.DEFAULT,
                Set.of("player", "parched", "husk_rider"),
                "action.interact.ride.horse",
                0.0f,
                true,
                false,
                2,
                List.of(
                        new RideableComponent.Seat(
                                0,
                                2,
                                new Vector3f(0.0f, 1.905f, 0.5f),
                                null,
                                null,
                                null,
                                null
                        ),
                        new RideableComponent.Seat(
                                1,
                                2,
                                new Vector3f(0.0f, 1.905f, -0.5f),
                                null,
                                null,
                                null,
                                null
                        )
                )
        );
    }

    @Override
    public String getOriginalName() {
        return "Camel Husk";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("camelhusk", "undead", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return null;
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                List.of(
                        new HealableComponent.Item(ItemID.RABBIT_FOOT, 2)
                )
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return null;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        boolean riderSpawned = this.nbt != null && this.getNbt().getBoolean(NBT_RIDER_SPAWNED);
        if (!riderSpawned) {
            this.pendingInitialRiderSpawn = true;
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean updated = super.onUpdate(currentTick);

        if (this.pendingInitialRiderSpawn) {
            this.pendingInitialRiderSpawn = false;
            spawnInitialRiderIfNeeded();
        }

        return updated;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        ArrayList<Item> drops = new ArrayList<>();
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 2) != 0) {
            int rottenFlesh = Utils.rand(2, 3 + looting);
            if (rottenFlesh > 0) {
                drops.add(Item.get(Item.ROTTEN_FLESH, 0, rottenFlesh));
            }
        }

        // Drop Ride Inventory
        ensureInventories();
        drops.addAll(Arrays.asList(HorseInventory.getInventoryDrops(getInventory(), this)));

        if (drops.isEmpty()) return Item.EMPTY_ARRAY;
        return drops.toArray(new Item[0]);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.passengers.stream().anyMatch(p -> p instanceof EntityHusk || p instanceof EntityParched)) return false;

        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (!item.isNull()) {
            if (item.getId() == Item.SADDLE && !this.isSaddled()) {
                this.getInventory().setEquippedItem(EquippableComponent.Type.SADDLE, Item.get(Item.SADDLE, 0, 1));
                return true;

            } else if (item.getId() == Item.SHEARS && this.isSaddled()) {
                if (!this.passengers.isEmpty()) return false;

                HorseInventory<?> inv = getInventory();
                Item saddleItem = inv.getEquippedItem(EquippableComponent.Type.SADDLE);
                this.getInventory().setEquippedItem(EquippableComponent.Type.SADDLE, Item.AIR);

                if (player.getInventory().canAddItem(saddleItem)) {
                    player.getInventory().addItem(saddleItem);
                } else {
                    this.getLevel().dropItem(clickedPos, saddleItem);
                }

                return true;
            }
        }

        if (isSaddled() && isSitting()) standUp();
        mountEntity(player, true);
        return false;
    }

    private void spawnInitialRiderIfNeeded() {
        if (this.closed || this.level == null) return;
        if (this.nbt != null && this.getNbt().getBoolean(NBT_RIDER_SPAWNED)) return;

        if (!this.passengers.isEmpty()) {
            if (this.nbt != null)
                this.nbt.putBoolean(NBT_RIDER_SPAWNED, true);
            return;
        }

        Entity rider = createRiderEntity(Entity.HUSK);
        Entity passenger = createRiderEntity(Entity.PARCHED);

        rider.spawnToAll();
        passenger.spawnToAll();
        this.mountEntity(rider, true);
        this.mountEntity(passenger, true);

        if (this.nbt != null)
            this.nbt.putBoolean(NBT_RIDER_SPAWNED, true);
    }

    private Entity createRiderEntity(String entityId) {
        CompoundTag nbt = Entity.getDefaultNBT(this.getLocation());

        if (entityId.equals(Entity.HUSK)) {
            Item ironSpear = Item.get(Item.IRON_SPEAR, 0, 1);
            nbt.putCompound("Mainhand", ItemHelper.write(ironSpear));
            nbt.putBoolean(NBT_HUSK_RIDER, true);
        }
        if (entityId.equals(Entity.PARCHED)) {
            Item bow = Item.get(Item.BOW, 0, 1);
            nbt.putCompound("Mainhand", ItemHelper.write(bow));
        }

        Entity newEntity = Entity.createEntity(entityId, this.getChunk(), nbt);
        return newEntity;
    }

    public boolean isRiddenByMob() {
        return this.getRider() != null && !(this.getRider() instanceof Player);
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
            ItemID.RABBIT_FOOT
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(
                                new MoveToRiderTargetExecutor(this.getMovementSpeedDefault() * 4.00f, true),
                                e -> this.isRiddenByMob(),
                                5, 1
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(this.getMovementSpeedDefault() * 1.25f, 18, 8, true, 80, true, 10),
                                all(
                                        e -> !this.isRiddenByMob(),
                                        e -> e.passengers.isEmpty(),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 80)
                                ),
                                4, 1
                        ),
                        new Behavior(
                                new TemptExecutor(2.5f, TEMPT_ITEMS),
                                all(
                                        e -> !this.isRiddenByMob(),
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                                ),
                                3, 1
                        ),
                        new Behavior(
                                new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                                all(
                                        new ProbabilityEvaluator(4, 10),
                                        e -> e.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_PLAYER),
                                        e -> {
                                            Player p = e.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
                                            return p != null && !e.isPassenger(p);
                                        },
                                        e -> e.passengers == null || e.passengers.isEmpty()
                                ),
                                2, 1, 100
                        ),
                        new Behavior(
                                new CamelSittingExecutor(8),
                                all(
                                        e -> !this.isRiddenByMob(),
                                        e -> !((EntityCamel) e).isSitting(),
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> Utils.rand(1, 35) == 1
                                ),
                                2, 1, 200
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(this.getMovementSpeedDefault(), 12, 100, false, -1, true, 10),
                                all(
                                        e -> !this.isRiddenByMob(),
                                        e -> !((EntityCamel) e).isSitting()
                                ),
                                1, 1
                        )
                )
                .sensors(
                        new NearestPlayerSensor(8, 0, 20)
                )
                .controllers(
                        new WalkController(),
                        new LookController(true, true),
                        new FluctuateController()
                )
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }


    @Override
    public void asyncPrepare(int currentTick) {
        Entity rider = this.getRider();
        if (rider instanceof Player && !this.isSaddled()) return;

        isActive = level.isHighLightChunk(getChunkX(), getChunkZ());
        if (!this.isImmobile()) {
            var behaviorGroup = getBehaviorGroup();
            if (behaviorGroup == null) return;
            behaviorGroup.collectSensorData(this);
            behaviorGroup.evaluateCoreBehaviors(this);
            behaviorGroup.evaluateBehaviors(this);
            behaviorGroup.tickRunningCoreBehaviors(this);
            behaviorGroup.tickRunningBehaviors(this);
            behaviorGroup.updateRoute(this);
            behaviorGroup.applyController(this);
            if (EntityAI.checkDebugOption(EntityAI.DebugOption.BEHAVIOR)) behaviorGroup.debugTick(this);
        }
        this.needsRecalcMovement = this.level.tickRateOptDelay == 1 || ((currentTick + tickSpread) & (this.level.tickRateOptDelay - 1)) == 0;
        this.calculateOffsetBoundingBox();
        if (!this.isImmobile()) {
            handleGravity();
            if (needsRecalcMovement) {
                handleCollideMovement(currentTick);
            }
            addTmpMoveMotionXZ(previousCollideMotion);
            handleFloatingMovement();
            handleGroundFrictionMovement();
            handlePassableBlockFrictionMovement();
        }
    }
}
