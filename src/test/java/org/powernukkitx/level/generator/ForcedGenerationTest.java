package org.powernukkitx.level.generator;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForcedGenerationTest {
    private static final int CHUNK_X = 1000;
    private static final int CHUNK_Z = -1000;
    private static final int SEARCH_RADIUS = 8;

    private Level level;
    private File dir;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @AfterEach
    void tearDown() {
        if (this.level != null) {
            try {
                this.level.close();
            } catch (Throwable ignored) {
            }
        }
        if (this.dir != null) {
            FileUtils.deleteQuietly(this.dir);
        }
    }

    @Test
    void forcedRequestDrivesOnlyTheChunksItDependsOn() throws Exception {
        final String name = "forced_generation_" + ProcessHandle.current().pid() + "_" + System.nanoTime();
        this.dir = new File("src/test/resources/" + name);
        FileUtils.copyDirectory(new File("src/test/resources/level"), this.dir);
        this.level = new Level(ServerMockFixture.server, name, this.dir.getPath(), 0, LevelDBProvider.class,
                new LevelConfig.GeneratorConfig("flat", 12345L, false, LevelConfig.AntiXrayMode.LOW, true,
                        DimensionEnum.OVERWORLD.getDimensionData(), new HashMap<>()));
        this.level.initLevel();

        final Level level = this.level;
        CompletableFuture.runAsync(() -> level.syncGenerateChunkFully(CHUNK_X, CHUNK_Z), task -> new Thread(task).start())
                .get(60, TimeUnit.SECONDS);

        final IChunk generated = level.getPhysicalChunkIfLoaded(CHUNK_X, CHUNK_Z);
        assertEquals(ChunkGenerationState.COMPLETE, generated.getGenerationState());

        final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
        while (heldAround(level) != 0 && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        assertEquals(0, heldAround(level), "chunks around the request are still held");

        int loaded = 0;
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                if (level.getPhysicalChunkIfLoaded(CHUNK_X + dx, CHUNK_Z + dz) != null) {
                    loaded++;
                }
            }
        }
        assertTrue(loaded >= 9 && loaded < 13 * 13, "loaded " + loaded + " chunks for one request");
    }

    private static int heldAround(Level level) {
        int held = 0;
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                if (level.isChunkInUse(Level.chunkHash(CHUNK_X + dx, CHUNK_Z + dz))) {
                    held++;
                }
            }
        }
        return held;
    }
}
