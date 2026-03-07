package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.DiveController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.BreedingExecutor;
import cn.nukkit.entity.ai.executor.LoveTimeoutExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import cn.nukkit.entity.ai.sensor.ISensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.DashActionComponent;
import cn.nukkit.entity.components.EquippableComponent;
import cn.nukkit.entity.components.HealableComponent;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.HomeComponent;
import cn.nukkit.entity.components.InventoryComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.entity.components.TameableComponent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemNautilusArmor;
import cn.nukkit.item.ItemSaddle;
import cn.nukkit.item.ItemShears;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Set;


/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class EntityNautilus extends EntityAnimal implements EntitySwimmable, InventoryHolder {

    @Override
    @NotNull public String getIdentifier() {
        return NAUTILUS;
    }

    public EntityNautilus(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected HorseInventory<EntityNautilus> entityInventory;
    protected boolean charging;

    @Override
    public float getWidth() {
        return 0.875f;
    }

    @Override
    public float getHeight() {
        return 0.95f;
    }

    @Override
    public boolean isRideable() {
        if (this.isBaby()) return false;
        return true;
    }

    @Override
    public @Nullable TameableComponent getComponentTameable() {
        return new TameableComponent(
                0.33f,
                Set.of(
                    ItemID.PUFFERFISH_BUCKET,
                    ItemID.PUFFERFISH
                )
        );
    }

    @Override
    public void onTameSuccess(Player player) {
        this.updateInventoryFlags();
        this.setHomePosition();
        this.getLevel().dropExpOrb(this, Utils.rand(1, 7));

        super.onTameSuccess(player);
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
                1,
                List.of(
                    new RideableComponent.Seat(
                        0,
                        2,
                        new Vector3f( 0.0f, 0.925f, 0.0f),
                        null,
                        null,
                        7.0f,
                        null
                    )
                )
        );
    }

    @Override
    public RideableComponent.InputType getInputControlType() {
        return RideableComponent.InputType.WATER;
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
                        EquippableComponent.Type.NAUTILUS_ARMOR,
                        Set.of(
                                "minecraft:copper_nautilus_armor",
                                "minecraft:iron_nautilus_armor",
                                "minecraft:golden_nautilus_armor",
                                "minecraft:diamond_nautilus_armor",
                                "minecraft:netherite_nautilus_armor"
                        ),
                        null
                )
            ));
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(15);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.15f);
    }

    @Override
    public int getAttackPower() {
        return 3;
    }

    @Override
    public float getDefaultUnderWaterSpeed() {
        if (this.charging) return 0.12f;
        if (this.getRider() != null) return 0.055f;
        return 0.15f;
    }

    @Override
    public @Nullable DashActionComponent getComponentDashAction() {
        return new DashActionComponent(
            true,
            2.0f,
            DashActionComponent.Direction.PASSENGER,
            154.0f,
            0.1f
            );
    }

    @Override
    public float getKnockbackResistance() {
        return 0.3f;
    }

    @Override
    public String getOriginalName() {
        return "Nautilus";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("aquatic", "nautilus", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                Set.of(
                    ItemID.PUFFERFISH_BUCKET,
                    ItemID.COD_BUCKET,
                    ItemID.SALMON_BUCKET,
                    ItemID.TROPICAL_FISH_BUCKET,
                    ItemID.PUFFERFISH,
                    ItemID.COD,
                    ItemID.SALMON,
                    ItemID.TROPICAL_FISH,
                    ItemID.COOKED_COD,
                    ItemID.COOKED_SALMON
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.NAUTILUS, EntityID.NAUTILUS)
                ),
                true
        );
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                List.of(
                    new HealableComponent.Item(ItemID.PUFFERFISH_BUCKET, 2),
                    new HealableComponent.Item(ItemID.COD_BUCKET, 4),
                    new HealableComponent.Item(ItemID.SALMON_BUCKET, 4),
                    new HealableComponent.Item(ItemID.TROPICAL_FISH_BUCKET, 2),
                    new HealableComponent.Item(ItemID.COD, 4),
                    new HealableComponent.Item(ItemID.SALMON, 4),
                    new HealableComponent.Item(ItemID.TROPICAL_FISH, 2),
                    new HealableComponent.Item(ItemID.PUFFERFISH, 2),
                    new HealableComponent.Item(ItemID.COOKED_COD, 10),
                    new HealableComponent.Item(ItemID.COOKED_SALMON, 12)
                )
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                    new AgeableComponent.FeedItem(ItemID.PUFFERFISH_BUCKET),
                    new AgeableComponent.FeedItem(ItemID.COD_BUCKET),
                    new AgeableComponent.FeedItem(ItemID.SALMON_BUCKET),
                    new AgeableComponent.FeedItem(ItemID.TROPICAL_FISH_BUCKET),
                    new AgeableComponent.FeedItem(ItemID.PUFFERFISH),
                    new AgeableComponent.FeedItem(ItemID.COD),
                    new AgeableComponent.FeedItem(ItemID.SALMON),
                    new AgeableComponent.FeedItem(ItemID.TROPICAL_FISH),
                    new AgeableComponent.FeedItem(ItemID.COOKED_COD),
                    new AgeableComponent.FeedItem(ItemID.COOKED_SALMON)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public @Nullable HomeComponent getComponentHome() {
        return new HomeComponent(true, null, null, null);
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
    public HorseInventory<EntityNautilus> getInventory() {
        ensureInventories();
        return entityInventory;
    }

    protected void ensureInventories() {
        if (this.entityInventory == null) this.entityInventory = new HorseInventory<>(this, getComponentInventory().size());
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setDataFlag(EntityFlag.CAN_SWIM, true, false);
        this.setDataFlag(EntityFlag.CAN_WALK, false, false);
        this.setDataFlag(EntityFlag.HAS_GRAVITY, true, false);
        this.setDataProperty(IS_BUOYANT, false, true);

        // Load items
        ensureInventories();
        if (namedTag.containsList("Inventory")) {
            entityInventory.load(namedTag.getList("Inventory", CompoundTag.class));
        }

        // Init home memory
        this.initHome();
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
        namedTag.putList("Inventory", inv.save(false));
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        return new Item[] {
                Item.get(Item.NAUTILUS_SHELL, 0, Utils.rand(0, 100) < 5 + looting ? 1 : 0)
        };
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (!item.isNull()) {
            // Saddle interaction
            if ((item instanceof ItemSaddle saddle) && this.isTamed() && !this.isSaddled()) {
                        this.getInventory().setEquippedItem(EquippableComponent.Type.SADDLE, saddle);
                        this.setHomePosition();
                        return true;

            // Armor interaction
            } else if ((item instanceof ItemNautilusArmor armor) && this.isTamed() && this.getInventory().getEquippedItem(EquippableComponent.Type.NAUTILUS_ARMOR).isNull()) {
                        this.getInventory().setEquippedItem(EquippableComponent.Type.NAUTILUS_ARMOR, armor);
                        return true;

            // Shears interaction
            } else if (item instanceof ItemShears
                        && (!this.getInventory().getEquippedItem(EquippableComponent.Type.NAUTILUS_ARMOR).isNull()
                            || !this.getInventory().getEquippedItem(EquippableComponent.Type.SADDLE).isNull())) {
                
                Item armor = this.getInventory().getEquippedItem(EquippableComponent.Type.NAUTILUS_ARMOR);
                Item saddle = this.getInventory().getEquippedItem(EquippableComponent.Type.SADDLE);

                if (!armor.isNull()) {
                    if (player.getInventory().canAddItem(armor)) {
                        player.getInventory().addItem(armor);
                    } else {
                        this.getLevel().dropItem(clickedPos, armor);
                    }
                    this.getInventory().setEquippedItem(EquippableComponent.Type.NAUTILUS_ARMOR, null);
                    this.getInventory().sendEquippedVisualsTo(this.getViewers().values());
                    return true;
                }
                if (!saddle.isNull()) {
                    if (player.getInventory().canAddItem(saddle)) {
                        player.getInventory().addItem(saddle);
                    } else {
                        this.getLevel().dropItem(clickedPos, saddle);
                    }
                    this.getInventory().setEquippedItem(EquippableComponent.Type.SADDLE, null);
                    this.getInventory().sendEquippedVisualsTo(this.getViewers().values());
                    this.setHomePosition();
                    return true;
                }
            }
        }

        if (isTamed()) mountEntity(player, true);
        return false;
    }

    @Override
    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        boolean result = super.dismountEntity(entity, sendLinks);

        this.setHomePosition();
        return result;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && source.getDamage() >= 2f && this.isHalfBodyInWater()) {
            source.setCancelled(true);
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent event) {
            Entity attacker = event.getDamager();
            if (attacker != null && !attacker.isClosed()) this.setRetaliate(event.getDamager());
        }
        return super.attack(source);
    }

    public int roamDistance() {
        if (this.isTamed() && this.isSaddled()) return 16;
        if (this.isTamed() && !this.isSaddled()) return 32;
        return 64;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setRetaliate(Entity entity) {
        getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity);
    }

    public boolean hasAttackTarget() {
        return getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) != null;
    }

    private boolean isHalfBodyInWater() {
        final int bx = (int) Math.floor(this.x);
        final int bz = (int) Math.floor(this.z);

        int yMin = (int) Math.floor(this.y) - 2;
        int yMax = (int) Math.floor(this.y + this.getHeight()) + 2;

        int topWaterBlockY = Integer.MIN_VALUE;
        for (int y = yMax; y >= yMin; y--) {
            Block b = this.level.getBlock(bx, y, bz);
            if (b instanceof BlockFlowingWater) {
                topWaterBlockY = y;
                break;
            }
        }

        if (topWaterBlockY == Integer.MIN_VALUE) return false;

        double surfaceY = topWaterBlockY + 1.0d;
        double maxFeetY = surfaceY - (this.getHeight() * 0.50d);
        final double eps = 0.05d;

        return this.y <= (maxFeetY + eps);
    }

    public float getEnvironmentalMoveSpeed() {
        if (!this.isInsideOfWater()) return getMovementSpeedDefault();
        return this.getDefaultUnderWaterSpeed();
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        ItemID.PUFFERFISH_BUCKET,
        ItemID.COD_BUCKET,
        ItemID.SALMON_BUCKET,
        ItemID.TROPICAL_FISH_BUCKET,
        ItemID.PUFFERFISH,
        ItemID.COD,
        ItemID.SALMON,
        ItemID.TROPICAL_FISH,
        ItemID.COOKED_COD,
        ItemID.COOKED_SALMON
    );

    // TODO: Reatliate when attacked
    // TODO: setHomePostiion on_unleashed
    // TODO: setHomePosition on_stop_tempting
    // TODO: Create dash attack behavior (retaliate)
    @Override
    public IBehaviorGroup requireBehaviorGroup() {
            return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                    new Behavior(
                        new LoveTimeoutExecutor(20 * 30),
                            e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                        4, 1
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
                    new Behavior( // RETURN HOME if too far
                        new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, this.getEnvironmentalMoveSpeed() * 8f, true),
                        entity -> {
                            EntityNautilus n = (EntityNautilus) entity;

                            if (!n.isTamed()) return false;
                            if (n.getRider() != null) return false;

                            if (entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)) return false;

                            Block home = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK);
                            if (home == null) return false;

                            double max = n.roamDistance();
                            return entity.distanceSquared(home) > (max * max);
                        },
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
                    new Behavior( // ROAM freely (not tamed)
                        new SpaceRandomRoamExecutor(getEnvironmentalMoveSpeed(), 64, 16, 80, false, -1, false, 10),
                        entity -> {
                            EntityNautilus n = (EntityNautilus) entity;
                            return !n.isTamed();
                        },
                        2
                    ),
                    new Behavior( // ROAM but only while inside home radius (tamed only)
                        new SpaceRandomRoamExecutor(getEnvironmentalMoveSpeed() * 3, roamDistance(), 16, 80, false, -1, false, 10),
                        entity -> {
                            EntityNautilus n = (EntityNautilus) entity;

                            if (entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)) return false;
                            Block home = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK);
                            if (home == null) return false;

                            double max = n.roamDistance();
                            return entity.distanceSquared(home) <= (max * max);
                        },
                        1
                    )
                ),
                Set.of(
                    new ISensor() {
                        @Override
                        public void sense(EntityIntelligent entity) {
                            EntityNautilus n = (EntityNautilus) entity;

                            if (!n.isTamed()) return;
                            Block home = n.getHomePosition();
                            if (home == null) return;
                            entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_BLOCK, home);
                        }

                        @Override
                        public int getPeriod() {
                            return 60;
                        }
                    }
                ),
                Set.of(
                    new SpaceMoveController(),
                    new LookController(true, true),
                    new DiveController()
                ),
                new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this),
                this
        );
    }

    @Override
    public void asyncPrepare(int currentTick) {
        if (this.getRider() == null || !this.isTamed() || !this.isSaddled()) {
            super.asyncPrepare(currentTick);
        }
    }

}
