package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityArthropod;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.ClimbController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.FleeFromTargetExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestEntitySensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.entity.passive.EntityArmadillo;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.Utils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntitySpider extends EntityMob implements EntityWalkable, EntityArthropod {
    @Override
    @NotNull
    public String getIdentifier() {
        return SPIDER;
    }


    public EntitySpider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private static final String NBT_RIDEABLE_TYPE = "JockeyType";
    private static final String NBT_RIDER_SPAWNED = "JockeyRiderSpawned";

    private boolean pendingJockeySpawn;
    private SpawnRiderType jockeyType;

    private enum SpawnRiderType {
        NORMAL(0),         // no special mounts
        SKELETON_JOCKEY(1),  // spawned as jockey
        STRAY_JOCKEY(2),  // spawned as jockey
        BOGGED_JOCKEY(3),  // spawned as jockey
        PARCHED_JOCKEY(4),  // spawned as jockey
        WITHER_SKELETON_JOCKEY(5),  // spawned as jockey
        BABY_ZOMBIE_JOCKEY(6),  // spawned as jockey
        BABY_HUSK_JOCKEY(7);  // spawned as jockey

        private final int id;

        private static final SpawnRiderType[] BY_ID = new SpawnRiderType[
                Collections.max(List.of(values()), Comparator.comparingInt(t -> t.id)).id + 1
                ];

        static {
            for (SpawnRiderType t : values()) BY_ID[t.id] = t;
        }

        SpawnRiderType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static SpawnRiderType fromId(int id) {
            return id >= 0 && id < BY_ID.length ? Objects.requireNonNullElse(BY_ID[id], NORMAL) : NORMAL;
        }
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public boolean isRideable() {
        return true;
    }

    @Override
    public @Nullable RideableComponent getComponentRideable() {
        Set<String> riders = Set.of("baby_zombie", "baby_husk");
        float y = 0.54f;
        float z = -0.1f;

        SpawnRiderType type = this.jockeyType != null ? this.jockeyType : SpawnRiderType.NORMAL;
        switch (type) {
            case SKELETON_JOCKEY, STRAY_JOCKEY, WITHER_SKELETON_JOCKEY -> {
                riders = Set.of("skeleton");
                y = 0.54f;
                z = 0f;
            }

            case BOGGED_JOCKEY, PARCHED_JOCKEY -> {
                riders = Set.of("skeleton");
                y = 0.325f;
                z = -0.1f;
            }

            default -> {
            }
        }

        return new RideableComponent(
                0,
                true,
                RideableComponent.DismountMode.DEFAULT,
                riders,
                null,
                0.0f,
                false,
                false,
                1,
                List.of(new RideableComponent.Seat(
                        0,
                        1,
                        new Vector3f(0.0f, y, z),
                        null,
                        null,
                        null,
                        null
                ))
        );
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.3f);
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(16);
    }

    @Override
    public String getOriginalName() {
        return "Spider";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("spider", "monster", "mob", "arthropod");
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    private SpawnRiderType rollInitialRideableType() {
        //  - 99% normal
        //  -  1% jockey
        //      - 80% biome-specific jockey
        //      - 20% default jockey
        //
        // Default jockey only when:
        //  - !daytime OR underground
        //
        // Biome-specific jockey (first-valid):
        //  - stray:   !daytime && snow && !underground
        //  - bogged:  !daytime && !underground && (swamp || mangrove)
        //  - parched: !daytime && !underground && desert
        //  - wither:  nether
        //  - fallback -> default jockey

        int r = ThreadLocalRandom.current().nextInt(100);
        if (r >= 1) return SpawnRiderType.NORMAL; // 99%

        boolean biomeSpecific = ThreadLocalRandom.current().nextInt(100) < 80;
        if (biomeSpecific) {
            SpawnRiderType t = rollBiomeSpecificJockeyType();
            if (t != null) return t;
        }

        return rollDefaultJockeyType();
    }

    private @Nullable SpawnRiderType rollBiomeSpecificJockeyType() {
        if (isNetherBiome()) return SpawnRiderType.WITHER_SKELETON_JOCKEY;

        boolean daytime = this.getLevel().isDaytime();
        boolean underground = isUnderground();

        if (!daytime && !underground) {
            if (isSnowCoveredBiome()) {
                return SpawnRiderType.STRAY_JOCKEY;
            }
            if (isSwampBiome() || isMangroveSwampBiome()) {
                return SpawnRiderType.BOGGED_JOCKEY;
            }
            if (isDesertBiome()) {
                return SpawnRiderType.PARCHED_JOCKEY;
            }
        }

        return null;
    }

    private SpawnRiderType rollDefaultJockeyType() {
        if (this.getLevel().isDaytime() && !isUnderground()) return SpawnRiderType.NORMAL;
        return SpawnRiderType.SKELETON_JOCKEY;
    }

    public SpawnRiderType getRideableType() {
        return jockeyType;
    }

    public void setRideableType(SpawnRiderType type) {
        this.jockeyType = (type == null ? SpawnRiderType.NORMAL : type);
        if (this.nbt != null) {
            this.nbt.putInt(NBT_RIDEABLE_TYPE, this.jockeyType.getId());
        }
    }

    private void spawnPendingRider() {
        if (this.closed || this.level == null) return;
        if (this.nbt != null && this.getNbt().getBoolean(NBT_RIDER_SPAWNED)) return;

        if (!this.passengers.isEmpty()) {
            if (this.nbt != null)
                this.nbt.putBoolean(NBT_RIDER_SPAWNED, true);
            return;
        }

        Entity rider = null;

        switch (this.jockeyType) {
            case SKELETON_JOCKEY -> rider = createRiderEntity(Entity.SKELETON);
            case STRAY_JOCKEY -> rider = createRiderEntity(Entity.STRAY);
            case BOGGED_JOCKEY -> rider = createRiderEntity(Entity.BOGGED);
            case PARCHED_JOCKEY -> rider = createRiderEntity(Entity.PARCHED);
            case WITHER_SKELETON_JOCKEY -> rider = createRiderEntity(Entity.WITHER_SKELETON);
            default -> {
            }
        }

        if (rider == null) return;

        rider.spawnToAll();
        this.mountEntity(rider, true);

        if (this.nbt != null)
            this.nbt.putBoolean(NBT_RIDER_SPAWNED, true);
    }

    private @Nullable Entity createRiderEntity(String entityId) {
        CompoundTag nbt = Entity.getDefaultNBT(this.getLocation());

        switch (this.jockeyType) {
            case SKELETON_JOCKEY, STRAY_JOCKEY, BOGGED_JOCKEY, PARCHED_JOCKEY -> {
                Item bow = Item.get(Item.BOW, 0, 1);
                nbt.put("Mainhand", ItemHelper.write(bow));
            }
            case WITHER_SKELETON_JOCKEY -> {
                Item sword = Item.get(Item.STONE_SWORD, 0, 1);
                nbt.put("Mainhand", ItemHelper.write(sword));
            }
            default -> {}
        }

        Entity rider = Entity.createEntity(entityId, this.getChunk(), nbt);
        if (rider == null) return null;
        return rider;
    }
    private boolean isUnderground() {
        if (this.level == null) return false;

        Block b = this.level.getBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ());
        if (b == null) return false;

        int highest = b.getLevel().getHeightMap(b.getFloorX(), b.getFloorZ());
        return highest > b.getFloorY() && b.canPassThrough()
                && b.getLevel().getBlock(b.getFloorX(), highest, b.getFloorZ()).isSolid();
    }

    private @Nullable Set<String> getSpawnBiomeTags() {
        if (this.level == null) return null;
        int biomeId = this.level.getBiomeId((int) this.x, (int) this.y, (int) this.z);
        return new ObjectOpenHashSet<>(Registries.BIOME.getTags(biomeId));
    }

    private boolean hasSpawnBiomeTag(String tag) {
        Set<String> tags = getSpawnBiomeTags();
        return tags != null && tags.contains(tag);
    }

    private boolean isNetherBiome() {
        return hasSpawnBiomeTag(BiomeTags.NETHER);
    }

    private boolean isSwampBiome() {
        return hasSpawnBiomeTag(BiomeTags.SWAMP);
    }

    private boolean isMangroveSwampBiome() {
        return hasSpawnBiomeTag(BiomeTags.MANGROVE_SWAMP);
    }

    private boolean isDesertBiome() {
        return hasSpawnBiomeTag(BiomeTags.DESERT);
    }

    private boolean isSnowCoveredBiome() {
        return hasSpawnBiomeTag(BiomeTags.SNOWY_SLOPES) ||
                hasSpawnBiomeTag(BiomeTags.FROZEN) ||
                hasSpawnBiomeTag(BiomeTags.ICE) ||
                hasSpawnBiomeTag(BiomeTags.ICE_PLAINS);
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
        this.setCanClimb(true);
        this.setWallClimbing(false);

        if (this.nbt != null && this.nbt.contains(NBT_RIDEABLE_TYPE)) {
            this.jockeyType = SpawnRiderType.fromId(this.getNbt().getInt(NBT_RIDEABLE_TYPE));
        } else {
            this.jockeyType = rollInitialRideableType();
            if (this.nbt != null) {
                nbt.putInt(NBT_RIDEABLE_TYPE, this.jockeyType.getId());
            }
        }

        boolean riderSpawned = this.nbt != null && this.getNbt().getBoolean(NBT_RIDER_SPAWNED);
        if (!riderSpawned && (this.jockeyType != SpawnRiderType.NORMAL)) {
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
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        float stringChance = 0.70f + (0.10f * looting);
        stringChance = Math.min(stringChance, 1.0f);

        if (Utils.rand(0f, 1f) < stringChance) {
            int amount = Utils.rand(1, 2 + looting);
            drops.add(Item.get(Item.STRING, 0, amount));
        }

        float eyeChance = 0.50f + (0.05f * looting);
        eyeChance = Math.min(eyeChance, 1.0f);

        if (Utils.rand(0f, 1f) < eyeChance) {
            int amount = Utils.rand(1, 1 + looting);
            drops.add(Item.get(Item.SPIDER_EYE, 0, amount));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_SPIDER_SAY), new RandomSoundEvaluator(), 6, 1),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.3f, true, 9), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY), 5, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, true, 30), all(
                            new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                            entity -> getLevel().getFullLight(this) <= 11
                            ), 3, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestTargetEntitySensor<>(0, 16, 20, List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new NearestEntitySensor(EntityArmadillo.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 42, 0)
                ),
                Set.of(
                        new WalkController(),
                        new ClimbController(),
                        new LookController(true, true)
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }


    @Override
    public boolean attackTarget(Entity entity) {
        return entity.isFamily("player") || entity.isFamily("golem") || entity.isFamily("snowgolem");
    }
}
