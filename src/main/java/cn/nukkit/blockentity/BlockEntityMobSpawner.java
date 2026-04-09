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
import cn.nukkit.utils.Utils;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

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

    public BlockEntityMobSpawner(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        this.entityId = this.namedTag.getInt(TAG_ENTITY_ID);
    }

    @Override
    protected void initBlockEntity() {
        NbtMapBuilder builder = this.namedTag.toBuilder();
        if (!this.namedTag.containsKey(TAG_SPAWN_RANGE) || !(this.namedTag.get(TAG_SPAWN_RANGE) instanceof Short)) {
            builder.putShort(TAG_SPAWN_RANGE, (short) SPAWN_RANGE);
        }

        if (!this.namedTag.containsKey(TAG_MIN_SPAWN_DELAY) || !(this.namedTag.get(TAG_MIN_SPAWN_DELAY) instanceof Short)) {
            builder.putShort(TAG_MIN_SPAWN_DELAY, (short) MIN_SPAWN_DELAY);
        }

        if (!this.namedTag.containsKey(TAG_MAX_SPAWN_DELAY) || !(this.namedTag.get(TAG_MAX_SPAWN_DELAY) instanceof Short)) {
            builder.putShort(TAG_MAX_SPAWN_DELAY, (short) MAX_SPAWN_DELAY);
        }

        if (!this.namedTag.containsKey(TAG_MAX_NEARBY_ENTITIES) || !(this.namedTag.get(TAG_MAX_NEARBY_ENTITIES) instanceof Short)) {
            builder.putShort(TAG_MAX_NEARBY_ENTITIES, (short) MAX_NEARBY_ENTITIES);
        }

        if (!this.namedTag.containsKey(TAG_REQUIRED_PLAYER_RANGE) || !(this.namedTag.get(TAG_REQUIRED_PLAYER_RANGE) instanceof Short)) {
            builder.putShort(TAG_REQUIRED_PLAYER_RANGE, (short) REQUIRED_PLAYER_RANGE);
        }

        if (!this.namedTag.containsKey(TAG_MINIMUM_SPAWN_COUNT) || !(this.namedTag.get(TAG_MINIMUM_SPAWN_COUNT) instanceof Short)) {
            builder.putShort(TAG_MINIMUM_SPAWN_COUNT, (short) MINIMUM_SPAWN_COUNT);
        }

        if (!this.namedTag.containsKey(TAG_MAXIMUM_SPAWN_COUNT) || !(this.namedTag.get(TAG_MAXIMUM_SPAWN_COUNT) instanceof Short)) {
            builder.putShort(TAG_MAXIMUM_SPAWN_COUNT, (short) MAXIMUM_SPAWN_COUNT);
        }

        this.namedTag = builder.build();

        this.spawnRange = this.namedTag.getShort(TAG_SPAWN_RANGE);
        this.minSpawnDelay = this.namedTag.getShort(TAG_MIN_SPAWN_DELAY);
        this.maxSpawnDelay = this.namedTag.getShort(TAG_MAX_SPAWN_DELAY);
        this.maxNearbyEntities = this.namedTag.getShort(TAG_MAX_NEARBY_ENTITIES);
        this.requiredPlayerRange = this.namedTag.getShort(TAG_REQUIRED_PLAYER_RANGE);
        this.minSpawnCount = this.namedTag.getShort(TAG_MINIMUM_SPAWN_COUNT);
        this.maxSpawnCount = this.namedTag.getShort(TAG_MAXIMUM_SPAWN_COUNT);

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
                    CreatureSpawnEvent ev = new CreatureSpawnEvent(this.entityId, pos, NbtMap.EMPTY, CreatureSpawnEvent.SpawnReason.SPAWNER);
                    level.getServer().getPluginManager().callEvent(ev);

                    if (ev.isCancelled()) {
                        ent.close();
                        continue;
                    }

                    if (ent != null) {
                        ent.namedTag = ent.namedTag.toBuilder().putBoolean("spawner", true).build();
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
        this.namedTag = this.namedTag.toBuilder()
                .putInt(TAG_ENTITY_ID, this.entityId)
                .putString(TAG_ID, BlockEntity.MOB_SPAWNER)
                .putShort(TAG_SPAWN_RANGE, (short) this.spawnRange)
                .putShort(TAG_MIN_SPAWN_DELAY, (short) this.minSpawnDelay)
                .putShort(TAG_MAX_SPAWN_DELAY, (short) this.maxSpawnDelay)
                .putShort(TAG_MAX_NEARBY_ENTITIES, (short) this.maxNearbyEntities)
                .putShort(TAG_REQUIRED_PLAYER_RANGE, (short) this.requiredPlayerRange)
                .putShort(TAG_MINIMUM_SPAWN_COUNT, (short) this.minSpawnCount)
                .putShort(TAG_MAXIMUM_SPAWN_COUNT, (short) this.maxSpawnCount)
                .build();
    }

    @Override
    public NbtMap getSpawnCompound() {
        return NbtMap.builder()
                .putString(TAG_ID, BlockEntity.MOB_SPAWNER)
                .putInt(TAG_ENTITY_ID, this.entityId)
                .putInt(TAG_X, (int) this.x)
                .putInt(TAG_Y, (int) this.y)
                .putInt(TAG_Z, (int) this.z)
                .build();
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
