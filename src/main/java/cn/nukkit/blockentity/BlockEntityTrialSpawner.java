package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowable;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockTrialSpawner;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.registry.Registries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityTrialSpawner extends BlockEntitySpawnable {

    public static final String TAG_ID = "id";
    public static final String TAG_X = "x";
    public static final String TAG_Y = "y";
    public static final String TAG_Z = "z";
    public static final String TAG_SPAWN_DATA = "spawn_data";
    public static final String TAG_TYPE_ID = "TypeId";
    public static final String TAG_WEIGHT = "Weight";
    public static final String TAG_SPAWN_RANGE = "SpawnRange";
    public static final String TAG_REQUIRED_PLAYER_RANGE = "RequiredPlayerRange";
    public static final String TAG_TICKS_BETWEEN_SPAWN = "ticks_between_spawn";
    public static final String TAG_TARGET_COOLDOWN_LENGTH = "target_cooldown_length";
    public static final String TAG_TOTAL_MOBS = "total_mobs";
    public static final String TAG_TOTAL_MOBS_ADDED_PER_PLAYER = "total_mobs_added_per_player";
    public static final String TAG_SIMULTANEOUS_MOBS = "simultaneous_mobs";
    public static final String TAG_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER = "simultaneous_mobs_added_per_player";
    public static final String TAG_SPAWN_BABY = "spawn_baby";
    public static final String TAG_NEXT_OMINOUS_PROJECTILE_TICK = "next_ominous_projectile_tick";
    public static final String TAG_OMINOUS_LINGERING_POTION = "ominous_lingering_potion";
    public static final String TAG_OMINOUS_PROJECTILE_KIND = "ominous_projectile_kind";

    public static final int STATE_INACTIVE = 0;
    public static final int STATE_WAITING_FOR_PLAYERS = 1;
    public static final int STATE_ACTIVE = 2;
    public static final int STATE_WAITING_FOR_REWARD_EJECTION = 3;
    public static final int STATE_EJECTING_REWARD = 4;
    public static final int STATE_COOLDOWN = 5;

    public static final int DEFAULT_SPAWN_RANGE = 4;
    public static final int DEFAULT_REQUIRED_PLAYER_RANGE = 14;
    public static final int DEFAULT_TICKS_BETWEEN_SPAWN = 40;
    public static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 30 * 60 * 20;
    public static final double DEFAULT_TOTAL_MOBS = 6.0d;
    public static final double DEFAULT_TOTAL_MOBS_ADDED_PER_PLAYER = 2.0d;
    public static final double DEFAULT_SIMULTANEOUS_MOBS = 2.0d;
    public static final double DEFAULT_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER = 1.0d;

    private static final int TRIAL_CHAMBER_TICKS_BETWEEN_SPAWN = 20;
    private static final int TRIAL_CHAMBER_SLOW_TICKS_BETWEEN_SPAWN = 160;
    private static final int TRIAL_CHAMBER_WAITING_FOR_REWARD_TICKS = 40;
    private static final int TRIAL_CHAMBER_EJECTING_REWARD_TICKS = 20;
    private static final int TRIAL_OMEN_DURATION_PER_LEVEL = 15 * 60 * 20;
    private static final int OMINOUS_PROJECTILE_INTERVAL_TICKS = 8 * 20;
    private static final int OMINOUS_PROJECTILE_DELAY_MIN_TICKS = 3 * 20;
    private static final int OMINOUS_PROJECTILE_DELAY_MAX_TICKS = 6 * 20;
    private static final int MAX_SPAWN_ATTEMPTS = 24;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private Set<Long> spawnedEntities = new HashSet<>();

    private String entityId = EntityID.BREEZE;
    private int spawnRange;
    private int requiredPlayerRange;
    private int ticksBetweenSpawn;
    private int targetCooldownLength;
    private double totalMobs;
    private double totalMobsAddedPerPlayer;
    private double simultaneousMobs;
    private double simultaneousMobsAddedPerPlayer;
    private boolean spawnBaby;

    private int state = STATE_WAITING_FOR_PLAYERS;
    private int nextMobSpawnTick;
    private int cooldownEndsAt;
    private int rewardStateEndsAt;
    private int totalSpawnedThisCycle;
    private int nextOminousProjectileTick;
    private Item pendingReward = Item.AIR;
    private PotionType ominousLingeringPotion;
    private String ominousProjectileKind;

    public BlockEntityTrialSpawner(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.entityId = this.namedTag.getString(TAG_TYPE_ID, EntityID.BREEZE);
    }

    @Override
    protected void initBlockEntity() {
        if (!this.namedTag.contains(TAG_SPAWN_RANGE) || !(this.namedTag.get(TAG_SPAWN_RANGE) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_SPAWN_RANGE, DEFAULT_SPAWN_RANGE);
        }
        if (!this.namedTag.contains(TAG_REQUIRED_PLAYER_RANGE) || !(this.namedTag.get(TAG_REQUIRED_PLAYER_RANGE) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_REQUIRED_PLAYER_RANGE, DEFAULT_REQUIRED_PLAYER_RANGE);
        }
        if (!this.namedTag.contains(TAG_TICKS_BETWEEN_SPAWN)) {
            this.namedTag.putInt(TAG_TICKS_BETWEEN_SPAWN, DEFAULT_TICKS_BETWEEN_SPAWN);
        }
        if (!this.namedTag.contains(TAG_TARGET_COOLDOWN_LENGTH)) {
            this.namedTag.putInt(TAG_TARGET_COOLDOWN_LENGTH, DEFAULT_TARGET_COOLDOWN_LENGTH);
        }
        if (!this.namedTag.contains(TAG_TOTAL_MOBS)) {
            this.namedTag.putDouble(TAG_TOTAL_MOBS, DEFAULT_TOTAL_MOBS);
        }
        if (!this.namedTag.contains(TAG_TOTAL_MOBS_ADDED_PER_PLAYER)) {
            this.namedTag.putDouble(TAG_TOTAL_MOBS_ADDED_PER_PLAYER, DEFAULT_TOTAL_MOBS_ADDED_PER_PLAYER);
        }
        if (!this.namedTag.contains(TAG_SIMULTANEOUS_MOBS)) {
            this.namedTag.putDouble(TAG_SIMULTANEOUS_MOBS, DEFAULT_SIMULTANEOUS_MOBS);
        }
        if (!this.namedTag.contains(TAG_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER)) {
            this.namedTag.putDouble(TAG_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER, DEFAULT_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER);
        }
        if (!this.namedTag.contains(TAG_SPAWN_BABY)) {
            this.namedTag.putBoolean(TAG_SPAWN_BABY, false);
        }

        this.entityId = this.namedTag.getString(TAG_TYPE_ID, EntityID.BREEZE);
        this.spawnRange = this.namedTag.getShort(TAG_SPAWN_RANGE);
        this.requiredPlayerRange = this.namedTag.getShort(TAG_REQUIRED_PLAYER_RANGE);
        this.ticksBetweenSpawn = this.namedTag.getInt(TAG_TICKS_BETWEEN_SPAWN, DEFAULT_TICKS_BETWEEN_SPAWN);
        this.targetCooldownLength = this.namedTag.getInt(TAG_TARGET_COOLDOWN_LENGTH, DEFAULT_TARGET_COOLDOWN_LENGTH);
        this.totalMobs = this.namedTag.getDouble(TAG_TOTAL_MOBS, DEFAULT_TOTAL_MOBS);
        this.totalMobsAddedPerPlayer = this.namedTag.getDouble(TAG_TOTAL_MOBS_ADDED_PER_PLAYER, DEFAULT_TOTAL_MOBS_ADDED_PER_PLAYER);
        this.simultaneousMobs = this.namedTag.getDouble(TAG_SIMULTANEOUS_MOBS, DEFAULT_SIMULTANEOUS_MOBS);
        this.simultaneousMobsAddedPerPlayer = this.namedTag.getDouble(TAG_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER, DEFAULT_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER);
        this.spawnBaby = this.namedTag.getBoolean(TAG_SPAWN_BABY);
        this.nextOminousProjectileTick = this.namedTag.getInt(TAG_NEXT_OMINOUS_PROJECTILE_TICK, this.level.getTick() + OMINOUS_PROJECTILE_INTERVAL_TICKS);
        resolveOminousProjectileSelection();

        this.state = this.entityId == null || this.entityId.isEmpty() ? STATE_INACTIVE : STATE_WAITING_FOR_PLAYERS;
        this.nextMobSpawnTick = this.level.getTick() + this.ticksBetweenSpawn;

        this.scheduleUpdate();
        super.initBlockEntity();
        this.level.getScheduler().scheduleTask(this::spawnToAll);
        updateBlockState(false);
    }

    @Override
    public boolean onUpdate() {
        if (this.closed || this.level == null) {
            return true;
        }
        Set<Long> spawnedEntities = getSpawnedEntities();
        if (!isBlockEntityValid()) {
            this.close();
        }
        if (this.closed) {
            return true;
        }

        cleanupTrackedEntities();

        if (this.entityId == null || this.entityId.isEmpty()) {
            setState(STATE_INACTIVE, false);
            this.scheduleUpdate();
            return true;
        }

        int currentTick = this.level.getTick();
        List<Player> detectedPlayers = getDetectedPlayers();
        if (tryActivateOminous(detectedPlayers, currentTick)) {
            detectedPlayers = getDetectedPlayers();
        }

        if (this.cooldownEndsAt > currentTick) {
            setState(STATE_COOLDOWN, false);
            this.scheduleUpdate();
            return true;
        }
        if (this.cooldownEndsAt != 0) {
            resetEncounter();
        }

        if (this.rewardStateEndsAt > currentTick) {
            if (this.state != STATE_WAITING_FOR_REWARD_EJECTION && this.state != STATE_EJECTING_REWARD) {
                setState(STATE_WAITING_FOR_REWARD_EJECTION, true);
            }
            this.scheduleUpdate();
            return true;
        }
        if (this.state == STATE_WAITING_FOR_REWARD_EJECTION) {
            setState(STATE_EJECTING_REWARD, true);
            this.rewardStateEndsAt = currentTick + TRIAL_CHAMBER_EJECTING_REWARD_TICKS;
            this.scheduleUpdate();
            return true;
        }
        if (this.state == STATE_EJECTING_REWARD) {
            ejectPendingReward();
            this.rewardStateEndsAt = 0;
            this.cooldownEndsAt = currentTick + this.targetCooldownLength;
            setState(STATE_COOLDOWN, true);
            this.scheduleUpdate();
            return true;
        }

        if (detectedPlayers.isEmpty()) {
            if (spawnedEntities.isEmpty() && this.totalSpawnedThisCycle == 0) {
                setState(STATE_WAITING_FOR_PLAYERS, false);
            }
            this.scheduleUpdate();
            return true;
        }

        setState(STATE_ACTIVE, true);

        if (isOminous() && currentTick >= this.nextOminousProjectileTick) {
            spawnOminousProjectileVolley(detectedPlayers, currentTick);
            this.nextOminousProjectileTick = currentTick + OMINOUS_PROJECTILE_INTERVAL_TICKS;
        }

        double allowedTotalMobs = getAllowedTotalMobs(detectedPlayers.size());
        if (this.totalSpawnedThisCycle >= allowedTotalMobs && spawnedEntities.isEmpty()) {
            preparePendingReward();
            this.rewardStateEndsAt = currentTick + TRIAL_CHAMBER_WAITING_FOR_REWARD_TICKS;
            setState(STATE_WAITING_FOR_REWARD_EJECTION, true);
            this.scheduleUpdate();
            return true;
        }

        if (!this.level.getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING) || this.server.getDifficulty() == 0) {
            this.scheduleUpdate();
            return true;
        }

        if (currentTick >= this.nextMobSpawnTick
                && this.totalSpawnedThisCycle < allowedTotalMobs
                && spawnedEntities.size() < getAllowedSimultaneousMobs(detectedPlayers.size())) {
            if (spawnTrialMob()) {
                this.totalSpawnedThisCycle++;
                this.level.addLevelSoundEvent(this, LevelSoundEvent.TRIAL_SPAWNER_SPAWN_MOB, -1, this.entityId, this.spawnBaby, false);
            }
            this.nextMobSpawnTick = currentTick + this.ticksBetweenSpawn;
        }

        if (this.totalSpawnedThisCycle >= allowedTotalMobs && spawnedEntities.isEmpty()) {
            preparePendingReward();
            this.rewardStateEndsAt = currentTick + TRIAL_CHAMBER_WAITING_FOR_REWARD_TICKS;
            setState(STATE_WAITING_FOR_REWARD_EJECTION, true);
        }

        this.scheduleUpdate();
        return true;
    }

    private void cleanupTrackedEntities() {
        Set<Long> spawnedEntities = getSpawnedEntities();
        spawnedEntities.removeIf(entityId -> {
            Entity entity = this.level.getEntity(entityId);
            return entity == null || entity.isClosed() || !entity.isAlive();
        });
    }

    private boolean tryActivateOminous(List<Player> detectedPlayers, int currentTick) {
        if (isOminous()) {
            return false;
        }

        for (Player player : detectedPlayers) {
            boolean hasBadOmen = player.hasEffect(EffectType.BAD_OMEN);
            boolean hasTrialOmen = player.hasEffect(EffectType.TRIAL_OMEN);
            if (!hasBadOmen && !hasTrialOmen) {
                continue;
            }

            if (hasBadOmen) {
                convertBadOmenToTrialOmen(player);
            }
            setOminous(true, true);
            despawnTrackedMobs();
            this.cooldownEndsAt = 0;
            this.rewardStateEndsAt = 0;
            this.totalSpawnedThisCycle = 0;
            this.pendingReward = Item.AIR;
            this.nextMobSpawnTick = currentTick;
            this.nextOminousProjectileTick = currentTick + OMINOUS_PROJECTILE_INTERVAL_TICKS;
            setState(STATE_ACTIVE, true);
            this.level.addLevelSoundEvent(this, LevelSoundEvent.APPLY_EFFECT_TRIAL_OMEN);
            this.level.addLevelSoundEvent(this, LevelSoundEvent.TRIAL_SPAWNER_CHARGE_ACTIVATE);
            return true;
        }

        return false;
    }

    private void convertBadOmenToTrialOmen(Player player) {
        Effect badOmen = player.getEffect(EffectType.BAD_OMEN);
        int amplifier = badOmen != null ? badOmen.getAmplifier() : 0;
        int duration = TRIAL_OMEN_DURATION_PER_LEVEL * Math.max(1, amplifier + 1);

        player.removeEffect(EffectType.BAD_OMEN);
        player.addEffect(Effect.get(EffectType.TRIAL_OMEN)
                .setAmplifier(amplifier)
                .setDuration(duration)
                .setVisible(true));
    }

    private List<Player> getDetectedPlayers() {
        List<Player> players = new ArrayList<>();
        for (Entity entity : this.level.getNearbyEntities(this.getBlock().getBoundingBox().grow(this.requiredPlayerRange, this.requiredPlayerRange, this.requiredPlayerRange))) {
            if (!(entity instanceof Player player) || player.isSpectator()) {
                continue;
            }
            if (hasLineOfSight(player)) {
                players.add(player);
            }
        }
        return players;
    }

    private boolean hasLineOfSight(Player player) {
        Vector3 from = player.getPosition().add(0, player.getEyeHeight(), 0);
        Vector3 to = new Vector3(this.x + 0.5, this.y + 0.5, this.z + 0.5);
        for (Block block : this.level.raycastBlocks(from, to, true, false, 0.25, false, false, true)) {
            if (block == null || block.isAir()) {
                continue;
            }
            if (block.getFloorX() == this.getFloorX() && block.getFloorY() == this.getFloorY() && block.getFloorZ() == this.getFloorZ()) {
                continue;
            }
            if (this.level.blocksBlockSight(block, false, false)) {
                return false;
            }
        }
        return true;
    }

    private boolean spawnTrialMob() {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            Position pos = new Position(
                    this.x + this.random.nextInt(-this.spawnRange, this.spawnRange + 1) + 0.5,
                    this.y,
                    this.z + this.random.nextInt(-this.spawnRange, this.spawnRange + 1) + 0.5,
                    this.level
            );
            if (!canSpawnAt(pos)) {
                continue;
            }

            Entity entity = Entity.createEntity(this.entityId, pos);
            if (entity == null) {
                return false;
            }
            if (this.spawnBaby && entity.isAgeable()) {
                entity.setBaby(true);
            }
            if (isOminous()) {
                applyOminousMobModifiers(entity);
            }

            CreatureSpawnEvent event = new CreatureSpawnEvent(
                    Registries.ENTITY.getEntityNetworkId(this.entityId),
                    pos,
                    new CompoundTag(),
                    CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER
            );
            this.level.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                entity.close();
                return false;
            }

            entity.namedTag.putBoolean("spawner", true);
            entity.namedTag.putBoolean("trial_spawner", true);
            entity.setPersistent(true);
            entity.spawnToAll();
            getSpawnedEntities().add(entity.getId());
            return true;
        }
        return false;
    }

    private boolean canSpawnAt(Position pos) {
        Block block = this.level.getBlock(pos);
        if (!block.getId().equals(Block.AIR)
                && !(block instanceof BlockFlowable)
                && !block.getId().equals(BlockID.FLOWING_WATER)
                && !block.getId().equals(BlockID.WATER)
                && !block.getId().equals(BlockID.LAVA)
                && !block.getId().equals(BlockID.FLOWING_LAVA)) {
            return false;
        }
        return block.subtract(0, 1, 0).getLevelBlock().isSolid();
    }

    private double getAllowedTotalMobs(int playerCount) {
        if (playerCount <= 0) {
            return 0;
        }
        double total = this.totalMobs + this.totalMobsAddedPerPlayer * (playerCount - 1);
        if (isOminous() && !supportsOminousEquipment()) {
            total *= 2;
        }
        return total;
    }

    private double getAllowedSimultaneousMobs(int playerCount) {
        if (playerCount <= 0) {
            return 0;
        }
        double simultaneous = this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (playerCount - 1);
        if (isOminous() && !supportsOminousEquipment()) {
            simultaneous += 1;
        }
        return simultaneous;
    }

    private void resetEncounter() {
        this.cooldownEndsAt = 0;
        this.rewardStateEndsAt = 0;
        this.totalSpawnedThisCycle = 0;
        getSpawnedEntities().clear();
        this.pendingReward = Item.AIR;
        this.nextMobSpawnTick = this.level.getTick() + this.ticksBetweenSpawn;
        this.nextOminousProjectileTick = this.level.getTick() + OMINOUS_PROJECTILE_INTERVAL_TICKS;
        if (isOminous()) {
            setOminous(false, true);
        }
        setState(STATE_WAITING_FOR_PLAYERS, false);
    }

    private void despawnTrackedMobs() {
        for (long entityId : new ArrayList<>(getSpawnedEntities())) {
            Entity entity = this.level.getEntity(entityId);
            if (entity != null && !entity.isClosed()) {
                entity.close();
            }
        }
        getSpawnedEntities().clear();
    }

    private void preparePendingReward() {
        if (!this.pendingReward.isNull()) {
            return;
        }
        this.pendingReward = isOminous()
                ? createOminousReward()
                : createNormalReward();
    }

    private void ejectPendingReward() {
        if (this.pendingReward.isNull()) {
            return;
        }
        this.level.dropItem(this.add(0.5, 0.8, 0.5), this.pendingReward, new Vector3(0, 0.2, 0));
        this.level.addLevelSoundEvent(this, LevelSoundEvent.TRIAL_SPAWNER_EJECT_ITEM);
        this.pendingReward = Item.AIR;
    }

    private Item createNormalReward() {
        if (this.random.nextBoolean()) {
            return Item.get(ItemID.TRIAL_KEY);
        }
        return switch (this.random.nextInt(10)) {
            case 0, 1, 2 -> Item.get(ItemID.COOKED_CHICKEN, 0, 1);
            case 3, 4, 5 -> Item.get(ItemID.BREAD, 0, this.random.nextInt(1, 4));
            case 6, 7 -> Item.get(ItemID.BAKED_POTATO, 0, this.random.nextInt(1, 4));
            case 8 -> Item.get(ItemID.POTION, PotionType.REGENERATION.id(), 1);
            case 9 -> Item.get(ItemID.POTION, PotionType.SWIFTNESS.id(), 1);
            default -> Item.get(ItemID.BREAD, 0, 1);
        };
    }

    private Item createOminousReward() {
        int roll = this.random.nextInt(100);
        if (roll < 30) {
            return Item.get(ItemID.OMINOUS_TRIAL_KEY, 0, 1);
        }
        if (roll < 51) {
            return Item.get(ItemID.BAKED_POTATO, 0, this.random.nextInt(2, 5));
        }
        if (roll < 72) {
            return Item.get(ItemID.COOKED_BEEF, 0, this.random.nextInt(1, 3));
        }
        if (roll < 86) {
            return Item.get(ItemID.GOLDEN_CARROT, 0, this.random.nextInt(1, 3));
        }
        if (roll < 93) {
            return Item.get(ItemID.POTION, PotionType.REGENERATION.id(), 1);
        }
        return Item.get(ItemID.POTION, PotionType.STRENGTH.id(), 1);
    }

    private void resolveOminousProjectileSelection() {
        if (this.namedTag.contains(TAG_OMINOUS_LINGERING_POTION)) {
            this.ominousLingeringPotion = PotionType.get(this.namedTag.getInt(TAG_OMINOUS_LINGERING_POTION));
        }
        if (this.namedTag.contains(TAG_OMINOUS_PROJECTILE_KIND)) {
            this.ominousProjectileKind = this.namedTag.getString(TAG_OMINOUS_PROJECTILE_KIND);
        }
        if (this.ominousLingeringPotion != null && this.ominousProjectileKind != null && !this.ominousProjectileKind.isEmpty()) {
            return;
        }

        Random seededRandom = new Random(this.level.getSeed() ^ (((long) this.getFloorX()) << 42) ^ (((long) this.getFloorY()) << 21) ^ this.getFloorZ());
        PotionType[] lingeringPotions = {
                PotionType.WIND_CHARGED,
                PotionType.OOZING,
                PotionType.WEAVING,
                PotionType.INFESTED,
                PotionType.STRENGTH,
                PotionType.SWIFTNESS,
                PotionType.SLOW_FALLING
        };
        String[] projectileKinds = {
                "arrow",
                "poison_arrow",
                "slowness_arrow",
                "small_fireball",
                "wind_charge"
        };

        this.ominousLingeringPotion = lingeringPotions[seededRandom.nextInt(lingeringPotions.length)];
        this.ominousProjectileKind = projectileKinds[seededRandom.nextInt(projectileKinds.length)];
    }

    private void spawnOminousProjectileVolley(List<Player> detectedPlayers, int currentTick) {
        List<Entity> targets = collectOminousProjectileTargets(detectedPlayers);
        if (targets.isEmpty()) {
            this.nextOminousProjectileTick = currentTick + OMINOUS_PROJECTILE_INTERVAL_TICKS;
            return;
        }

        for (Entity target : targets) {
            Vector3 spawnPos = findOminousProjectileSpawnPosition(target);
            int delay = this.random.nextInt(OMINOUS_PROJECTILE_DELAY_MIN_TICKS, OMINOUS_PROJECTILE_DELAY_MAX_TICKS + 1);
            this.level.addLevelSoundEvent(spawnPos, LevelSoundEvent.OMINOUS_ITEM_SPAWNER_SPAWN_ITEM_BEGIN);
            this.level.getScheduler().scheduleDelayedTask(() -> {
                if (this.closed || this.level == null || !this.isOminous()) {
                    return;
                }
                this.level.addLevelSoundEvent(spawnPos, LevelSoundEvent.OMINOUS_ITEM_SPAWNER_ABOUT_TO_SPAWN_ITEM);
            }, delay - 20);
            this.level.getScheduler().scheduleDelayedTask(() -> launchOminousProjectile(spawnPos), delay);
        }
    }

    private List<Entity> collectOminousProjectileTargets(List<Player> detectedPlayers) {
        List<Entity> targets = new ArrayList<>(detectedPlayers);
        double maxDistanceSquared = this.requiredPlayerRange * this.requiredPlayerRange;
        for (long spawnedEntityId : getSpawnedEntities()) {
            Entity entity = this.level.getEntity(spawnedEntityId);
            if (entity == null || entity.isClosed() || !entity.isAlive()) {
                continue;
            }
            if (entity.distanceSquared(this) <= maxDistanceSquared) {
                targets.add(entity);
            }
        }
        return targets;
    }

    private Vector3 findOminousProjectileSpawnPosition(Entity target) {
        double rayHeight = this.random.nextDouble(2.0d, 6.0d);
        Vector3 from = target.getPosition();
        Vector3 to = from.add(0, rayHeight, 0);

        for (Block block : this.level.raycastBlocks(from, to, true, false, 0.25, false, false, true)) {
            if (block == null || block.isAir()) {
                continue;
            }
            double y = block.getFloorY() - 0.25d;
            if (block.getCollisionBoundingBox() == null) {
                y = block.getFloorY() + 0.5d;
            }
            return new Vector3(block.getFloorX() + 0.5d, y, block.getFloorZ() + 0.5d);
        }

        return to;
    }

    private void launchOminousProjectile(Vector3 spawnPos) {
        if (this.closed || this.level == null || !this.isOminous()) {
            return;
        }

        launchLingeringPotion(spawnPos);
        launchSecondaryOminousProjectile(spawnPos);
        this.level.addLevelSoundEvent(spawnPos, LevelSoundEvent.OMINOUS_ITEM_SPAWNER_SPAWN_ITEM);
    }

    private void launchLingeringPotion(Vector3 spawnPos) {

        CompoundTag nbt = Entity.getDefaultNBT(spawnPos, new Vector3(0, -0.35d, 0), 0f, 90f)
                .putInt("PotionId", this.ominousLingeringPotion.id());
        Entity projectile = Entity.createEntity(EntityID.LINGERING_POTION, this.level.getChunk((int) spawnPos.x >> 4, (int) spawnPos.z >> 4), nbt);
        if (projectile != null) {
            projectile.spawnToAll();
        }
    }

    private void launchSecondaryOminousProjectile(Vector3 spawnPos) {
        switch (this.ominousProjectileKind) {
            case "arrow" -> launchArrow(spawnPos, null);
            case "poison_arrow" -> launchArrow(spawnPos, PotionType.POISON_LONG);
            case "slowness_arrow" -> launchArrow(spawnPos, PotionType.SLOWNESS_STRONG);
            case "small_fireball" -> launchProjectileEntity(EntityID.SMALL_FIREBALL, spawnPos, new Vector3(0, -0.3d, 0), 2.0d);
            case "wind_charge" -> launchProjectileEntity(EntityID.WIND_CHARGE_PROJECTILE, spawnPos, new Vector3(0, -0.4d, 0), null);
            default -> launchArrow(spawnPos, null);
        }
    }

    private void launchArrow(Vector3 spawnPos, PotionType potionType) {
        CompoundTag nbt = Entity.getDefaultNBT(spawnPos, new Vector3(0, -1.2d, 0), 0f, 90f)
                .putDouble("damage", 2.0d)
                .putByte("pickup", (byte) 2);
        EntityArrow arrow = (EntityArrow) Entity.createEntity(EntityID.ARROW, this.level.getChunk((int) spawnPos.x >> 4, (int) spawnPos.z >> 4), nbt);
        if (arrow == null) {
            return;
        }
        if (potionType != null) {
            arrow.setItem(new ItemArrow(potionType.id() + 1, 1));
        }
        arrow.spawnToAll();
    }

    private void launchProjectileEntity(String projectileEntityId, Vector3 spawnPos, Vector3 motion, Double damage) {
        CompoundTag nbt = Entity.getDefaultNBT(spawnPos, motion, 0f, 90f);
        if (damage != null) {
            nbt.putDouble("damage", damage);
        }
        Entity projectile = Entity.createEntity(projectileEntityId, this.level.getChunk((int) spawnPos.x >> 4, (int) spawnPos.z >> 4), nbt);
        if (projectile != null) {
            projectile.spawnToAll();
        }
    }

    private boolean isOminous() {
        if (this.closed || this.level == null) {
            return false;
        }
        return this.getBlock() instanceof BlockTrialSpawner trialSpawner
                && trialSpawner.getPropertyValue(CommonBlockProperties.OMINOUS);
    }

    private boolean supportsOminousEquipment() {
        return switch (this.entityId) {
            case EntityID.ZOMBIE, EntityID.HUSK, EntityID.SKELETON, EntityID.STRAY, EntityID.BOGGED -> true;
            default -> false;
        };
    }

    private void applyOminousMobModifiers(Entity entity) {
        if (!(entity instanceof EntityMob mob) || !supportsOminousEquipment()) {
            return;
        }

        equipOminousArmor(mob);

        if (isOminousRangedMob()) {
            mob.setItemInHand(createOminousRangedWeapon());
        } else if (isOminousMeleeMob()) {
            mob.setItemInHand(createOminousMeleeWeapon());
        }
    }

    private boolean isOminousMeleeMob() {
        return EntityID.ZOMBIE.equals(this.entityId) || EntityID.HUSK.equals(this.entityId);
    }

    private boolean isOminousRangedMob() {
        return EntityID.SKELETON.equals(this.entityId)
                || EntityID.STRAY.equals(this.entityId)
                || EntityID.BOGGED.equals(this.entityId);
    }

    private void equipOminousArmor(EntityMob mob) {
        String helmetId;
        String chestplateId;
        String trimPattern;
        int roll = this.random.nextInt(7);
        if (roll <= 3) {
            helmetId = ItemID.CHAINMAIL_HELMET;
            chestplateId = ItemID.CHAINMAIL_CHESTPLATE;
            trimPattern = "bolt";
        } else if (roll <= 5) {
            helmetId = ItemID.IRON_HELMET;
            chestplateId = ItemID.IRON_CHESTPLATE;
            trimPattern = "flow";
        } else {
            helmetId = ItemID.DIAMOND_HELMET;
            chestplateId = ItemID.DIAMOND_CHESTPLATE;
            trimPattern = "flow";
        }

        if (this.random.nextBoolean()) {
            mob.setHelmet(createOminousArmorPiece(helmetId, trimPattern));
        }
        if (this.random.nextBoolean()) {
            mob.setChestplate(createOminousArmorPiece(chestplateId, trimPattern));
        }
    }

    private Item createOminousArmorPiece(String itemId, String trimPattern) {
        Item item = Item.get(itemId);
        item.addEnchantment(
                Enchantment.getEnchantment(Enchantment.ID_PROTECTION_FIRE).setLevel(4, false),
                Enchantment.getEnchantment(Enchantment.ID_PROTECTION_PROJECTILE).setLevel(4, false),
                Enchantment.getEnchantment(Enchantment.ID_PROTECTION_ALL).setLevel(4, false)
        );
        CompoundTag tag = item.getOrCreateNamedTag();
        tag.putCompound("Trim", new CompoundTag()
                .putString("Material", "copper")
                .putString("Pattern", trimPattern));
        item.setNamedTag(tag);
        return item;
    }

    private Item createOminousMeleeWeapon() {
        return switch (this.random.nextInt(7)) {
            case 0, 1, 2, 3 -> Item.get(ItemID.IRON_SWORD);
            case 4 -> enchantedItem(Item.get(ItemID.IRON_SWORD), Enchantment.ID_DAMAGE_ALL, 1);
            case 5 -> enchantedItem(Item.get(ItemID.IRON_SWORD), Enchantment.ID_KNOCKBACK, 1);
            case 6 -> Item.get(ItemID.DIAMOND_SWORD);
            default -> Item.get(ItemID.IRON_SWORD);
        };
    }

    private Item createOminousRangedWeapon() {
        return switch (this.random.nextInt(4)) {
            case 0, 1 -> Item.get(ItemID.BOW);
            case 2 -> enchantedItem(Item.get(ItemID.BOW), Enchantment.ID_BOW_POWER, 1);
            case 3 -> enchantedItem(Item.get(ItemID.BOW), Enchantment.ID_BOW_KNOCKBACK, 1);
            default -> Item.get(ItemID.BOW);
        };
    }

    private Item enchantedItem(Item item, int enchantmentId, int level) {
        item.addEnchantment(Enchantment.getEnchantment(enchantmentId).setLevel(level, false));
        return item;
    }

    private void setOminous(boolean ominous, boolean sendUpdate) {
        if (this.closed || this.level == null) {
            return;
        }
        if (!(this.getBlock() instanceof BlockTrialSpawner block)) {
            return;
        }
        if (block.getPropertyValue(CommonBlockProperties.OMINOUS) == ominous) {
            return;
        }
        block.setPropertyValue(CommonBlockProperties.OMINOUS, ominous);
        this.level.setBlock(block, block, sendUpdate, false);
        this.spawnToAll();
    }

    private void setState(int newState, boolean notify) {
        if (this.state == newState) {
            return;
        }

        int previous = this.state;
        this.state = newState;
        updateBlockState(true);

        if (!notify) {
            return;
        }

        if (newState == STATE_ACTIVE && previous == STATE_WAITING_FOR_PLAYERS) {
            this.level.addLevelSoundEvent(this, LevelSoundEvent.TRIAL_SPAWNER_DETECT_PLAYER);
            this.level.addLevelSoundEvent(this, LevelSoundEvent.TRIAL_SPAWNER_OPEN_SHUTTER);
        } else if (newState == STATE_WAITING_FOR_REWARD_EJECTION) {
            this.level.addLevelSoundEvent(this, LevelSoundEvent.TRIAL_SPAWNER_CLOSE_SHUTTER);
        } else if (newState == STATE_EJECTING_REWARD) {
            this.level.addLevelSoundEvent(this, LevelSoundEvent.TRIAL_SPAWNER_EJECT_ITEM);
        }
    }

    private void updateBlockState(boolean sendUpdate) {
        if (this.closed || this.level == null) {
            return;
        }
        if (!(this.getBlock() instanceof BlockTrialSpawner block)) {
            return;
        }
        block.setPropertyValue(CommonBlockProperties.TRIAL_SPAWNER_STATE, this.state);
        this.level.setBlock(block, block, false, sendUpdate);
        this.spawnToAll();
    }

    public void applyTrialChamberDefaults(String structureName) {
        this.spawnRange = DEFAULT_SPAWN_RANGE;
        this.requiredPlayerRange = DEFAULT_REQUIRED_PLAYER_RANGE;
        this.targetCooldownLength = DEFAULT_TARGET_COOLDOWN_LENGTH;
        this.totalMobs = DEFAULT_TOTAL_MOBS;
        this.totalMobsAddedPerPlayer = DEFAULT_TOTAL_MOBS_ADDED_PER_PLAYER;
        this.spawnBaby = structureName.endsWith("/baby_zombie");

        if (structureName.contains("/breeze/")) {
            this.ticksBetweenSpawn = TRIAL_CHAMBER_TICKS_BETWEEN_SPAWN;
            this.totalMobs = 2.0d;
            this.totalMobsAddedPerPlayer = 1.0d;
            this.simultaneousMobs = 1.0d;
            this.simultaneousMobsAddedPerPlayer = 0.5d;
        } else if (structureName.contains("/slow_ranged/")) {
            this.ticksBetweenSpawn = TRIAL_CHAMBER_SLOW_TICKS_BETWEEN_SPAWN;
            this.simultaneousMobs = 4.0d;
            this.simultaneousMobsAddedPerPlayer = 2.0d;
        } else if (this.spawnBaby) {
            this.ticksBetweenSpawn = TRIAL_CHAMBER_TICKS_BETWEEN_SPAWN;
            this.simultaneousMobs = 2.0d;
            this.simultaneousMobsAddedPerPlayer = 0.5d;
        } else {
            this.ticksBetweenSpawn = TRIAL_CHAMBER_TICKS_BETWEEN_SPAWN;
            this.simultaneousMobs = 3.0d;
            this.simultaneousMobsAddedPerPlayer = 0.5d;
        }

        this.nextMobSpawnTick = this.level.getTick() + this.ticksBetweenSpawn;
        this.spawnToAll();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putString(TAG_TYPE_ID, this.entityId);
        this.namedTag.putShort(TAG_SPAWN_RANGE, this.spawnRange);
        this.namedTag.putShort(TAG_REQUIRED_PLAYER_RANGE, this.requiredPlayerRange);
        this.namedTag.putInt(TAG_TICKS_BETWEEN_SPAWN, this.ticksBetweenSpawn);
        this.namedTag.putInt(TAG_TARGET_COOLDOWN_LENGTH, this.targetCooldownLength);
        this.namedTag.putDouble(TAG_TOTAL_MOBS, this.totalMobs);
        this.namedTag.putDouble(TAG_TOTAL_MOBS_ADDED_PER_PLAYER, this.totalMobsAddedPerPlayer);
        this.namedTag.putDouble(TAG_SIMULTANEOUS_MOBS, this.simultaneousMobs);
        this.namedTag.putDouble(TAG_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER, this.simultaneousMobsAddedPerPlayer);
        this.namedTag.putBoolean(TAG_SPAWN_BABY, this.spawnBaby);
        this.namedTag.putInt(TAG_NEXT_OMINOUS_PROJECTILE_TICK, this.nextOminousProjectileTick);
        if (this.ominousLingeringPotion != null) {
            this.namedTag.putInt(TAG_OMINOUS_LINGERING_POTION, this.ominousLingeringPotion.id());
        }
        if (this.ominousProjectileKind != null) {
            this.namedTag.putString(TAG_OMINOUS_PROJECTILE_KIND, this.ominousProjectileKind);
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (this.entityId == null || this.entityId.isEmpty()) {
            this.entityId = EntityID.BREEZE;
        }
        return new CompoundTag()
                .putString(TAG_ID, BlockEntity.TRIAL_SPAWNER)
                .putCompound(TAG_SPAWN_DATA, new CompoundTag()
                        .putString(TAG_TYPE_ID, this.entityId)
                        .putInt(TAG_WEIGHT, 1)
                )
                .putInt(TAG_X, (int) this.x)
                .putInt(TAG_Y, (int) this.y)
                .putInt(TAG_Z, (int) this.z);
    }

    @Override
    public boolean isBlockEntityValid() {
        if (this.level == null) {
            return false;
        }
        return Objects.equals(level.getBlockIdAt((int) x, (int) y, (int) z), Block.TRIAL_SPAWNER);
    }

    public String getSpawnEntityType() {
        return this.entityId;
    }

    public void setSpawnEntityType(String entityId) {
        this.entityId = entityId;
        this.state = entityId == null || entityId.isEmpty() ? STATE_INACTIVE : STATE_WAITING_FOR_PLAYERS;
        this.spawnToAll();
    }

    public void setRequiredPlayerRange(int range) {
        this.requiredPlayerRange = range;
    }

    private Set<Long> getSpawnedEntities() {
        if (this.spawnedEntities == null) {
            this.spawnedEntities = new HashSet<>();
        }
        return this.spawnedEntities;
    }

    @Override
    public void close() {
        super.close();
        getSpawnedEntities().clear();
    }
}
