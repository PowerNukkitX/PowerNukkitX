package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowable;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Utils;

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
    public static final int MAX_SPAWN_DELAY = 5000;
    public static final int MAX_NEARBY_ENTITIES = 8;
    public static final int REQUIRED_PLAYER_RANGE = 16;

    public BlockEntityMobSpawner(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.entityId = this.namedTag.getInt(TAG_ENTITY_ID);
    }

    @Override
    protected void initBlockEntity() {
        if (!this.namedTag.contains(TAG_SPAWN_RANGE) || !(this.namedTag.get(TAG_SPAWN_RANGE) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_SPAWN_RANGE, SPAWN_RANGE);
        }

        if (!this.namedTag.contains(TAG_MIN_SPAWN_DELAY) || !(this.namedTag.get(TAG_MIN_SPAWN_DELAY) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_MIN_SPAWN_DELAY, MIN_SPAWN_DELAY);
        }

        if (!this.namedTag.contains(TAG_MAX_SPAWN_DELAY) || !(this.namedTag.get(TAG_MAX_SPAWN_DELAY) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_MAX_SPAWN_DELAY, MAX_SPAWN_DELAY);
        }

        if (!this.namedTag.contains(TAG_MAX_NEARBY_ENTITIES) || !(this.namedTag.get(TAG_MAX_NEARBY_ENTITIES) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_MAX_NEARBY_ENTITIES, MAX_NEARBY_ENTITIES);
        }

        if (!this.namedTag.contains(TAG_REQUIRED_PLAYER_RANGE) || !(this.namedTag.get(TAG_REQUIRED_PLAYER_RANGE) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_REQUIRED_PLAYER_RANGE, REQUIRED_PLAYER_RANGE);
        }

        if (!this.namedTag.contains(TAG_MINIMUM_SPAWN_COUNT) || !(this.namedTag.get(TAG_MINIMUM_SPAWN_COUNT) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_MINIMUM_SPAWN_COUNT, MINIMUM_SPAWN_COUNT);
        }

        if (!this.namedTag.contains(TAG_MAXIMUM_SPAWN_COUNT) || !(this.namedTag.get(TAG_MAXIMUM_SPAWN_COUNT) instanceof ShortTag)) {
            this.namedTag.putShort(TAG_MAXIMUM_SPAWN_COUNT, MAXIMUM_SPAWN_COUNT);
        }

        this.spawnRange = this.namedTag.getShort(TAG_SPAWN_RANGE);
        this.minSpawnDelay = this.namedTag.getShort(TAG_MIN_SPAWN_DELAY);
        this.maxSpawnDelay = this.namedTag.getShort(TAG_MAX_SPAWN_DELAY);
        this.maxNearbyEntities = this.namedTag.getShort(TAG_MAX_NEARBY_ENTITIES);
        this.requiredPlayerRange = this.namedTag.getShort(TAG_REQUIRED_PLAYER_RANGE);
        this.minSpawnCount = this.namedTag.getShort(TAG_MINIMUM_SPAWN_COUNT);
        this.maxSpawnCount = this.namedTag.getShort(TAG_MAXIMUM_SPAWN_COUNT);

        this.scheduleUpdate();
        super.initBlockEntity();
    }

    @Override
    public boolean onUpdate() {

        if(!isBlockEntityValid()) this.close();

        if (this.closed) {
            return false;
        }

        if(!getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) return true;

        if (this.delay++ >= Utils.rand(this.minSpawnDelay, this.maxSpawnDelay)) {
            this.delay = 0;
            int nearbyEntities = 0;
            boolean playerInRange = false;
            for (Entity entity : this.level.getEntities()) {
                if (!playerInRange && entity instanceof Player && !((Player) entity).isSpectator()) {
                    if (entity.distance(this) <= this.requiredPlayerRange) {
                        playerInRange = true;
                    }
                } else if (entity instanceof EntityAnimal || entity instanceof EntityMob) {
                    if (entity.distance(this) <= this.requiredPlayerRange) {
                        nearbyEntities++;
                    }
                }
            }

            int amountToSpawn = minSpawnCount + nukkitRandom.nextInt(maxSpawnCount);
            for (int i = 0; i < amountToSpawn; i++) {
                if (playerInRange && nearbyEntities <= this.maxNearbyEntities) {
                    Position pos = new Position
                            (
                                    this.x + Utils.rand(-this.spawnRange, this.spawnRange),
                                    this.getY(),
                                    this.z + Utils.rand(-this.spawnRange, this.spawnRange),
                                    this.level
                            );
                    Block block = level.getBlock(pos);
                    //Mobs shouldn't spawn in walls and they shouldn't retry to
                    if (
                            block.getId() != Block.AIR && !(block instanceof BlockFlowable) &&
                                    block.getId() != BlockID.FLOWING_WATER && block.getId() != BlockID.WATER &&
                                    block.getId() != BlockID.LAVA && block.getId() != BlockID.FLOWING_LAVA
                    ) {
                        continue;
                    }
                    if(!block.subtract(0, 1, 0).getLevelBlock().isSolid()) {
                        continue;
                    }

                    Entity ent = Entity.createEntity(this.entityId, pos);
                    if(ent instanceof EntityMob) {
                        if(getLevel().getFullLight(this) > 7)  {
                            ent.close();
                            continue;
                        }
                    }
                    CreatureSpawnEvent ev = new CreatureSpawnEvent(this.entityId, pos, new CompoundTag(), CreatureSpawnEvent.SpawnReason.SPAWNER);
                    level.getServer().getPluginManager().callEvent(ev);

                    if (ev.isCancelled()) {
                        ent.close();
                        continue;
                    }

                    if(ent != null) {
                        ent.namedTag.putBoolean("spawner", true);
                        ent.spawnToAll();
                    }

                }
            }
        }
        return true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putInt(TAG_ENTITY_ID, this.entityId);
        this.namedTag.putString(TAG_ID, BlockEntity.MOB_SPAWNER);
        this.namedTag.putShort(TAG_SPAWN_RANGE, this.spawnRange);
        this.namedTag.putShort(TAG_MIN_SPAWN_DELAY, this.minSpawnDelay);
        this.namedTag.putShort(TAG_MAX_SPAWN_DELAY, this.maxSpawnDelay);
        this.namedTag.putShort(TAG_MAX_NEARBY_ENTITIES, this.maxNearbyEntities);
        this.namedTag.putShort(TAG_REQUIRED_PLAYER_RANGE, this.requiredPlayerRange);
        this.namedTag.putShort(TAG_MINIMUM_SPAWN_COUNT, this.minSpawnCount);
        this.namedTag.putShort(TAG_MAXIMUM_SPAWN_COUNT, this.maxSpawnCount);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString(TAG_ID, BlockEntity.MOB_SPAWNER)
                .putInt(TAG_ENTITY_ID, this.entityId)
                .putInt(TAG_X, (int) this.x)
                .putInt(TAG_Y, (int) this.y)
                .putInt(TAG_Z, (int) this.z);
    }

    @Override
    public boolean isBlockEntityValid() {
        return Objects.equals(level.getBlockIdAt((int) x, (int) y, (int) z), Block.MOB_SPAWNER);
    }

    public int getSpawnEntityType() {
        return this.entityId;
    }

    public void setSpawnEntityType(int entityId) {
        this.entityId = entityId;
        this.spawnToAll();
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
