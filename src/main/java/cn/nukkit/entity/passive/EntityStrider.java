package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.evaluator.RiderItemControllableEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.BreedingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FollowRiderExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.LoveTimeoutExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.StriderMoveToLavaExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.FollowEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.StriderLavaSensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BoostableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Erik Miller | EinBexiii, Curse
 */
public class EntityStrider extends EntityAnimal implements EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return STRIDER;
    }

    public EntityStrider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    private static final String NBT_RIDEABLE_TYPE = "JockeyType";
    private static final String NBT_RIDER_SPAWNED = "JockeyRiderSpawned";
    private static final int LAVA_SEEK_DELAY_TICKS = 20 * 10;

    private boolean pendingJockeySpawn;
    private SpawnRiderType jockeyType;
    public int nextLavaSeekTick = 0;
    private int coldSinceTick = -1;

    private enum SpawnRiderType {
        NORMAL(0),         // unsaddled, no special mounts
        PIGLIN_JOCKEY(1),  // spawned as jockey -> must be saddled, but NOT player-controlled saddle
        PARENT_JOCKEY(2),  // spawned as parent stack mount (strider)
        PLAYER(3);         // player saddled it -> player mount (controllable)

        private final int id;

        SpawnRiderType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static SpawnRiderType fromId(int id) {
            return switch (id) {
                case 1 -> PIGLIN_JOCKEY;
                case 2 -> PARENT_JOCKEY;
                case 3 -> PLAYER;
                default -> NORMAL;
            };
        }
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    @Override
    public boolean isRideable() {
        if (this.isBaby()) return false;
        return true;
    }

    @Override
    public boolean requireSaddleToMount() {
        return true;
    }

    @Override
    public @Nullable RideableComponent getComponentRideable() {
        boolean saddled = this.isSaddled();

        Set<String> riders;
        String interactText;
        Vector3f seatPos;

        if (saddled) {
            interactText = "action.interact.ride.strider";
            seatPos = new Vector3f(0.0f, 1.7f, -0.2f);

            riders = switch (this.jockeyType) {
                case PLAYER -> Set.of("player");
                case PIGLIN_JOCKEY -> Set.of("player", "zombie_pigman");
                default -> null;
            };
        } else {
            riders = (this.jockeyType == SpawnRiderType.PARENT_JOCKEY) ? Set.of("strider") : null;
            interactText = "";
            seatPos = new Vector3f(0.0f, 1.7f, 0.0f);
        }

        if (riders == null) return null;

        return new RideableComponent(
                0,
                true,
                RideableComponent.DismountMode.DEFAULT,
                riders,
                interactText,
                0.0f,
                false,
                false,
                1,
                List.of(new RideableComponent.Seat(
                        0,
                        1,
                        seatPos,
                        null,
                        null,
                        null,
                        null
                ))
        );
    }

    @Override
    public Vector3f getMountedOffset(Entity passenger) {
        if (passenger instanceof Player) {
            float standingEyeHeight = passenger.getHeight() * 0.9f;
            float mountedOffsetY = standingEyeHeight * 0.75308642f;
            return new Vector3f(0f, mountedOffsetY - 0.13f, 0f); // Player needs to shift a bit down
        } else if (passenger instanceof EntityStrider) {
            return new Vector3f(0f, 0f, 0f); // Strider baby dont have sitting animation, so its base must be zero
        }
        float mountedOffsetY = -passenger.getHeight() * 0.24691358f;
        return new Vector3f(0f, mountedOffsetY - 0.13f, 0f); // Regular mobs needs to shift a bit down
    }

    @Override
    public boolean canBeSaddled() {
        return true;
    }

    @Override
    public String getItemControllable() {
        return Item.WARPED_FUNGUS_ON_A_STICK;
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        float envMovement = isWarm() ? getDefaultLavaMovementSpeed() : 0.16f;
        return MovementComponent.value(envMovement);
    }

    @Override
    public float getDefaultLavaMovementSpeed() {
        return 0.32f;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public String getOriginalName() {
        return "Strider";
    }

    @Override
    public Set<String> typeFamily() {
        if (this.isBaby()) return Set.of("strider", "strider_baby", "mob");
        return Set.of("strider", "strider_adult", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                Set.of(
                    BlockID.WARPED_FUNGUS
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.STRIDER, EntityID.STRIDER)
                ),
                false
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                    new AgeableComponent.FeedItem(BlockID.WARPED_FUNGUS)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public @Nullable BoostableComponent getComponentBoostable() {
        return new BoostableComponent(
            1.35f,
            16.0f,
            List.of(
                    new BoostableComponent.BoostItem(
                        ItemID.WARPED_FUNGUS_ON_A_STICK,
                        1,
                        ItemID.FISHING_ROD
                    )
            )
        );
    }

    @Override
    public int getFrostbiteInjury() {
        return 5;
    }

    private SpawnRiderType rollInitialRideableType() {
        // Weights:
        //  - 10% piglin jockey (saddled)
        //  -  5% parent jockey
        //  - 85% normal
        int r = ThreadLocalRandom.current().nextInt(100);

        if (r < 10) return SpawnRiderType.PIGLIN_JOCKEY;
        if (r < 15) return SpawnRiderType.PARENT_JOCKEY;
        return SpawnRiderType.NORMAL;
    }

    public SpawnRiderType getRideableType() {
        return jockeyType;
    }

    public void setRideableType(SpawnRiderType type) {
        this.jockeyType = (type == null ? SpawnRiderType.NORMAL : type);
        if (this.namedTag != null) {
            this.namedTag.putInt(NBT_RIDEABLE_TYPE, this.jockeyType.getId());
        }
    }

    private void spawnPendingRider() {
        if (this.closed || this.level == null || this.isBaby()) return;

        if (this.namedTag != null && this.namedTag.getBoolean(NBT_RIDER_SPAWNED)) return;

        if (!this.passengers.isEmpty()) {
            if (this.namedTag != null) this.namedTag.putBoolean(NBT_RIDER_SPAWNED, true);
            return;
        }

        Entity rider = null;

        if (this.jockeyType == SpawnRiderType.PIGLIN_JOCKEY) {
            rider = createRiderEntity(Entity.ZOMBIE_PIGMAN);
        } else if (this.jockeyType == SpawnRiderType.PARENT_JOCKEY) {
            rider = createRiderEntity(STRIDER);
            if (rider instanceof EntityStrider s) {
                s.setBaby(true);
            }
        }

        if (rider == null) return;

        rider.spawnToAll();
        this.mountEntity(rider, true);

        if (this.namedTag != null) this.namedTag.putBoolean(NBT_RIDER_SPAWNED, true);
    }

    private @Nullable Entity createRiderEntity(String entityId) {
        CompoundTag nbt = Entity.getDefaultNBT(this.getLocation());

        if (this.jockeyType == SpawnRiderType.PIGLIN_JOCKEY) {
            Item stick = Item.get(Item.WARPED_FUNGUS_ON_A_STICK, 0, 1);
            nbt.put("Mainhand", NBTIO.putItemHelper(stick));
        }

        Entity rider = Entity.createEntity(entityId, this.getChunk(), nbt);
        if (rider == null) return null;
        return rider;
    }

    public boolean isWarm() {
        return this.isInsideOfLava() || (this.riding != null && this.riding.isInsideOfLava());
    }

    private boolean isShaking() {
        return this.getDataFlag(EntityFlag.SHAKING);
    }

    private void setShaking(boolean shaking) {
        if (isShaking() == shaking) return;
        this.setDataFlag(EntityFlag.SHAKING, shaking);
    }

    private void tickColdState() {
        setShaking(!isWarm());
    }

    private void tickWaterContactDamage() {
        if (!this.isAlive()) return;

        if (this.isTouchingWater()) {
            this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.DROWNING, 1.0f));
        }
    }

    private void tickWarmTimers() {
        int tick = this.getServer().getTick();

        if (isWarm()) {
            coldSinceTick = -1;
            nextLavaSeekTick = 0;

            if (this instanceof EntityIntelligent ei) {
                ei.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_BLOCK);
            }
            return;
        }

        if (coldSinceTick < 0) {
            coldSinceTick = tick;
            nextLavaSeekTick = tick + LAVA_SEEK_DELAY_TICKS;
        }
    }

    public boolean shouldReturnToLavaNow() {
        if (isWarm()) return false;

        int tick = this.getServer().getTick();

        if (coldSinceTick < 0) return false;

        return tick >= (coldSinceTick + LAVA_SEEK_DELAY_TICKS);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag != null && this.namedTag.contains(NBT_RIDEABLE_TYPE)) {
            this.jockeyType = SpawnRiderType.fromId(this.namedTag.getInt(NBT_RIDEABLE_TYPE));
        } else {
            this.jockeyType = rollInitialRideableType();
            if (this.namedTag != null) {
                this.namedTag.putInt(NBT_RIDEABLE_TYPE, this.jockeyType.getId());
            }
        }

        if (this.jockeyType == SpawnRiderType.PIGLIN_JOCKEY && !this.isSaddled()) {
            setSaddle(true);
        }

        boolean riderSpawned = this.namedTag != null && this.namedTag.getBoolean(NBT_RIDER_SPAWNED);
        if (!riderSpawned && !this.isBaby() && (this.jockeyType == SpawnRiderType.PIGLIN_JOCKEY || this.jockeyType == SpawnRiderType.PARENT_JOCKEY)) {
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

        tickColdState();
        tickWaterContactDamage();
        tickWarmTimers();

        return updated;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        int amount = Utils.rand(2, 5 + looting);
        drops.add(Item.get(Item.STRING, 0, amount));

        if (isSaddled()) {
            drops.add(Item.get(Item.SADDLE, 0, 1));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public Integer getExperienceDrops() {
        return ThreadLocalRandom.current().nextInt(2) + 1;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (this.isBaby()) return false;

        if (!item.isNull()) {
            if (item.getId() == Item.SADDLE && !this.isSaddled()) {
                getLevel().addLevelSoundEvent(this, LevelSoundEvent.SADDLE, -1, getIdentifier(), false, false);
                setSaddle(true);
                setRideableType(SpawnRiderType.PLAYER);
                return true;

            } else if (item.getId() == Item.SHEARS && this.isSaddled()) {
                if (!this.passengers.isEmpty()) return false;

                Item saddleItem = Item.get(Item.SADDLE, 0, 1);
                if (player.getInventory().canAddItem(saddleItem)) {
                    player.getInventory().addItem(saddleItem);
                } else {
                    this.getLevel().dropItem(clickedPos, saddleItem);
                }

                setSaddle(false);
                setRideableType(SpawnRiderType.NORMAL);
                return false;
            }
        }

        if (isSaddled()) mountEntity(player, true);
        return false;
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        ItemID.WARPED_FUNGUS_ON_A_STICK,
        BlockID.WARPED_FUNGUS
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
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
                ),
                Set.of(
                    new Behavior(
                        new PlaySoundExecutor(Sound.MOB_STRIDER_IDLE), new RandomSoundEvaluator(), 8,1),
                    new Behavior(
                        new MoveToTargetExecutor(CoreMemoryTypes.STAY_NEARBY, this.getMovementSpeedDefault() * 1.10f, true),
                            all(
                                e -> e.isBaby(),
                                e -> e.getMemoryStorage().notEmpty(CoreMemoryTypes.PARENT),
                                e -> e.getMemoryStorage().notEmpty(CoreMemoryTypes.STAY_NEARBY)
                            ),
                        9, 1
                    ),
                    new Behavior(
                        new FollowRiderExecutor(),
                            new RiderItemControllableEvaluator(),
                        8, 1
                    ),
                    new Behavior(
                        new BreedingExecutor(16, 200, 0.25f),
                            all(
                                e -> !e.isBaby(),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                            ),
                        7, 1
                    ),
                    new Behavior(
                        new StriderMoveToLavaExecutor(CoreMemoryTypes.NEAREST_BLOCK, this.getMovementSpeedDefault() * 3.0f),
                            all(
                                e -> !(e.getRider() instanceof Player),
                                e -> !((EntityStrider) e).isWarm(),
                                e -> e.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_BLOCK),
                                e -> ((EntityStrider) e).shouldReturnToLavaNow()
                            ),
                        6, 1
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(this.getMovementSpeedDefault() * 1.25f, 18, 8, true, 80, true, 10),
                            all(
                                e -> e.passengers.isEmpty(),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 80)
                            ),
                        5, 1
                    ),
                    new Behavior(
                        new TemptExecutor(1.2f, false, true, false, 10, 2.0f, new TemptExecutor.TemptSound("tempt", 2.0f, 5.0f), TEMPT_ITEMS),
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
                        new FlatRandomRoamExecutor(this.getMovementSpeedDefault(), 12, 100, false, -1, true, 10),
                            (entity -> true),
                        1, 1
                    )
                ),
                Set.of(
                    new FollowEntitySensor(6f, 2f),
                    new StriderLavaSensor(24, 200),
                    new NearestPlayerSensor(8, 0, 20)
                ),
                Set.of(
                    new WalkController(),
                    new LookController(true, true)
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
