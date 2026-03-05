package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.BreedingExecutor;
import cn.nukkit.entity.ai.executor.RideableTameExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.LoveTimeoutExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.EquippableComponent;
import cn.nukkit.entity.components.HealableComponent;
import cn.nukkit.entity.components.InventoryComponent;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.entity.components.utils.AttributesFloatRange;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EntityLlama extends EntityAnimal implements EntityWalkable, InventoryHolder {
    @Override
    @NotNull public String getIdentifier() {
        return LLAMA;
    }

    public EntityLlama(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private HorseInventory<EntityLlama> invNoChest;
    private HorseInventory<EntityLlama> invChested;
    private int llamaStrength;

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public boolean isRideable() {
        if (this.isBaby()) return false;
        return true;
    }

    @Override
    public @Nullable RideableComponent getRideableData() {
        boolean controllable = this.isTamed();
        return new RideableComponent(
            0,
            controllable,
            RideableComponent.DismountMode.DEFAULT,
            Set.of("player"),
            "action.interact.ride.horse",
            0.0f,
            false,
            false,
            1,
            List.of(new RideableComponent.Seat(
                0, 1,
                new Vector3f(0.0f, 1.17f, -0.3f),
                null, null, null, null
            ))
        );
    }

    @Override
    public boolean canBeChested() {
        return true;
    }

    @Override
    public @Nullable EquippableComponent getEquippableData() {
        return new EquippableComponent(List.of(
                    new EquippableComponent.Slot(
                        1,
                        EquippableComponent.Type.CARPET,
                        Set.of(
                            BlockID.WHITE_CARPET,
                            BlockID.ORANGE_CARPET,
                            BlockID.MAGENTA_CARPET,
                            BlockID.LIGHT_BLUE_CARPET,
                            BlockID.YELLOW_CARPET,
                            BlockID.LIME_CARPET,
                            BlockID.PINK_CARPET,
                            BlockID.GRAY_CARPET,
                            BlockID.LIGHT_GRAY_CARPET,
                            BlockID.CYAN_CARPET,
                            BlockID.PURPLE_CARPET,
                            BlockID.BLUE_CARPET,
                            BlockID.BROWN_CARPET,
                            BlockID.GREEN_CARPET,
                            BlockID.RED_CARPET,
                            BlockID.BLACK_CARPET
                        ),
                        null
                    )
                ));
    }

    @Override
    public @Nullable AttributesFloatRange getHealthRange() {
        return new AttributesFloatRange(15f, 30f);
    }

    @Override
    public float getDefaultSpeed() {
        return 0.25f;
    }

    @Override
    public String getOriginalName() {
        return "LLama";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("llama", "mob");
    }

    @Override
    public @Nullable BreedableComponent getBreedable() {
        return new BreedableComponent(
                null,
                null,
                BreedableComponent.blendAttributesOf(
                    Attribute.MAX_HEALTH
                ),
                null,
                Set.of(
                    BlockID.HAY_BLOCK
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.LLAMA, EntityID.LLAMA)
                ),
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                true,
                null
        );
    }

    @Override
    public HealableComponent getHealable() {
        return new HealableComponent(
                List.of(
                    new HealableComponent.Item(BlockID.WHEAT, 2),
                    new HealableComponent.Item(BlockID.HAY_BLOCK, 10)
                )
        );
    }

    @Override
    public AgeableComponent getAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                    new AgeableComponent.FeedItem(BlockID.WHEAT, 0.016667f),
                    new AgeableComponent.FeedItem(BlockID.HAY_BLOCK, 0.9f)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public @Nullable InventoryComponent getInventoryComponent() {
        return new InventoryComponent(
                3,
                false,
                InventoryComponent.Type.HORSE,
                16,
                false,
                false
        );
    }

    /** This sync inventories when entity have more than one (with or without chest for instance) */
    protected void syncEquippableInventories() {
        ensureInventories();
        HorseInventory<EntityLlama> src = getInventory();
        src.syncEquippedTo(invNoChest);
        src.syncEquippedTo(invChested);
    }

    protected void ensureInventories() {
        EquippableComponent eq = this.getEquippableData();
        int equipCount = (eq != null) ? eq.getEquipCount() : 0;

        if (this.invNoChest == null) this.invNoChest = new HorseInventory<>(this, equipCount);

        if (this.invChested == null) {
            int storageSlots = this.llamaStrength * 3;
            this.invChested = new HorseInventory<>(this, equipCount + storageSlots);
        }
    }

    @Override
    public void updateInventoryFlags() {
        this.setDataProperty(Entity.CONTAINER_TYPE, 12);
        this.setDataProperty(Entity.CONTAINER_SIZE, 16);
        this.setDataProperty(Entity.CONTAINER_STRENGTH_MODIFIER, 3);
        this.setDataProperty(Entity.STRENGTH, this.llamaStrength);
        this.setDataProperty(Entity.STRENGTH_MAX, 5);
    }

    @Override
    public HorseInventory<EntityLlama> getInventory() {
        ensureInventories();
        return this.isChested() ? this.invChested : this.invNoChest;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("LlamaStrength")) {
            this.llamaStrength = this.namedTag.getInt("LlamaStrength");
        } else {
            this.llamaStrength = ThreadLocalRandom.current().nextInt(1, 6);
            this.namedTag.putInt("LlamaStrength", this.llamaStrength);
        }

        // Load items
        ensureInventories();
        if (namedTag.containsList("Inventory")) {
            var inv = isChested() ? invChested : invNoChest;
            inv.load(namedTag.getList("Inventory", CompoundTag.class));
            syncEquippableInventories();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        var inv = isChested() ? invChested : invNoChest;
        syncEquippableInventories();
        namedTag.putBoolean("Chested", isChested());
        namedTag.putList("Inventory", inv.save(isChested()));
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        if (this.isBaby()) return Item.EMPTY_ARRAY;

        ArrayList<Item> drops = new ArrayList<>();
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 2) != 0) {
            int amount = Utils.rand(0, 2 + looting);
            if (amount > 0) {
                drops.add(Item.get(Item.LEATHER, 0, amount));
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
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (this.isBaby()) return false;

        if (!item.isNull() && this.isTamed()) {
            // 1) Add chest
            Item chestItem = Block.get(Block.CHEST).toItem();
            if (item.getId() == chestItem.getId() && this.canBeChested() && !this.isChested()) {
                // set chested + refresh inventories
                this.setChest(true);
                syncEquippableInventories();
                updateInventoryFlags();
                return true;
            }
        }

        // 2) Sneak -> open inventory
        if (player.isSneaking() && this.isTamed() && this.isChested()) {
            this.openInventory(player);
            return false;
        }

        // 3) Default: mount
        mountEntity(player, true);
        return false;
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        BlockID.HAY_BLOCK
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
                        new BreedingExecutor(16, 200, 0.25f),
                            all(
                                e -> !e.isBaby(),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                            ),
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
                ),
                Set.of(
                    new Behavior(
                        new FlatRandomRoamExecutor(0.55f, 18, 8, true, 80, true, 10),
                            all(
                                e -> !e.isTamed(),
                                e -> e.passengers.isEmpty(),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 80)
                            ),
                        5, 1
                    ),
                    new Behavior(
                        new RideableTameExecutor(0.4f, 12, 40, true, 100, true, 10, 35),
                            all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.RIDER_NAME),
                                e -> !this.hasOwner(false)
                            ),
                        4, 1
                    ),
                    new Behavior(
                        new TemptExecutor(1.2f, TEMPT_ITEMS),
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
                        1, 1, 100
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10),
                            (entity -> true),
                        1, 1
                    )
                ),
                Set.of(
                    new NearestPlayerSensor(8, 0, 20)),
                Set.of(
                    new WalkController(),
                    new LookController(true, true),
                    new FluctuateController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
