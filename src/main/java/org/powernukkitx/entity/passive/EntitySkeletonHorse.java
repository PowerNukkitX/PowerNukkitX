package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.evaluator.RiderItemControllableEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.FollowRiderExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.SkeletonHorseTrapExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.SkeletonHorseTrapSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.HorseJumpStrengthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.entity.weather.EntityLightningBolt;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.inventory.EntityArmorInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntitySkeletonHorse extends EntityAnimal implements EntityWalkable {
    @Override
    @NotNull
    public String getIdentifier() {
        return SKELETON_HORSE;
    }

    public EntitySkeletonHorse(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private static final String NBT_RIDER_SPAWNED = "JockeyRiderSpawned";

    private boolean naturalSpawn;
    private boolean enableTrap;
    private boolean startTrap;
    private boolean pendingJockeySpawn;

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.6f;
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
    public @Nullable RideableComponent getComponentRideable() {
        return new RideableComponent(
                0,
                false,
                RideableComponent.DismountMode.DEFAULT,
                Set.of("player", "skeleton", "baby_zombie", "baby_husk"),
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
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(15);
    }

    @Override
    public @Nullable HorseJumpStrengthComponent getComponentHorseJumpStrength() {
        return HorseJumpStrengthComponent.range(0.4f, 1.0f);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.2f);
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
        return "Skeleton Horse";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("skeletonhorse", "undead", "mob");
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        setInputControls(true);

        // EnableTrap
        if (this.nbt.contains("EnableTrap")) {
            this.enableTrap = this.getNbt().getBoolean("EnableTrap");
        } else {
            this.enableTrap = true;
            this.nbt.putBoolean("EnableTrap", true);
        }

        // SpawnReason
        String reason = this.nbt.contains("SpawnReason") ? this.getNbt().getString("SpawnReason") : null;
        this.naturalSpawn = reason != null && reason.equalsIgnoreCase("NATURAL");

    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 2) != 0) {
            int amount = Utils.rand(0, 2 + looting);
            if (amount > 0) {
                return new Item[]{
                        Item.get(Item.BONE, 0, amount)
                };
            }
        }

        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (this.isBaby()) return false;

        mountEntity(player, true);
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean b = super.onUpdate(currentTick);

        if (currentTick % 2 == 0 && getRideJumping() != null && currentTick - getRideJumping().get() > 5 && this.isOnGround()) {
            this.setDataFlag(ActorFlags.STANDING, false);
            this.rideJumping.set(-1);
        }

        if (this.pendingJockeySpawn) {
            this.pendingJockeySpawn = false;
            spawnPendingRider();
        }

        return b;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if ((source instanceof EntityDamageByEntityEvent ev) && ev.getDamager() instanceof EntityLightningBolt) {
            ev.setCancelled(true);
            return false;
        }
        return super.attack(source);
    }

    public boolean isTrapEnabled() {
        return enableTrap;
    }

    public boolean isNaturalSpawn() {
        return naturalSpawn;
    }

    public void disableTrap() {
        if (!this.enableTrap) return;

        this.enableTrap = false;
        this.nbt.putBoolean("EnableTrap", false);
    }

    public boolean isStartedTrap() {
        return this.startTrap;
    }

    public void setStartTrap(boolean value) {
        this.startTrap = value;
    }

    public void skeletonTrap() {
        Vector3 vector = this.getPosition();

        CompoundTag nbt = Entity.getDefaultNBT(vector, null, (float) this.yaw, (float) this.pitch);
        EntityLightningBolt bolt = new EntityLightningBolt(this.chunk, nbt);

        bolt.spawnToAll();

        int lightningRuntimeId = Registries.ENTITY.getEntityNetworkId(EntityID.LIGHTNING_BOLT);
        this.level.addLevelSoundEvent(vector, SoundEvent.THUNDER, -1, lightningRuntimeId);
        this.level.addLevelSoundEvent(vector, SoundEvent.EXPLODE, -1, lightningRuntimeId);

        double spread = 1.5d;
        CompoundTag nbt1 = nbt.copy();
        nbt1.getList("Pos", DoubleTag.class).add(0, new DoubleTag(vector.x + spread));
        nbt1.getList("Pos", DoubleTag.class).add(2, new DoubleTag(vector.z));

        CompoundTag nbt2 = nbt.copy();
        nbt2.getList("Pos", DoubleTag.class).add(0, new DoubleTag(vector.x - (spread * 0.5d)));
        nbt2.getList("Pos", DoubleTag.class).add(2, new DoubleTag(vector.z + (spread * 0.866d)));

        CompoundTag nbt3 = nbt.copy();
        nbt3.getList("Pos", DoubleTag.class).add(0, new DoubleTag(vector.x - (spread * 0.5d)));
        nbt3.getList("Pos", DoubleTag.class).add(2, new DoubleTag(vector.z - (spread * 0.866d)));

        Entity skeletonHorse1 = Entity.createEntity(Entity.SKELETON_HORSE, this.getChunk(), nbt1);
        Entity skeletonHorse2 = Entity.createEntity(Entity.SKELETON_HORSE, this.getChunk(), nbt2);
        Entity skeletonHorse3 = Entity.createEntity(Entity.SKELETON_HORSE, this.getChunk(), nbt3);

        this.setPendingRider(true);
        ((EntitySkeletonHorse) skeletonHorse1).setPendingRider(true);
        ((EntitySkeletonHorse) skeletonHorse2).setPendingRider(true);
        ((EntitySkeletonHorse) skeletonHorse3).setPendingRider(true);

        skeletonHorse1.spawnToAll();
        skeletonHorse2.spawnToAll();
        skeletonHorse3.spawnToAll();
    }

    public void setPendingRider(boolean value) {
        this.pendingJockeySpawn = value;
    }

    public void setNaturalSpawn(boolean value) {
        this.naturalSpawn = value;
    }

    private void spawnPendingRider() {
        if (this.closed || this.level == null || this.isBaby()) return;
        if (this.nbt != null && this.getNbt().getBoolean(NBT_RIDER_SPAWNED)) return;

        if (!this.passengers.isEmpty()) {
            if (this.nbt != null)
                this.nbt.putBoolean(NBT_RIDER_SPAWNED, true);
            return;
        }

        Entity rider = createRiderEntity(Entity.SKELETON);
        if (rider == null) return;

        this.mountEntity(rider, true);
        rider.spawnToAll();

        if (this.nbt != null)
            this.nbt.putBoolean(NBT_RIDER_SPAWNED, true);
    }

    private @Nullable Entity createRiderEntity(String entityId) {
        CompoundTag nbt = Entity.getDefaultNBT(this.getLocation());

        // Bow + enchants
        Item bow = Item.get(Item.BOW, 0, 1);
        Enchantment.addRandomEnchantments(
                bow,
                1 + ThreadLocalRandom.current().nextInt(2),
                false
        );
        nbt.putCompound("Mainhand", ItemHelper.write(bow));

        // Iron helmet + enchants
        Item helmet = Item.get(Item.IRON_HELMET, 0, 1);
        Enchantment.addRandomEnchantments(
                helmet,
                1 + ThreadLocalRandom.current().nextInt(3),
                false
        );
        ListTag<CompoundTag> armor = new ListTag<>(Tag.TAG_Compound);
        armor.add(ItemHelper.write(helmet, EntityArmorInventory.SLOT_HEAD));
        nbt.putList("Armor", armor);

        return Entity.createEntity(entityId, this.getChunk(), nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .coreBehaviors(
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
                                new SkeletonHorseTrapExecutor(),
                                e -> ((EntitySkeletonHorse) e).isStartedTrap(),
                                4, 1, 1
                        ),
                        new Behavior( // Follow rider ONLY if rider has an attack target
                                new FollowRiderExecutor(),
                                all(
                                        new RiderItemControllableEvaluator(),
                                        e -> e.hasControllingPassenger(),
                                        e -> {
                                            Entity rider = e.getRider();
                                            if (rider == null || !rider.isAlive()) return false;
                                            if (rider instanceof EntityIntelligent ri) {
                                                return ri.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET)
                                                        && ri.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) != null;
                                            }
                                            return false;
                                        }
                                ),
                                3,
                                1
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10),
                                e -> e.passengers == null || e.passengers.isEmpty(),
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
                        )
                )
                .sensors(
                        new NearestPlayerSensor(8, 0, 20),
                        new SkeletonHorseTrapSensor(10, 3)
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
        if (rider instanceof Player) return;

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
            if (needsRecalcMovement) handleCollideMovement(currentTick);
            addTmpMoveMotionXZ(previousCollideMotion);
            handleFloatingMovement();
            handleGroundFrictionMovement();
            handlePassableBlockFrictionMovement();
        }
    }
}
