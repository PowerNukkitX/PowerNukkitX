package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Attribute;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityMarkVariant;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.RideableTameExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.EquippableComponent;
import org.powernukkitx.entity.components.HealableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.HorseJumpStrengthComponent;
import org.powernukkitx.entity.components.InventoryComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.inventory.HorseInventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityHorse extends EntityAnimal implements EntityWalkable, EntityVariant, EntityMarkVariant, InventoryHolder {
    @Override
    @NotNull
    public String getIdentifier() {
        return HORSE;
    }

    public EntityHorse(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private static final int[] VARIANTS = {0, 1, 2, 3, 4, 5, 6};
    private static final int[] MARK_VARIANTS = {0, 1, 2, 3, 4};
    private HorseInventory<EntityHorse> entityInventory;

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.7f;
        }
        return 1.4f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.8f;
        }
        return 1.6f;
    }

    @Override
    public boolean isEquine() {
        return true;
    }

    @Override
    public boolean isRideable() {
        return !this.isBaby();
    }

    @Override
    public @Nullable RideableComponent getComponentRideable() {
        boolean crounchingSkipInteract = this.isTamed();
        Set<String> riders = crounchingSkipInteract ? Set.of("player") : Set.of("player", "baby_zombie", "baby_husk");

        return new RideableComponent(
                0,
                crounchingSkipInteract,
                RideableComponent.DismountMode.DEFAULT,
                riders,
                "action.interact.mount",
                0.0f,
                false,
                false,
                1,
                List.of(
                        new RideableComponent.Seat(
                                0,
                                1,
                                new Vector3f(0.0f, 1.1f, -0.2f),
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
                ),
                new EquippableComponent.Slot(
                        1,
                        EquippableComponent.Type.HORSE_ARMOR,
                        Set.of(
                                ItemID.LEATHER_HORSE_ARMOR,
                                ItemID.IRON_HORSE_ARMOR,
                                ItemID.GOLDEN_HORSE_ARMOR,
                                ItemID.DIAMOND_HORSE_ARMOR,
                                ItemID.COPPER_HORSE_ARMOR,
                                ItemID.NETHERITE_HORSE_ARMOR
                        ),
                        null
                )
        ));
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.range(15, 30);
    }

    @Override
    public @Nullable HorseJumpStrengthComponent getComponentHorseJumpStrength() {
        return HorseJumpStrengthComponent.range(0.4f, 1.0f);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.range(0.1125f, 0.3375f);
    }

    @Override
    protected double getStepHeight() {
        return 1.0625f;
    }

    @Override
    protected double getStepHeightControlled() {
        return 1.0625f;
    }

    @Override
    protected double getStepHeightJumpPrevented() {
        return 0.5625;
    }

    @Override
    public String getOriginalName() {
        return "Horse";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("horse", "mob");
    }

    @Override
    public int randomVariant() {
        return getAllVariant()[new Random(System.currentTimeMillis()).nextInt(getAllVariant().length)];
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public int[] getAllMarkVariant() {
        return MARK_VARIANTS;
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                BreedableComponent.blendAttributesOf(
                        Attribute.HEALTH,
                        Attribute.MOVEMENT_SPEED,
                        Attribute.HORSE_JUMP_STRENGTH
                ),
                null,
                Set.of(
                        ItemID.GOLDEN_CARROT,
                        ItemID.GOLDEN_APPLE,
                        ItemID.ENCHANTED_GOLDEN_APPLE
                ),
                List.of(
                        new BreedableComponent.BreedsWith(EntityID.HORSE, EntityID.HORSE),
                        new BreedableComponent.BreedsWith(EntityID.DONKEY, EntityID.MULE)
                ),
                null,
                null,
                null,
                null,
                false,
                new BreedableComponent.MutationFactor(0.111f, 0.2f, null),
                null,
                null,
                true,
                null
        );
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                List.of(
                        new HealableComponent.Item(BlockID.WHEAT, 2),
                        new HealableComponent.Item(BlockID.HAY_BLOCK, 20),
                        new HealableComponent.Item(ItemID.SUGAR, 1),
                        new HealableComponent.Item(ItemID.APPLE, 3),
                        new HealableComponent.Item(ItemID.CARROT, 3),
                        new HealableComponent.Item(ItemID.GOLDEN_CARROT, 4),
                        new HealableComponent.Item(ItemID.GOLDEN_APPLE, 10),
                        new HealableComponent.Item(ItemID.ENCHANTED_GOLDEN_APPLE, 10)
                )
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                        new AgeableComponent.FeedItem(BlockID.WHEAT, 0.016667f),
                        new AgeableComponent.FeedItem(BlockID.HAY_BLOCK, 0.15f),
                        new AgeableComponent.FeedItem(ItemID.SUGAR, 0.025f),
                        new AgeableComponent.FeedItem(ItemID.APPLE, 0.05f),
                        new AgeableComponent.FeedItem(ItemID.CARROT, 0.05f),
                        new AgeableComponent.FeedItem(ItemID.GOLDEN_CARROT, 0.05f),
                        new AgeableComponent.FeedItem(ItemID.GOLDEN_APPLE, 0.2f),
                        new AgeableComponent.FeedItem(ItemID.ENCHANTED_GOLDEN_APPLE, 0.2f)
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
                2,
                false,
                false
        );
    }

    @Override
    public HorseInventory<EntityHorse> getInventory() {
        ensureInventories();
        return entityInventory;
    }

    private void ensureInventories() {
        if (this.entityInventory == null)
            this.entityInventory = new HorseInventory<>(this, getComponentInventory().size());
    }

    @Override
    public void initEntity() {
        super.initEntity();

        // Load items
        ensureInventories();
        if (nbt.containsList("Inventory")) {
            entityInventory.load(getNbt().getList("Inventory", CompoundTag.class).getAll());
        }

        if (!hasVariant()) {
            this.setVariant(randomVariant());
        }
        if (!hasMarkVariant()) {
            this.setMarkVariant(randomMarkVariant());
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
        this.nbt.putList("Inventory", inv.save(isChested()));
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        ArrayList<Item> drops = new ArrayList<>();
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 2) != 0) {
            int leatherAmount = Utils.rand(0, 2 + looting);
            if (leatherAmount > 0) {
                drops.add(Item.get(Item.LEATHER, 0, leatherAmount));
            }
        }

        // Drop Ride Inventory
        ensureInventories();
        drops.addAll(Arrays.asList(HorseInventory.getInventoryDrops(getInventory(), this)));

        if (drops.isEmpty()) return Item.EMPTY_ARRAY;
        return drops.toArray(new Item[0]);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean b = super.onUpdate(currentTick);

        if (currentTick % 2 == 0 && getRideJumping() != null && currentTick - getRideJumping().get() > 5 && this.isOnGround()) {
            this.setDataFlag(ActorFlags.STANDING, false);
            this.rideJumping.set(-1);
        }
        return b;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (this.isBaby()) return false;

        mountEntity(player, true);
        return false;
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
            ItemID.GOLDEN_APPLE,
            ItemID.ENCHANTED_GOLDEN_APPLE,
            ItemID.GOLDEN_CARROT
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(
                                new LoveTimeoutExecutor(20 * 30),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                3, 1
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
                ),
                Set.of(
                        new Behavior(
                                new BreedingExecutor(16, 200, 0.25f),
                                all(
                                        e -> !e.isBaby(),
                                        e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                                ),
                                5, 1
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.55f, 18, 8, true, 80, true, 10),
                                all(
                                        e -> !e.isTamed(),
                                        e -> e.passengers.isEmpty(),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 80)
                                ),
                                4, 1
                        ),
                        new Behavior(
                                new RideableTameExecutor(0.4f, 12, 40, true, 100, true, 10, 35),
                                all(
                                        new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.RIDER_NAME),
                                        e -> !this.hasOwner(false)
                                ),
                                3, 1
                        ),
                        new Behavior(
                                new TemptExecutor(1.2f, TEMPT_ITEMS),
                                all(
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                                ),
                                2, 1
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
                                1, 1, 100
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10),
                                (entity -> true),
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
        if (this.getRider() == null || !this.isTamed() || !this.isSaddled()) {
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
