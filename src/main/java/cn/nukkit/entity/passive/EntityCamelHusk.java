package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToRiderTargetExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.executor.camel.CamelSittingExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.EquippableComponent;
import cn.nukkit.entity.components.HealableComponent;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.entity.mob.EntityHusk;
import cn.nukkit.entity.mob.EntityParched;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class EntityCamelHusk extends EntityCamel {
    @Override
    @NotNull public String getIdentifier() {
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
                        new Vector3f( 0.0f, 1.905f, 0.5f),
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

        boolean riderSpawned = this.namedTag != null && this.namedTag.getBoolean(NBT_RIDER_SPAWNED);
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
        if (this.namedTag != null && this.namedTag.getBoolean(NBT_RIDER_SPAWNED)) return;

        if (!this.passengers.isEmpty()) {
            if (this.namedTag != null) this.namedTag.putBoolean(NBT_RIDER_SPAWNED, true);
            return;
        }

        Entity rider = createRiderEntity(Entity.HUSK);
        Entity passenger = createRiderEntity(Entity.PARCHED);

        rider.spawnToAll();
        passenger.spawnToAll();
        this.mountEntity(rider, true);
        this.mountEntity(passenger, true);

        if (this.namedTag != null) this.namedTag.putBoolean(NBT_RIDER_SPAWNED, true);
    }

    private Entity createRiderEntity(String entityId) {
        CompoundTag nbt = Entity.getDefaultNBT(this.getLocation());

        if (entityId.equals(Entity.HUSK)) {
            Item ironSpear = Item.get(Item.IRON_SPEAR, 0, 1);
            nbt.put("Mainhand", NBTIO.putItemHelper(ironSpear));
            nbt.putBoolean(NBT_HUSK_RIDER, true);
        }
        if (entityId.equals(Entity.PARCHED)) {
            Item bow = Item.get(Item.BOW, 0, 1);
            nbt.put("Mainhand", NBTIO.putItemHelper(bow));
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
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                ),
                Set.of(
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
                ),
                Set.of(
                    new NearestPlayerSensor(8, 0, 20)
                ),
                Set.of(
                    new WalkController(),
                    new LookController(true, true),
                    new FluctuateController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
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
