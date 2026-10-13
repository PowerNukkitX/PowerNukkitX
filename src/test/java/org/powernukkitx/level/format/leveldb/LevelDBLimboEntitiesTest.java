package org.powernukkitx.level.format.leveldb;

import org.iq80.leveldb.WriteBatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeAll
    static void boot() {
        // Opening LevelDB storage reads the server settings
        ServerMockFixture.boot();
    }

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
            assertEquals(Tag.TAG_End, overworldLimbo.type);
            assertEquals(0, overworldLimbo.size());

            CompoundTag theEnd = storage.readGlobalCompound(THE_END_KEY);
            assertNotNull(theEnd);
            CompoundTag endData = theEnd.getCompound("data");
            assertTrue(endData.containsCompound("DragonFight"));
            assertEquals(1, endData.getCompound("DragonFight").getByte("DragonKilled"));
            ListTag<?> endLimbo = endData.getList("LimboEntities");
            assertEquals(Tag.TAG_End, endLimbo.type);
            assertEquals(0, endLimbo.size());
        } finally {
            storage.close();
        }
    }

    @Test
    void loadsEmptyEndListWithoutRewritingIt(@TempDir Path tempDir) throws Exception {
        LevelDBStorage storage = new LevelDBStorage(1, tempDir.resolve("world").toString());
        try {
            CompoundTag root = new CompoundTag().putCompound("data", new CompoundTag()
                    .putList("LimboEntities", new ListTag<CompoundTag>(Tag.TAG_End)));
            storage.writeGlobalCompound(OVERWORLD_KEY, root);
            byte[] stored = storage.getDb().get(OVERWORLD_KEY);

            LevelDBLimboEntities limbo = new LevelDBLimboEntities(storage);
            limbo.initializeGeneratedDimension(OVERWORLD);
            limbo.loadDimension(OVERWORLD);

            assertArrayEquals(stored, storage.getDb().get(OVERWORLD_KEY));
        } finally {
            storage.close();
        }
    }

    @Test
    void savesEmptyListAsEndList(@TempDir Path tempDir) throws Exception {
        LevelDBStorage storage = new LevelDBStorage(1, tempDir.resolve("world").toString());
        try {
            CompoundTag root = new CompoundTag().putCompound("data", new CompoundTag()
                    .putList("LimboEntities", new ListTag<CompoundTag>(Tag.TAG_Compound)));
            storage.writeGlobalCompound(OVERWORLD_KEY, root);

            LevelDBLimboEntities limbo = new LevelDBLimboEntities(storage);
            limbo.loadDimension(OVERWORLD);
            limbo.saveDimension(OVERWORLD);

            ListTag<?> buckets = storage.readGlobalCompound(OVERWORLD_KEY).getCompound("data").getList("LimboEntities");
            assertEquals(Tag.TAG_End, buckets.type);
            assertEquals(0, buckets.size());
        } finally {
            storage.close();
        }
    }

    @Test
    void rejectsNonEmptyListOfAnotherType(@TempDir Path tempDir) throws Exception {
        LevelDBStorage storage = new LevelDBStorage(1, tempDir.resolve("world").toString());
        try {
            CompoundTag root = new CompoundTag().putCompound("data", new CompoundTag()
                    .putList("LimboEntities", new ListTag<FloatTag>().add(new FloatTag(1f))));
            storage.writeGlobalCompound(OVERWORLD_KEY, root);

            LevelDBLimboEntities limbo = new LevelDBLimboEntities(storage);
            assertThrows(IllegalStateException.class, () -> limbo.loadDimension(OVERWORLD));
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

            // Deferring only records the actor; it reaches storage with the level data, while the
            // source chunk drops it from its digest when that chunk is saved
            limbo.saveDimension(OVERWORLD);

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
            limbo.saveDimension(OVERWORLD);

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
            limbo.saveDimension(OVERWORLD);

            CompoundTag root = storage.readGlobalCompound(OVERWORLD_KEY);
            assertEquals(0, root.getCompound("data").getList("LimboEntities", CompoundTag.class).size());
            // The materialized actor is owned by the target chunk now and is written with it
            Mockito.verify(target).setChanged();
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
        // No entity is loaded yet, so materializing never finds a duplicate
        Mockito.when(chunk.getLevel()).thenReturn(Mockito.mock(Level.class));
        Mockito.when(chunk.getX()).thenReturn(x);
        Mockito.when(chunk.getZ()).thenReturn(z);
        return chunk;
    }
}
