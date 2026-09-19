package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.NumberTag;
import org.powernukkitx.nbt.tag.Tag;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

/**
 * Tracks and serializes LimboEntities runtime state for the vanilla dimensions.
 *
 * Limbo holds actors pending transfer to their position-derived chunk. Actorprefix
 * and digp ownership remain managed by normal chunk actor persistence rather than
 * being directly rewritten by Limbo materialization.
 *
 * @author Curse
 */
@Slf4j
final class LevelDBLimboEntities {
    private static final byte LIMBO_VERSION = 2;
    private static final byte[] OVERWORLD_KEY = "Overworld".getBytes(StandardCharsets.UTF_8);
    private static final byte[] NETHER_KEY = "Nether".getBytes(StandardCharsets.UTF_8);
    private static final byte[] THE_END_KEY = "TheEnd".getBytes(StandardCharsets.UTF_8);

    private final LevelDBStorage storage;
    private final Map<Integer, Map<Long, CompoundTag>> runtimeBuckets = new HashMap<>();

    LevelDBLimboEntities(LevelDBStorage storage) {
        this.storage = storage;
    }

    static boolean supportsDimension(DimensionData dimensionData) {
        int dimensionId = dimensionData.getDimensionId();
        return dimensionId == Level.DIMENSION_OVERWORLD || dimensionId == Level.DIMENSION_NETHER || dimensionId == Level.DIMENSION_THE_END;
    }

    void initializeGeneratedDimension(DimensionData dimensionData) {
        if (!supportsDimension(dimensionData)) {
            return;
        }

        synchronized (this.storage) {
            byte[] key = getDimensionKey(dimensionData);
            CompoundTag root = this.storage.readGlobalCompound(key);
            root = root == null ? new CompoundTag() : root.copy();

            Preconditions.checkState(!root.contains("data") || root.containsCompound("data"),
                    "Canonical dimension data contains invalid data tag");
            CompoundTag data = root.containsCompound("data") ? root.getCompound("data").copy() : new CompoundTag();

            if (data.contains("LimboEntities")) {
                Preconditions.checkState(data.containsList("LimboEntities"), "Canonical dimension data contains invalid LimboEntities");
                ListTag<?> buckets = data.getList("LimboEntities");
                Preconditions.checkState(buckets.type == Tag.TAG_Compound, "LimboEntities is not List<Compound>");
                return;
            }

            data.putList("LimboEntities", new ListTag<CompoundTag>(Tag.TAG_Compound));
            root.putCompound("data", data);
            this.storage.writeGlobalCompound(key, root);
        }
    }

    void loadDimension(DimensionData dimensionData) {
        if (!supportsDimension(dimensionData)) {
            return;
        }

        synchronized (this.storage) {
            int dimensionId = dimensionData.getDimensionId();
            if (this.runtimeBuckets.containsKey(dimensionId)) {
                return;
            }

            CompoundTag root = readCanonicalRoot(dimensionData);
            ListTag<CompoundTag> buckets = getLimboBuckets(root.getCompound("data"));
            Map<Long, CompoundTag> bucketsByChunk = new HashMap<>();

            for (int i = 0; i < buckets.size(); i++) {
                CompoundTag bucket = buckets.get(i);
                validateBucket(bucket);

                int chunkX = bucket.getInt("ChunkX");
                int chunkZ = bucket.getInt("ChunkZ");
                long chunkHash = Level.chunkHash(chunkX, chunkZ);

                Preconditions.checkState(bucketsByChunk.put(chunkHash, bucket.copy()) == null,
                        "Duplicate LimboEntities bucket for chunk [%s,%s]", chunkX, chunkZ);
            }

            this.runtimeBuckets.put(dimensionId, bucketsByChunk);
        }
    }

    void saveDimension(DimensionData dimensionData) {
        if (!supportsDimension(dimensionData)) {
            return;
        }

        synchronized (this.storage) {
            CompoundTag root = readCanonicalRoot(dimensionData);
            CompoundTag data = root.getCompound("data").copy();
            ListTag<CompoundTag> buckets = new ListTag<>(Tag.TAG_Compound);

            for (CompoundTag bucket : getRuntimeBuckets(dimensionData).values()) {
                validateBucket(bucket);
                if (getEntityTagList(bucket).size() == 0) {
                    continue;
                }
                buckets.add(bucket.copy());
            }

            data.putList("LimboEntities", buckets);
            root.putCompound("data", data);
            this.storage.writeGlobalCompound(getDimensionKey(dimensionData), root);
        }
    }

    void deferActor(IChunk sourceChunk, CompoundTag actorTag, int targetChunkX, int targetChunkZ) {
        DimensionData dimensionData = sourceChunk.getProvider().getDimensionData();
        Preconditions.checkArgument(supportsDimension(dimensionData), "LimboEntities is not defined for dimension %s", dimensionData.getDimensionId());
        Preconditions.checkArgument(actorTag.contains("UniqueID"), "Limbo actor is missing UniqueID");
        Preconditions.checkArgument(isInChunk(actorTag, targetChunkX, targetChunkZ), "Limbo target chunk does not match actor Pos");

        LevelDBActorStorage.getActorStorageKey(actorTag.getLong("UniqueID"));
        CompoundTag limboActor = actorTag.copy().putByte("LimboVersion", LIMBO_VERSION);

        synchronized (this.storage) {
            Map<Long, CompoundTag> buckets = getRuntimeBuckets(dimensionData);
            long chunkHash = Level.chunkHash(targetChunkX, targetChunkZ);
            CompoundTag bucket = buckets.get(chunkHash);

            if (bucket == null) {
                ListTag<CompoundTag> entityTags = new ListTag<>(Tag.TAG_Compound);
                entityTags.add(limboActor);
                buckets.put(chunkHash, new CompoundTag()
                        .putInt("ChunkX", targetChunkX)
                        .putInt("ChunkZ", targetChunkZ)
                        .putList("EntityTagList", entityTags));
                return;
            }

            validateBucket(bucket);
            ListTag<CompoundTag> entityTags = copyCompoundList(getEntityTagList(bucket));
            entityTags.add(limboActor);

            CompoundTag updatedBucket = bucket.copy();
            updatedBucket.putList("EntityTagList", entityTags);
            buckets.put(chunkHash, updatedBucket);
        }
    }

    void consumeChunk(IChunk chunk) {
        DimensionData dimensionData = chunk.getProvider().getDimensionData();
        if (!supportsDimension(dimensionData)) {
            return;
        }

        long chunkHash = Level.chunkHash(chunk.getX(), chunk.getZ());
        ListTag<CompoundTag> pending;

        synchronized (this.storage) {
            CompoundTag bucket = getRuntimeBuckets(dimensionData).get(chunkHash);
            if (bucket == null) {
                return;
            }

            validateBucket(bucket);
            pending = copyCompoundList(getEntityTagList(bucket));
        }

        Set<Long> successfulActors = new HashSet<>();

        for (int i = 0; i < pending.size(); i++) {
            Entity entity = materializeActor(chunk, pending.get(i));
            if (entity != null) {
                successfulActors.add(entity.uniqueIdLong());
            }
        }

        if (successfulActors.size() == 0) {
            return;
        }

        synchronized (this.storage) {
            Map<Long, CompoundTag> buckets = getRuntimeBuckets(dimensionData);
            CompoundTag bucket = buckets.get(chunkHash);
            if (bucket == null) {
                return;
            }

            ListTag<CompoundTag> entityTags = getEntityTagList(bucket);
            ListTag<CompoundTag> retained = new ListTag<>(Tag.TAG_Compound);

            for (int i = 0; i < entityTags.size(); i++) {
                CompoundTag actorTag = entityTags.get(i);
                if (actorTag.contains("UniqueID") && successfulActors.contains(actorTag.getLong("UniqueID"))) {
                    continue;
                }
                retained.add(actorTag.copy());
            }

            if (retained.size() == 0) {
                buckets.remove(chunkHash);
            } else {
                CompoundTag retainedBucket = bucket.copy();
                retainedBucket.putList("EntityTagList", retained);
                buckets.put(chunkHash, retainedBucket);
            }
        }

        chunk.setChanged();
    }

    private Entity materializeActor(IChunk chunk, CompoundTag limboActor) {
        if (!limboActor.containsByte("LimboVersion") || limboActor.getByte("LimboVersion") != LIMBO_VERSION) {
            return null;
        }
        if (!limboActor.contains("UniqueID") || limboActor.getLong("UniqueID") == 0
                || !limboActor.containsString("identifier") || !isInChunk(limboActor, chunk.getX(), chunk.getZ())) {
            return null;
        }

        long actorUniqueId = limboActor.getLong("UniqueID");
        try {
            LevelDBActorStorage.getActorStorageKey(actorUniqueId);
        } catch (IllegalArgumentException e) {
            return null;
        }

        if (chunk.getLevel().getEntityByUniqueId(actorUniqueId) != null) {
            return null;
        }

        CompoundTag actorTag = limboActor.copy();
        actorTag.remove("LimboVersion");

        try {
            Entity entity = Entity.createEntity(actorTag.getString("identifier"), chunk, actorTag);
            return entity != null && !entity.isClosed() ? entity : null;
        } catch (Exception e) {
            log.error("Failed to materialize Limbo entity {} in chunk [{},{}]", actorTag.getString("identifier"), chunk.getX(), chunk.getZ(), e);
            return null;
        }
    }

    private Map<Long, CompoundTag> getRuntimeBuckets(DimensionData dimensionData) {
        Map<Long, CompoundTag> buckets = this.runtimeBuckets.get(dimensionData.getDimensionId());
        Preconditions.checkState(buckets != null, "LimboEntities runtime state is not loaded for dimension %s",
                dimensionData.getDimensionId());
        return buckets;
    }

    private CompoundTag readCanonicalRoot(DimensionData dimensionData) {
        CompoundTag root = this.storage.readGlobalCompound(getDimensionKey(dimensionData));
        Preconditions.checkState(root != null, "Missing LimboEntities dimension data for dimension %s", dimensionData.getDimensionId());
        Preconditions.checkState(root.containsCompound("data"), "Canonical dimension data is missing data compound");

        CompoundTag data = root.getCompound("data");
        Preconditions.checkState(data.containsList("LimboEntities"), "Canonical dimension data is missing LimboEntities");

        ListTag<?> buckets = data.getList("LimboEntities");
        Preconditions.checkState(buckets.type == Tag.TAG_Compound, "LimboEntities is not List<Compound>");
        return root;
    }

    private static ListTag<CompoundTag> getLimboBuckets(CompoundTag data) {
        return data.getList("LimboEntities", CompoundTag.class);
    }

    private static void validateBucket(CompoundTag bucket) {
        Preconditions.checkState(bucket.containsInt("ChunkX"), "LimboEntities bucket is missing INT ChunkX");
        Preconditions.checkState(bucket.containsInt("ChunkZ"), "LimboEntities bucket is missing INT ChunkZ");
        Preconditions.checkState(bucket.containsList("EntityTagList"), "LimboEntities bucket is missing EntityTagList");

        ListTag<?> entityTags = bucket.getList("EntityTagList");
        Preconditions.checkState(entityTags.type == Tag.TAG_Compound, "EntityTagList is not List<Compound>");
    }

    private static ListTag<CompoundTag> getEntityTagList(CompoundTag bucket) {
        return bucket.getList("EntityTagList", CompoundTag.class);
    }

    private static ListTag<CompoundTag> copyCompoundList(ListTag<CompoundTag> source) {
        ListTag<CompoundTag> copy = new ListTag<>(Tag.TAG_Compound);
        for (int i = 0; i < source.size(); i++) {
            copy.add(source.get(i).copy());
        }
        return copy;
    }

    private static boolean isInChunk(CompoundTag actorTag, int chunkX, int chunkZ) {
        if (!actorTag.containsList("Pos")) {
            return false;
        }

        ListTag<? extends Tag> pos = actorTag.getList("Pos");
        if (pos.size() < 3 || !(pos.get(0) instanceof NumberTag<?>) || !(pos.get(2) instanceof NumberTag<?>)) {
            return false;
        }

        int actorChunkX = (int) Math.floor(((NumberTag<?>) pos.get(0)).getData().doubleValue()) >> 4;
        int actorChunkZ = (int) Math.floor(((NumberTag<?>) pos.get(2)).getData().doubleValue()) >> 4;
        return actorChunkX == chunkX && actorChunkZ == chunkZ;
    }

    private static byte[] getDimensionKey(DimensionData dimensionData) {
        return switch (dimensionData.getDimensionId()) {
            case Level.DIMENSION_OVERWORLD -> OVERWORLD_KEY;
            case Level.DIMENSION_NETHER -> NETHER_KEY;
            case Level.DIMENSION_THE_END -> THE_END_KEY;
            default -> throw new IllegalArgumentException("LimboEntities is not defined for dimension " + dimensionData.getDimensionId());
        };
    }
}
