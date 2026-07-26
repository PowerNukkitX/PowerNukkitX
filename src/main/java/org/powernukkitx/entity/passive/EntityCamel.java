package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.executor.camel.CamelSittingExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.FollowEntitySensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.DashActionComponent;
import org.powernukkitx.entity.components.EquippableComponent;
import org.powernukkitx.entity.components.HealableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.InventoryComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.inventory.HorseInventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class EntityCamel extends EntityAnimal implements InventoryHolder {
    @Override
    @NotNull
    public String getIdentifier() {
        return CAMEL;
    }

    public EntityCamel(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected HorseInventory<EntityCamel> entityInventory;


    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.85f;
        }
        return 1.7f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            if (isSitting()) return 0.472f;
            return 1.1875f;
        }
        if (isSitting()) return 0.945f;
        return 2.375f;
    }

    @Override
    public float getFootHeight() {
        if (!isBaby()) {
            return 1.5f;
        }
        return super.getFootHeight();
    }

    @Override
    public boolean isTamed() {
        return true;
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
                Set.of("player"),
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
    public RideableComponent.InputType getInputControlType() {
        return RideableComponent.InputType.GROUND;
    }

    @Override
    public boolean canBeSaddled() {
        return true;
    }

    @Override
    public @Nullable EquippableComponent getComponentEquippable() {
        return new EquippableComponent(List.of(
                new EquippableComponent.Slot(
                        0,
                        EquippableComponent.Type.SADDLE,
                        Set.of("minecraft:saddle"),
                        null
                )
        ));
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(32);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.15f);
    }

    @Override
    public float getSprintMultiplier() {
        return 2.5f;
    }

    @Override
    public @Nullable DashActionComponent getComponentDashAction() {
        return new DashActionComponent(
                false,
                2.75f,
                DashActionComponent.Direction.ENTITY,
                20.0f,
                0.6f
        );
    }

    @Override
    protected double getStepHeight() {
        return 1.5625f;
    }

    @Override
    protected double getStepHeightControlled() {
        return 1.5625f;
    }

    @Override
    protected double getStepHeightJumpPrevented() {
        return 0.5625;
    }

    @Override
    public float getKnockbackResistance() {
        if (this.isSitting()) return 1.0f;
        return 0.8f;
    }

    @Override
    public boolean canBePushedByEntities() {
        return !this.isSitting();
    }

    @Override
    public boolean canBePushedByPiston() {
        return true;
    }

    @Override
    public String getOriginalName() {
        return "Camel";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("camel", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                Set.of(
                        BlockID.CACTUS
                ),
                List.of(
                        new BreedableComponent.BreedsWith(EntityID.CAMEL, EntityID.CAMEL)
                ),
                false
        );
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                List.of(
                        new HealableComponent.Item(BlockID.CACTUS, 2)
                )
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                        new AgeableComponent.FeedItem(BlockID.CACTUS)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public @Nullable InventoryComponent getComponentInventory() {
        return new InventoryComponent(
                null,
                false,
                InventoryComponent.Type.HORSE,
                null,
                false,
                false
        );
    }

    @Override
    public HorseInventory<EntityCamel> getInventory() {
        ensureInventories();
        return entityInventory;
    }

    protected void ensureInventories() {
        if (this.entityInventory == null)
            this.entityInventory = new HorseInventory<>(this, getComponentInventory().size());
    }

    @Override
    public void initEntity() {
        super.initEntity();

        // Load items
        ensureInventories();
        if (nbt.containsList("Inventory")) {
            entityInventory.load(nbt.getList("Inventory", CompoundTag.class).getAll());
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        getInventory().sendEquippedVisuals(player);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        var inv = getInventory();
        nbt.putList("Inventory", inv.save(isChested()));
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        ArrayList<Item> drops = new ArrayList<>();

        // Drop Ride Inventory
        ensureInventories();
        drops.addAll(Arrays.asList(HorseInventory.getInventoryDrops(getInventory(), this)));

        if (drops.isEmpty()) return Item.EMPTY_ARRAY;
        return drops.toArray(new Item[0]);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (this.isBaby()) return false;

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

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (isSitting()) standUp();
        return super.attack(source);
    }

    public boolean isSitting() {
        return this.getDataFlag(ActorFlags.SITTING);
    }

    public boolean canSitHere() {
        Block above = this.level.getBlock(this.add(0, 1, 0));
        return above == null || above.canPassThrough();
    }

    public void sitDown() {
        if (isSitting()) return;
        if (!canSitHere()) return;

        this.setDataFlag(ActorFlags.SITTING, true);
        this.setDataProperty(ActorDataTypes.SITTING_AMOUNT, 1.05f);
        this.setDataProperty(ActorDataTypes.SITTING_AMOUNT_PREVIOUS, 1.05f);
        this.setMovementSpeed(0);
    }

    public void standUp() {
        if (!isSitting()) return;

        this.setDataFlag(ActorFlags.SITTING, false);
        this.setDataProperty(ActorDataTypes.SITTING_AMOUNT, 0f);
        this.setDataProperty(ActorDataTypes.SITTING_AMOUNT_PREVIOUS, 0f);

        this.setMovementSpeed(this.getMovementSpeedDefault());
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
            BlockID.CACTUS
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .coreBehaviors(
                        new Behavior(
                                new LoveTimeoutExecutor(20 * 30),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                2, 1
                        ),
                        new Behavior(
                                new AnimalGrowExecutor(),
                                all(
                                        e -> e.isAgeable(),
                                        e -> e.isBaby(),
                                        e -> !e.isGrowthPaused(),
                                        e -> e.getTicksGrowLeft() > 0
                                ),
                                1, 1, 1200
                        )
                )
                .behaviors(
                        new Behavior(
                                new BreedingExecutor(16, 200, 0.35f),
                                all(
                                        e -> !e.isBaby(),
                                        e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                                ),
                                6, 1
                        ),
                        new Behavior(
                                new MoveToTargetExecutor(CoreMemoryTypes.STAY_NEARBY, this.getMovementSpeedDefault() * 1.10f, true),
                                all(
                                        e -> e.isBaby(),
                                        e -> e.getMemoryStorage().notEmpty(CoreMemoryTypes.PARENT),
                                        e -> e.getMemoryStorage().notEmpty(CoreMemoryTypes.STAY_NEARBY)
                                ),
                                5, 1
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(this.getMovementSpeedDefault() * 1.25f, 18, 8, true, 80, true, 10),
                                all(
                                        e -> e.passengers.isEmpty(),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 80)
                                ),
                                4, 1
                        ),
                        new Behavior(
                                new TemptExecutor(2.5f, TEMPT_ITEMS),
                                all(
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
                                        e -> !((EntityCamel) e).isSitting(),
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> Utils.rand(1, 35) == 1
                                ),
                                2, 1, 200
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(this.getMovementSpeedDefault(), 12, 100, false, -1, true, 10),
                                all(
                                        e -> !((EntityCamel) e).isSitting()
                                ),
                                1, 1
                        )
                )
                .sensors(
                        new FollowEntitySensor(6f, 2f),
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
        if (this.getRider() == null || !this.isSaddled()) {

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

}
