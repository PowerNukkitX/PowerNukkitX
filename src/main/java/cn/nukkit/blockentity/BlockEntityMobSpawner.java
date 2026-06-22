package cn.nukkit.blockentity;

import cn.nukkit.Player;
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

        if (!getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) return true;

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
                    }

                }
            }
        }
        return true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putInt(TAG_ENTITY_ID, this.entityId)
                .putString(TAG_ID, BlockEntity.MOB_SPAWNER)
                .putShort(TAG_SPAWN_RANGE, (short) this.spawnRange)
                .putShort(TAG_MIN_SPAWN_DELAY, (short) this.minSpawnDelay)
                .putShort(TAG_MAX_SPAWN_DELAY, (short) this.maxSpawnDelay)
                .putShort(TAG_MAX_NEARBY_ENTITIES, (short) this.maxNearbyEntities)
                .putShort(TAG_REQUIRED_PLAYER_RANGE, (short) this.requiredPlayerRange)
                .putShort(TAG_MINIMUM_SPAWN_COUNT, (short) this.minSpawnCount)
                .putShort(TAG_MAXIMUM_SPAWN_COUNT, (short) this.maxSpawnCount);
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
