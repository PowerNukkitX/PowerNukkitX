package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.DiveController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.executor.MoveToRiderTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import cn.nukkit.entity.ai.sensor.ISensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.EquippableComponent;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemNautilusArmor;
import cn.nukkit.item.ItemSaddle;
import cn.nukkit.item.ItemShears;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.ItemHelper;
import cn.nukkit.utils.Utils;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class EntityZombieNautilus extends EntityNautilus {

    @Override
    @NotNull
    public String getIdentifier() {
        return ZOMBIE_NAUTILUS;
    }

    public EntityZombieNautilus(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    private static final String NBT_RIDEABLE_TYPE = "JockeyType";
    private static final String NBT_RIDER_SPAWNED = "JockeyRiderSpawned";

    private boolean pendingJockeySpawn;
    private SpawnRiderType jockeyType;

    private enum SpawnRiderType {
        NORMAL(0),
        DROWNED_JOCKEY(1);

        private final int id;

        SpawnRiderType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static SpawnRiderType fromId(int id) {
            return switch (id) {
                case 1 -> DROWNED_JOCKEY;
                default -> NORMAL;
            };
        }
    }

    @Override
    public RideableComponent getComponentRideable() {
        boolean crounchingSkipInteract = this.isTamed() ? true : false;
        String interactText = crounchingSkipInteract ? "action.interact.ride.horse" : null;
        Set<String> ridersFamily = crounchingSkipInteract ? Set.of("player") : Set.of("drowned");
        float yOffset = crounchingSkipInteract ? 0.925f : 1.0f;
        float zOffset = crounchingSkipInteract ? 0f : -0.2f;

        return new RideableComponent(
                0,
                crounchingSkipInteract,
                RideableComponent.DismountMode.DEFAULT,
                ridersFamily,
                interactText,
                0.0f,
                true,
                false,
                1,
                List.of(
                        new RideableComponent.Seat(
                                0,
                                2,
                                new Vector3f(0.0f, yOffset, zOffset),
                                null,
                                null,
                                7.0f,
                                null
                        )
                )
        );
    }

    @Override
    public String getOriginalName() {
        return "Zombie Nautilus";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombie_nautilus", "zombie", "undead", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return null;
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return null;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && source.getDamage() >= 2f) {
            source.setCancelled(true);
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent event) {
            Entity attacker = event.getDamager();
            if (attacker != null && !attacker.isClosed()) this.setRetaliate(event.getDamager());
        }
        return super.attack(source);
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (this.namedTag != null && this.namedTag.containsKey(NBT_RIDEABLE_TYPE)) {
            this.jockeyType = SpawnRiderType.fromId(this.namedTag.getInt(NBT_RIDEABLE_TYPE));
        } else {
            this.jockeyType = rollInitialRideableType();
            if (this.namedTag != null) {
                this.namedTag = this.namedTag.toBuilder().putInt(NBT_RIDEABLE_TYPE, this.jockeyType.getId()).build();
            }
        }

        boolean riderSpawned = this.namedTag != null && this.namedTag.getBoolean(NBT_RIDER_SPAWNED);
        if (!riderSpawned && (this.jockeyType == SpawnRiderType.DROWNED_JOCKEY)) {
            this.pendingJockeySpawn = true;
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean updated = super.onUpdate(currentTick);

        if (this.pendingJockeySpawn) {
            this.pendingJockeySpawn = false;
            spawnPendingRider();
        }

        return updated;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[]{
                Item.get(Item.ROTTEN_FLESH, 0, Utils.rand(0, 3))
        };
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.hasControllingPassenger()) return false;

        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        // Tame interaction
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

    private SpawnRiderType rollInitialRideableType() {
        // Weights:
        //  - 10% normal
        //  - 90% drowned jockey
        int r = ThreadLocalRandom.current().nextInt(100);

        if (r < 10) return SpawnRiderType.NORMAL;
        return SpawnRiderType.DROWNED_JOCKEY;
    }

    private void spawnPendingRider() {
        if (this.closed) return;
        if (this.namedTag != null && this.namedTag.getBoolean(NBT_RIDER_SPAWNED)) return;

        if (!this.passengers.isEmpty()) {
            if (this.namedTag != null)
                this.namedTag = this.namedTag.toBuilder().putBoolean(NBT_RIDER_SPAWNED, true).build();
            return;
        }

        Entity rider = null;
        if (this.jockeyType == SpawnRiderType.DROWNED_JOCKEY) {
            rider = createRiderEntity(Entity.DROWNED);
        }

        if (rider == null) return;

        rider.spawnToAll();
        this.mountEntity(rider, true);

        if (this.namedTag != null)
            this.namedTag = this.namedTag.toBuilder().putBoolean(NBT_RIDER_SPAWNED, true).build();
    }

    private @Nullable Entity createRiderEntity(String entityId) {
        NbtMapBuilder nbt = Entity.getDefaultNBT(this.getLocation()).toBuilder();

        if (this.jockeyType == SpawnRiderType.DROWNED_JOCKEY) {
            Item trident = Item.get(Item.TRIDENT, 0, 1);
            nbt.putCompound("Mainhand", ItemHelper.write(trident));
        }

        Entity rider = Entity.createEntity(entityId, this.getChunk(), nbt.build());
        if (rider == null) return null;
        return rider;
    }

    public boolean isRiddenByMob() {
        return this.getRider() != null && !(this.getRider() instanceof Player);
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
            ItemID.PUFFERFISH,
            ItemID.PUFFERFISH_BUCKET
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                ),
                Set.of(
                        new Behavior(
                                new MoveToRiderTargetExecutor(this.getEnvironmentalMoveSpeed() * 2.00f, true),
                                e -> this.isRiddenByMob(),
                                7, 1
                        ),
                        new Behavior(
                                new TemptExecutor(1.2f, TEMPT_ITEMS),
                                all(
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                                ),
                                4, 1
                        ),
                        new Behavior( // RETURN HOME if too far
                                new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, this.getEnvironmentalMoveSpeed() * 8f, true),
                                entity -> {
                                    EntityNautilus n = (EntityNautilus) entity;

                                    if (!n.isTamed()) return false;
                                    if (n.hasControllingPassenger()) return false;

                                    if (entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)) return false;

                                    Block home = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK);
                                    if (home == null) return false;

                                    double max = n.roamDistance();
                                    return entity.distanceSquared(home) > (max * max);
                                },
                                3, 1
                        ),
                        new Behavior( // ROAM freely (not tamed)
                                new SpaceRandomRoamExecutor(getEnvironmentalMoveSpeed(), 64, 16, 80, false, -1, false, 10),
                                entity -> {
                                    EntityNautilus n = (EntityNautilus) entity;
                                    return !n.isTamed() && !n.hasControllingPassenger();
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

}
