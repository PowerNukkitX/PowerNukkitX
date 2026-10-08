package org.powernukkitx.level.format.leveldb;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.WriteBatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies canonical LevelDB LimboEntities producer and consumer transitions.
 *
 * @author Curse
 */
class LevelDBLimboEntitiesTest {
    private static final DimensionData OVERWORLD = DimensionEnum.OVERWORLD.getDimensionData();
    private static final DimensionData THE_END = DimensionEnum.THE_END.getDimensionData();
    private static final byte[] OVERWORLD_KEY = "Overworld".getBytes(StandardCharsets.UTF_8);
    private static final byte[] THE_END_KEY = "TheEnd".getBytes(StandardCharsets.UTF_8);
    private static final long ACTOR_ID = (-1L << 32) | 123L;
    private static final int SOURCE_X = 0;
    private static final int SOURCE_Z = 0;
    private static final int TARGET_X = 2;
    private static final int TARGET_Z = -1;

    @Test
    void initializesCanonicalEmptyListsAndPreservesEndData(@TempDir Path tempDir) throws Exception {
        LevelDBStorage storage = new LevelDBStorage(1, tempDir.resolve("world").toString());
        try {
            LevelDBLimboEntities limbo = new LevelDBLimboEntities(storage);
            CompoundTag dragonFight = new CompoundTag().putByte("DragonKilled", 1);
            CompoundTag endRoot = new CompoundTag().putCompound(
                    "data", new CompoundTag().putCompound("DragonFight", dragonFight));
            storage.writeGlobalCompound(THE_END_KEY, endRoot);

            limbo.initializeGeneratedDimension(OVERWORLD);
            limbo.initializeGeneratedDimension(THE_END);

            CompoundTag overworld = storage.readGlobalCompound(OVERWORLD_KEY);
            assertNotNull(overworld);
            ListTag<?> overworldLimbo = overworld.getCompound("data").getList("LimboEntities");
            assertEquals(Tag.TAG_Compound, overworldLimbo.type);
            assertEquals(0, overworldLimbo.size());

            CompoundTag theEnd = storage.readGlobalCompound(THE_END_KEY);
            assertNotNull(theEnd);
            CompoundTag endData = theEnd.getCompound("data");
            assertTrue(endData.containsCompound("DragonFight"));
            assertEquals(1, endData.getCompound("DragonFight").getByte("DragonKilled"));
            ListTag<?> endLimbo = endData.getList("LimboEntities");
            assertEquals(Tag.TAG_Compound, endLimbo.type);
            assertEquals(0, endLimbo.size());
        } finally {
            storage.close();
        }
    }

    @Test
    void mismatchMovesActorFromNormalOwnershipToCanonicalLimbo(@TempDir Path tempDir) throws Exception {
        LevelDBStorage storage = new LevelDBStorage(1, tempDir.resolve("world").toString());
        try {
            CompoundTag actor = createActor();
            LevelDBLimboEntities limbo = deferActor(storage, actor);
            long actorStorageKey = LevelDBActorStorage.getActorStorageKey(ACTOR_ID);

            byte[] sourceDigest = storage.getDb().get(LevelDBActorStorage.getDigestKey(SOURCE_X, SOURCE_Z, OVERWORLD));
            assertNotNull(sourceDigest);
            assertEquals(0, LevelDBActorStorage.readDigest(sourceDigest).size());
            assertNull(storage.getDb().get(LevelDBActorStorage.getActorKey(actorStorageKey)));

            CompoundTag root = storage.readGlobalCompound(OVERWORLD_KEY);
            ListTag<CompoundTag> buckets = root.getCompound("data").getList("LimboEntities", CompoundTag.class);
            assertEquals(1, buckets.size());

            CompoundTag bucket = buckets.get(0);
            assertEquals(TARGET_X, bucket.getInt("ChunkX"));
            assertEquals(TARGET_Z, bucket.getInt("ChunkZ"));
            ListTag<CompoundTag> actors = bucket.getList("EntityTagList", CompoundTag.class);
            assertEquals(1, actors.size());
            assertEquals(2, actors.get(0).getByte("LimboVersion"));
            assertEquals(ACTOR_ID, actors.get(0).getLong("UniqueID"));
        } finally {
            storage.close();
        }
    }

    @Test
    void failedMaterializationRemainsInLimbo(@TempDir Path tempDir) throws Exception {
        LevelDBStorage storage = new LevelDBStorage(1, tempDir.resolve("world").toString());
        try {
            LevelDBLimboEntities limbo = deferActor(storage, createActor());
            IChunk target = mockChunk(TARGET_X, TARGET_Z, OVERWORLD);
            Entity closedEntity = Mockito.mock(Entity.class);
            Mockito.when(closedEntity.isClosed()).thenReturn(true);

            try (MockedStatic<Entity> entities = Mockito.mockStatic(Entity.class)) {
                entities.when(() -> Entity.createEntity(
                        Mockito.eq("minecraft:pig"), Mockito.same(target), Mockito.any(CompoundTag.class)))
                        .thenReturn(closedEntity);
                limbo.consumeChunk(target);
            }

            CompoundTag root = storage.readGlobalCompound(OVERWORLD_KEY);
            ListTag<CompoundTag> buckets = root.getCompound("data").getList("LimboEntities", CompoundTag.class);
            assertEquals(1, buckets.size());
            assertEquals(1, buckets.get(0).getList("EntityTagList", CompoundTag.class).size());
            assertNull(storage.getDb().get(LevelDBActorStorage.getDigestKey(TARGET_X, TARGET_Z, OVERWORLD)));
        } finally {
            storage.close();
        }
    }

    @Test
    void successfulMaterializationRestoresNormalActorOwnership(@TempDir Path tempDir) throws Exception {
        LevelDBStorage storage = new LevelDBStorage(1, tempDir.resolve("world").toString());
        try {
            CompoundTag actor = createActor();
            LevelDBLimboEntities limbo = deferActor(storage, actor);
            IChunk target = mockChunk(TARGET_X, TARGET_Z, OVERWORLD);
            Entity entity = Mockito.mock(Entity.class);
            Mockito.when(entity.uniqueIdLong()).thenReturn(ACTOR_ID);
            Mockito.when(entity.isClosed()).thenReturn(false);
            Mockito.when(entity.getNbt()).thenReturn(actor.copy());

            try (MockedStatic<Entity> entities = Mockito.mockStatic(Entity.class)) {
                entities.when(() -> Entity.createEntity(
                        Mockito.eq("minecraft:pig"), Mockito.same(target), Mockito.any(CompoundTag.class)))
                        .thenAnswer(invocation -> {
                            CompoundTag materializedNbt = invocation.getArgument(2);
                            assertFalse(materializedNbt.contains("LimboVersion"));
                            return entity;
                        });
                limbo.consumeChunk(target);
            }

            CompoundTag root = storage.readGlobalCompound(OVERWORLD_KEY);
            assertEquals(0, root.getCompound("data").getList("LimboEntities", CompoundTag.class).size());

            long actorStorageKey = LevelDBActorStorage.getActorStorageKey(ACTOR_ID);
            byte[] targetDigest = storage.getDb().get(LevelDBActorStorage.getDigestKey(TARGET_X, TARGET_Z, OVERWORLD));
            assertNotNull(targetDigest);
            assertEquals(List.of(actorStorageKey), LevelDBActorStorage.readDigest(targetDigest));

            byte[] actorBytes = storage.getDb().get(LevelDBActorStorage.getActorKey(actorStorageKey));
            assertNotNull(actorBytes);
            CompoundTag persistedActor = readLittleEndianCompound(actorBytes);
            assertEquals(ACTOR_ID, persistedActor.getLong("UniqueID"));
            assertFalse(persistedActor.contains("LimboVersion"));

            byte[] digestVersion = storage.getDb().get(LevelDBKeyUtil.ACTOR_DIGEST_VERSION.getKey(TARGET_X, TARGET_Z, OVERWORLD));
            assertArrayEquals(new byte[]{0}, digestVersion);
        } finally {
            storage.close();
        }
    }

    private static LevelDBLimboEntities deferActor(LevelDBStorage storage, CompoundTag actor) throws Exception {
        LevelDBLimboEntities limbo = new LevelDBLimboEntities(storage);
        limbo.initializeGeneratedDimension(OVERWORLD);
        limbo.loadDimension(OVERWORLD);

        long actorStorageKey = LevelDBActorStorage.getActorStorageKey(ACTOR_ID);
        try (WriteBatch batch = storage.createBatch()) {
            batch.put(
                    LevelDBActorStorage.getDigestKey(SOURCE_X, SOURCE_Z, OVERWORLD),
                    LevelDBActorStorage.writeDigest(List.of(actorStorageKey)));
            batch.put(LevelDBActorStorage.getActorKey(actorStorageKey), LevelDBStorage.writeLittleEndianCompound(actor));
            storage.writeBatch(batch);
        }

        limbo.deferActor(mockChunk(SOURCE_X, SOURCE_Z, OVERWORLD), actor, TARGET_X, TARGET_Z);
        return limbo;
    }

    private static CompoundTag createActor() {
        return new CompoundTag()
                .putString("identifier", "minecraft:pig")
                .putLong("UniqueID", ACTOR_ID)
                .putList("Pos", new ListTag<FloatTag>()
                        .add(new FloatTag(32.25f))
                        .add(new FloatTag(70f))
                        .add(new FloatTag(-0.5f)));
    }

    private static IChunk mockChunk(int x, int z, DimensionData dimensionData) {
        LevelProvider provider = Mockito.mock(LevelProvider.class);
        Mockito.when(provider.getDimensionData()).thenReturn(dimensionData);

        IChunk chunk = Mockito.mock(IChunk.class);
        Mockito.when(chunk.getProvider()).thenReturn(provider);
        Mockito.when(chunk.getX()).thenReturn(x);
        Mockito.when(chunk.getZ()).thenReturn(z);
        return chunk;
    }

    private static CompoundTag readLittleEndianCompound(byte[] bytes) throws Exception {
        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes);
             var reader = NbtUtils.createReaderLE(input)) {
            return CompoundTag.fromNetwork((NbtMap) reader.readTag());
        }
    }
}
