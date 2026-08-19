package org.powernukkitx.level.generator.object;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockStone;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockVector3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockManagerChunkHookTest {

    private static final AtomicInteger NEXT_CHUNK_X = new AtomicInteger(4000);

    private static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    private static IChunk newChunk() {
        return level.getChunk(NEXT_CHUNK_X.getAndIncrement(), 4000, true);
    }

    private static void generate(IChunk chunk) {
        chunk.setChunkState(ChunkState.GENERATED);
        BlockManager.applyPendingSubChunkUpdates(level, chunk);
    }

    @Test
    void hookRunsImmediatelyWhenItsChunkIsGenerated() {
        IChunk chunk = newChunk();
        chunk.setChunkState(ChunkState.GENERATED);
        AtomicInteger runs = new AtomicInteger();
        BlockManager manager = new BlockManager(level);
        manager.addHook(chunk.getX(), chunk.getZ(), runs::incrementAndGet);

        manager.applySubChunkUpdate();

        assertEquals(1, runs.get());
    }

    @Test
    void hookWaitsUntilItsChunkIsGenerated() {
        IChunk chunk = newChunk();
        AtomicInteger runs = new AtomicInteger();
        BlockManager manager = new BlockManager(level);
        manager.addHook(chunk.getX(), chunk.getZ(), runs::incrementAndGet);

        manager.applySubChunkUpdate();
        assertEquals(0, runs.get());

        generate(chunk);
        assertEquals(1, runs.get());

        BlockManager.applyPendingSubChunkUpdates(level, chunk);
        assertEquals(1, runs.get());
    }

    @Test
    void waitingHookRunsAfterTheBlocksHeldWithIt() {
        IChunk chunk = newChunk();
        BlockVector3 pos = new BlockVector3(chunk.getX() << 4, 70, chunk.getZ() << 4);
        AtomicReference<String> seen = new AtomicReference<>();
        BlockManager manager = new BlockManager(level);
        manager.setBlockStateAt(pos, BlockStone.PROPERTIES.getDefaultState());
        manager.addHook(pos, () -> seen.set(level.getBlock(pos.getX(), pos.getY(), pos.getZ()).getId()));

        manager.applySubChunkUpdate();
        assertNull(seen.get());
        assertTrue(chunk.getExtraData().contains("pendingSubChunkUpdates"));

        generate(chunk);
        assertEquals(BlockID.STONE, seen.get());
    }

    @Test
    void hookRunsImmediatelyOnceItsChunkHasAlreadyBeenGenerated() {
        IChunk chunk = newChunk();
        BlockManager blocks = new BlockManager(level);
        blocks.setBlockStateAt(chunk.getX() << 4, 70, chunk.getZ() << 4, BlockStone.PROPERTIES.getDefaultState());
        blocks.applySubChunkUpdate();
        generate(chunk);

        AtomicInteger runs = new AtomicInteger();
        BlockManager manager = new BlockManager(level);
        manager.addHook(chunk.getX(), chunk.getZ(), runs::incrementAndGet);
        manager.applySubChunkUpdate();

        assertEquals(1, runs.get());
    }

    @Test
    void mergedHooksKeepWaitingOnTheirChunk() {
        IChunk chunk = newChunk();
        AtomicInteger runs = new AtomicInteger();
        BlockManager piece = new BlockManager(level);
        piece.addHook(chunk.getX(), chunk.getZ(), runs::incrementAndGet);
        BlockManager root = new BlockManager(level);
        root.merge(piece);

        root.applySubChunkUpdate();
        assertEquals(0, runs.get());

        generate(chunk);
        assertEquals(1, runs.get());
    }

    @Test
    void failingWaitingHookDoesNotStopTheOthers() {
        IChunk chunk = newChunk();
        AtomicInteger runs = new AtomicInteger();
        BlockManager manager = new BlockManager(level);
        manager.addHook(chunk.getX(), chunk.getZ(), () -> {
            throw new IllegalStateException("failing hook");
        });
        manager.addHook(chunk.getX(), chunk.getZ(), runs::incrementAndGet);
        manager.applySubChunkUpdate();

        assertDoesNotThrow(() -> generate(chunk));
        assertEquals(1, runs.get());
    }

    @Test
    void clearingTheHooksOfALevelDropsThoseWaiting() {
        IChunk chunk = newChunk();
        AtomicInteger runs = new AtomicInteger();
        BlockManager manager = new BlockManager(level);
        manager.addHook(chunk.getX(), chunk.getZ(), runs::incrementAndGet);
        manager.applySubChunkUpdate();

        BlockManager.clearPendingHooks(level.getId());
        generate(chunk);

        assertEquals(0, runs.get());
    }

    @Test
    void chunkThatWaitedTheLongestIsDroppedPastTheBound() throws IllegalAccessException {
        int bound = (int) FieldUtils.readDeclaredStaticField(BlockManager.class, "MAX_PENDING_HOOK_CHUNKS", true);
        int chunkZ = 8000;
        AtomicInteger oldestRuns = new AtomicInteger();
        AtomicInteger newestRuns = new AtomicInteger();
        BlockManager.clearPendingHooks(level.getId());

        BlockManager oldest = new BlockManager(level);
        oldest.addHook(0, chunkZ, oldestRuns::incrementAndGet);
        oldest.applySubChunkUpdate();
        BlockManager others = new BlockManager(level);
        for (int chunkX = 1; chunkX < bound; chunkX++) {
            others.addHook(chunkX, chunkZ, () -> {
            });
        }
        others.applySubChunkUpdate();
        BlockManager newest = new BlockManager(level);
        newest.addHook(bound, chunkZ, newestRuns::incrementAndGet);
        newest.applySubChunkUpdate();

        generate(level.getChunk(0, chunkZ, true));
        generate(level.getChunk(bound, chunkZ, true));
        BlockManager.clearPendingHooks(level.getId());

        assertEquals(0, oldestRuns.get());
        assertEquals(1, newestRuns.get());
    }
}
