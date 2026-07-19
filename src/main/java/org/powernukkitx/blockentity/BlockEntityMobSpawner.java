package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFlowable;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.mob.EntityMob;
import org.powernukkitx.event.entity.CreatureSpawnEvent;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ShortTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.Utils;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityMobSpawner extends BlockEntitySpawnable {
    private int entityId;
    private int spawnRange;
    private int maxNearbyEntities;
    private int requiredPlayerRange;
    private int delay = 0;
    private int minSpawnDelay;
    private int maxSpawnDelay;
    private int minSpawnCount;
    private int maxSpawnCount;
    private final ThreadLocalRandom nukkitRandom = ThreadLocalRandom.current();

    public static final String TAG_ID = "id";
    public static final String TAG_X = "x";
    public static final String TAG_Y = "y";
    public static final String TAG_Z = "z";
    public static final String TAG_ENTITY_ID = "EntityId";
    public static final String TAG_ENTITY_IDENTIFIER = "EntityIdentifier";
    public static final String TAG_SPAWN_DATA = "SpawnData";
    public static final String TAG_TYPE_ID = "TypeId";
    public static final String TAG_WEIGHT = "Weight";
    public static final String TAG_SPAWN_RANGE = "SpawnRange";
    public static final String TAG_MIN_SPAWN_DELAY = "MinSpawnDelay";
    public static final String TAG_MAX_SPAWN_DELAY = "MaxSpawnDelay";
    public static final String TAG_MAX_NEARBY_ENTITIES = "MaxNearbyEntities";
    public static final String TAG_REQUIRED_PLAYER_RANGE = "RequiredPlayerRange";
    public static final String TAG_MINIMUM_SPAWN_COUNT = "MinimumSpawnerCount";
    public static final String TAG_MAXIMUM_SPAWN_COUNT = "MaximumSpawnerCount";

    public static final int SPAWN_RANGE = 4;
    public static final int MINIMUM_SPAWN_COUNT = 1;
    public static final int MAXIMUM_SPAWN_COUNT = 4;
    public static final int MIN_SPAWN_DELAY = 200;
    public static final int MAX_SPAWN_DELAY = 800;
    public static final int MAX_NEARBY_ENTITIES = 6;
    public static final int REQUIRED_PLAYER_RANGE = 16;

    public BlockEntityMobSpawner(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.entityId = this.getNbt().getInt(TAG_ENTITY_ID);
    }

    @Override
    protected void initBlockEntity() {
        if (!this.nbt.contains(TAG_SPAWN_RANGE) || !(this.nbt.get(TAG_SPAWN_RANGE) instanceof ShortTag)) {
            this.nbt.putShort(TAG_SPAWN_RANGE, (short) SPAWN_RANGE);
        }

        if (!this.nbt.contains(TAG_MIN_SPAWN_DELAY) || !(this.nbt.get(TAG_MIN_SPAWN_DELAY) instanceof ShortTag)) {
            this.nbt.putShort(TAG_MIN_SPAWN_DELAY, (short) MIN_SPAWN_DELAY);
        }

        if (!this.nbt.contains(TAG_MAX_SPAWN_DELAY) || !(this.nbt.get(TAG_MAX_SPAWN_DELAY) instanceof ShortTag)) {
            this.nbt.putShort(TAG_MAX_SPAWN_DELAY, (short) MAX_SPAWN_DELAY);
        }

        if (!this.nbt.contains(TAG_MAX_NEARBY_ENTITIES) || !(this.nbt.get(TAG_MAX_NEARBY_ENTITIES) instanceof ShortTag)) {
            this.nbt.putShort(TAG_MAX_NEARBY_ENTITIES, (short) MAX_NEARBY_ENTITIES);
        }

        if (!this.nbt.contains(TAG_REQUIRED_PLAYER_RANGE) || !(this.nbt.get(TAG_REQUIRED_PLAYER_RANGE) instanceof ShortTag)) {
            this.nbt.putShort(TAG_REQUIRED_PLAYER_RANGE, (short) REQUIRED_PLAYER_RANGE);
        }

        if (!this.nbt.contains(TAG_MINIMUM_SPAWN_COUNT) || !(this.nbt.get(TAG_MINIMUM_SPAWN_COUNT) instanceof ShortTag)) {
            this.nbt.putShort(TAG_MINIMUM_SPAWN_COUNT, (short) MINIMUM_SPAWN_COUNT);
        }

        if (!this.nbt.contains(TAG_MAXIMUM_SPAWN_COUNT) || !(this.nbt.get(TAG_MAXIMUM_SPAWN_COUNT) instanceof ShortTag)) {
            this.nbt.putShort(TAG_MAXIMUM_SPAWN_COUNT, (short) MAXIMUM_SPAWN_COUNT);
        }

        final CompoundTag nbtMap = getNbt();
        this.spawnRange = nbtMap.getShort(TAG_SPAWN_RANGE);
        this.minSpawnDelay = nbtMap.getShort(TAG_MIN_SPAWN_DELAY);
        this.maxSpawnDelay = nbtMap.getShort(TAG_MAX_SPAWN_DELAY);
        this.maxNearbyEntities = nbtMap.getShort(TAG_MAX_NEARBY_ENTITIES);
        this.requiredPlayerRange = nbtMap.getShort(TAG_REQUIRED_PLAYER_RANGE);
        this.minSpawnCount = nbtMap.getShort(TAG_MINIMUM_SPAWN_COUNT);
        this.maxSpawnCount = nbtMap.getShort(TAG_MAXIMUM_SPAWN_COUNT);

        this.scheduleUpdate();
        super.initBlockEntity();
        this.level.getScheduler().scheduleTask(this::spawnToAll);
    }

    @Override
    public boolean onUpdate() {

        if (!isBlockEntityValid()) this.close();

        if (this.closed) {
            return false;
        }

        if (this.entityId <= 0) return true;

        if(!getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) return true;

        if (this.delay++ >= Utils.rand(this.minSpawnDelay, this.maxSpawnDelay)) {
            this.delay = 0;
            int nearbyEntities = 0;
            boolean playerInRange = false;
            String spawnIdentifier = resolveEntityIdentifier();
            for (Entity entity : this.level.getEntities()) {
                if (!playerInRange && entity instanceof Player && !((Player) entity).isSpectator()) {
                    if (entity.distance(this) <= this.requiredPlayerRange) {
                        playerInRange = true;
                    }
                } else if (isSameSpawnerEntity(entity, spawnIdentifier)) {
                    if (isEntityInSpawnerDetectionArea(entity)) {
                        nearbyEntities++;
                    }
                }
            }

            int amountToSpawn = minSpawnCount + nukkitRandom.nextInt(maxSpawnCount);
            for (int i = 0; i < amountToSpawn; i++) {
                if (playerInRange && nearbyEntities < this.maxNearbyEntities) {
                    Position pos = new Position
                            (
                                    this.x + Utils.rand(-this.spawnRange, this.spawnRange),
                                    this.y + Utils.rand(-1, 1),
                                    this.z + Utils.rand(-this.spawnRange, this.spawnRange),
                                    this.level
                            );
                    Block block = level.getBlock(pos);
                    //Mobs shouldn't spawn in walls, and they shouldn't retry to
                    if (
                            !block.getId().equals(Block.AIR) && !(block instanceof BlockFlowable) &&
                                    !block.getId().equals(BlockID.FLOWING_WATER) && !block.getId().equals(BlockID.WATER) &&
                                    !block.getId().equals(BlockID.LAVA) && !block.getId().equals(BlockID.FLOWING_LAVA)
                    ) {
                        continue;
                    }
                    if (!block.subtract(0, 1, 0).getLevelBlock().isSolid()) {
                        continue;
                    }

                    Entity ent = Entity.createEntity(this.entityId, pos);
                    if (ent instanceof EntityMob && getLevel().getFullLight(this) > 7) {
                        ent.close();
                        continue;
                    }
                    CreatureSpawnEvent ev = new CreatureSpawnEvent(this.entityId, pos, new CompoundTag(), CreatureSpawnEvent.SpawnReason.SPAWNER);
                    level.getServer().getPluginManager().callEvent(ev);

                    if (ev.isCancelled()) {
                        ent.close();
                        continue;
                    }

                    if (ent != null) {
                        ent.getNbt().putBoolean("spawner", true);
                        ent.spawnToAll();
                        if (isEntityInSpawnerDetectionArea(ent)) nearbyEntities++;
                    }

                }
            }
        }
        return true;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.entityId <= 0) return;
        super.spawnTo(player);
    }

    @Override
    public void spawnToAll() {
        if (this.entityId <= 0) return;
        super.spawnToAll();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.remove(TAG_ENTITY_ID);
        this.nbt.remove(TAG_ENTITY_IDENTIFIER);
        this.nbt.remove(TAG_SPAWN_DATA);
        if (this.entityId > 0) {
            this.nbt.putInt(TAG_ENTITY_ID, this.entityId);
            String identifier = resolveEntityIdentifier();
            if (identifier != null && !identifier.isEmpty()) {
                this.nbt.putString(TAG_ENTITY_IDENTIFIER, identifier);
            }
        }
        this.nbt.putString(TAG_ID, BlockEntity.MOB_SPAWNER);
        this.nbt.putShort(TAG_SPAWN_RANGE, this.spawnRange);
        this.nbt.putShort(TAG_MIN_SPAWN_DELAY, this.minSpawnDelay);
        this.nbt.putShort(TAG_MAX_SPAWN_DELAY, this.maxSpawnDelay);
        this.nbt.putShort(TAG_MAX_NEARBY_ENTITIES, this.maxNearbyEntities);
        this.nbt.putShort(TAG_REQUIRED_PLAYER_RANGE, this.requiredPlayerRange);
        this.nbt.putShort(TAG_MINIMUM_SPAWN_COUNT, this.minSpawnCount);
        this.nbt.putShort(TAG_MAXIMUM_SPAWN_COUNT, this.maxSpawnCount);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = new CompoundTag()
                .putString(TAG_ID, BlockEntity.MOB_SPAWNER)
                .putInt(TAG_X, (int) this.x)
                .putInt(TAG_Y, (int) this.y)
                .putInt(TAG_Z, (int) this.z);

        if (this.entityId > 0) {
            nbt.putInt(TAG_ENTITY_ID, this.entityId);
            String identifier = resolveEntityIdentifier();
            if (identifier != null && !identifier.isEmpty()) {
                nbt.putString(TAG_ENTITY_IDENTIFIER, identifier);
                nbt.putCompound(TAG_SPAWN_DATA, new CompoundTag()
                        .putString(TAG_TYPE_ID, identifier)
                        .putInt(TAG_WEIGHT, 1)
                );
            }
        }
        return nbt;
    }

    @Override
    public boolean isBlockEntityValid() {
        return Objects.equals(level.getBlockIdAt((int) x, (int) y, (int) z), Block.MOB_SPAWNER);
    }

    public int getSpawnEntityType() {
        return this.entityId;
    }

    public boolean hasSpawnEntityType() {
        return this.entityId > 0;
    }

    public void setSpawnEntityType(int entityId) {
        this.entityId = entityId;
        this.spawnToAll();
    }

    private String resolveEntityIdentifier() {
        String currentIdentifier = Registries.ENTITY.getEntityIdentifier(this.entityId);
        if (currentIdentifier != null && !currentIdentifier.isEmpty()) return currentIdentifier;
        String identifier = this.nbt.getString(TAG_ENTITY_IDENTIFIER);
        if (identifier != null && !identifier.isEmpty()) return identifier;
        return null;
    }

    private boolean isEntityInSpawnerDetectionArea(Entity entity) {
        return entity.x >= this.x - 8 && entity.x < this.x + 8
                && entity.y >= this.y - 5 && entity.y < this.y + 5
                && entity.z >= this.z - 8 && entity.z < this.z + 8;
    }

    private boolean isSameSpawnerEntity(Entity entity, String spawnIdentifier) {
        if (spawnIdentifier != null && !spawnIdentifier.isEmpty()) {
            return spawnIdentifier.equals(entity.getIdentifier());
        }
        return entity.getNetworkId() == this.entityId;
    }

    public void setMinSpawnDelay(int minDelay) {
        if (minDelay > this.maxSpawnDelay) {
            return;
        }

        this.minSpawnDelay = minDelay;
    }

    public void setMaxSpawnDelay(int maxDelay) {
        if (this.minSpawnDelay > maxDelay) {
            return;
        }

        this.maxSpawnDelay = maxDelay;
    }

    public void setSpawnDelay(int minDelay, int maxDelay) {
        if (minDelay > maxDelay) {
            return;
        }

        this.minSpawnDelay = minDelay;
        this.maxSpawnDelay = maxDelay;
    }

    public void setRequiredPlayerRange(int range) {
        this.requiredPlayerRange = range;
    }

    public void setMaxNearbyEntities(int count) {
        this.maxNearbyEntities = count;
    }
}
