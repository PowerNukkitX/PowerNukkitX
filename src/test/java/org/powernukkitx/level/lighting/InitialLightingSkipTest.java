package org.powernukkitx.level.lighting;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockDaylightDetector;
import org.powernukkitx.block.BlockGlowstone;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockStone;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.level.generator.ChunkGenerationState;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InitialLightingSkipTest {
    private static final int BASE_X = -300;
    private static final int BASE_Z = -420;

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

    /**
     * A stone roof spans the border of two chunks and is open on the west side only, so sky light
     * reaches the east chunk's part of the roof only sideways through the west chunk. Once every
     * chunk around them is lit, in whatever order, the roofed chunks must have the same light as
     * when sky light is spread into every neighbour.
     */
    @Test
    void skippingUnlitNeighborsLightsChunksTheSame() throws Exception {
        final String name = "initial_lighting_" + ProcessHandle.current().pid() + "_" + System.nanoTime();
        this.dir = new File("src/test/resources/" + name);
        FileUtils.copyDirectory(new File("src/test/resources/level"), this.dir);
        this.level = new Level(ServerMockFixture.server, name, this.dir.getPath(), 0, LevelDBProvider.class,
                new LevelConfig.GeneratorConfig("flat", 4242L, false, LevelConfig.AntiXrayMode.LOW, true,
                        DimensionEnum.OVERWORLD.getDimensionData(), new HashMap<>()));
        this.level.initLevel();
        final Level level = this.level;
        for (int dx = -1; dx <= 2; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                final int chunkX = BASE_X + dx;
                final int chunkZ = BASE_Z + dz;
                CompletableFuture.runAsync(() -> level.syncGenerateChunkFully(chunkX, chunkZ), task -> new Thread(task).start())
                        .get(120, TimeUnit.SECONDS);
            }
        }

        final Chunk west = (Chunk) level.getPhysicalChunkIfLoaded(BASE_X, BASE_Z);
        final Chunk east = (Chunk) level.getPhysicalChunkIfLoaded(BASE_X + 1, BASE_Z);
        assertEquals(ChunkGenerationState.COMPLETE, west.getGenerationState());
        assertEquals(ChunkGenerationState.COMPLETE, east.getGenerationState());
        final int roofY = west.getHeightMap(0, 0) + 4;
        for (int z = 0; z < 16; z++) {
            for (int x = 8; x < 16; x++) {
                west.setBlockState(x, roofY, z, BlockStone.PROPERTIES.getDefaultState());
                east.setBlockState(x - 8, roofY, z, BlockStone.PROPERTIES.getDefaultState());
            }
        }
        west.setBlockState(14, roofY - 1, 3, BlockGlowstone.PROPERTIES.getDefaultState());
        west.setBlockState(13, roofY - 1, 1, BlockDaylightDetector.PROPERTIES.getDefaultState());
        west.recalculateHeightMap();
        east.recalculateHeightMap();

        final List<Chunk> loaded = new ArrayList<>();
        final List<Chunk> area = new ArrayList<>();
        for (int dx = -2; dx <= 3; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (level.getPhysicalChunkIfLoaded(BASE_X + dx, BASE_Z + dz) instanceof Chunk chunk) {
                    loaded.add(chunk);
                    if (dx >= -1 && dx <= 2 && Math.abs(dz) <= 1) area.add(chunk);
                }
            }
        }
        final List<Chunk> roofed = List.of(west, east);
        final List<Chunk> roofFirst = new ArrayList<>(roofed);
        area.stream().filter(chunk -> !roofed.contains(chunk)).forEach(roofFirst::add);
        final List<Chunk> roofLast = new ArrayList<>(roofFirst.subList(2, roofFirst.size()));
        roofLast.add(east);
        roofLast.add(west);

        for (List<Chunk> order : List.of(roofFirst, roofLast)) {
            final String full = lightAll(loaded, order, roofed, false);
            assertTrue(east.getSection((roofY - 1) >> 4).getBlockSkyLight(2, (roofY - 1) & 0x0f, 8) > 0,
                    "no light reached under the roof of the east chunk");
            final int detectorLight = level.getBlockSkyLightAt(west.getX() << 4 | 13, roofY - 1, west.getZ() << 4 | 1);
            assertEquals(full, lightAll(loaded, order, roofed, true),
                    "light differs when lighting " + (order.get(0) == west ? "the roofed chunks first" : "the roofed chunks last"));
            assertEquals(detectorLight, level.getBlockSkyLightAt(west.getX() << 4 | 13, roofY - 1, west.getZ() << 4 | 1),
                    "the daylight detector sees different sky light");
        }
        assertTrue(east.getSection((roofY - 1) >> 4).getBlockLight(1, (roofY - 1) & 0x0f, 3) > 0,
                "the glowstone does not light the east chunk");

        final int lampX = west.getX() << 4 | 15;
        final int eastX = east.getX() << 4;
        final int z = west.getZ() << 4 | 8;
        final int before = level.getBlockLightAt(eastX, roofY - 1, z);
        level.setBlock(lampX, roofY - 1, z, Block.get(BlockID.LIT_REDSTONE_LAMP), false, true);
        level.updateBlockLight();
        level.flushRunTimeLighting();
        assertEquals(14, level.getBlockLightAt(eastX, roofY - 1, z), "a lit redstone lamp does not light across the border");
        level.setBlock(lampX, roofY - 1, z, Block.get(BlockID.REDSTONE_LAMP), false, true);
        level.updateBlockLight();
        level.flushRunTimeLighting();
        assertEquals(before, level.getBlockLightAt(eastX, roofY - 1, z), "an unlit redstone lamp still lights across the border");

        final int holeX = eastX | 4;
        level.setBlock(holeX, roofY, z, Block.get(BlockID.AIR), false, true);
        level.updateBlockLight();
        level.flushRunTimeLighting();
        assertEquals(15, level.getBlockSkyLightAt(holeX, roofY - 1, z), "sky light does not fall through a hole in the roof");
        assertEquals(14, level.getBlockSkyLightAt(holeX + 1, roofY - 1, z), "sky light does not spread sideways under the roof");
    }

    private static String lightAll(List<Chunk> loaded, List<Chunk> order, List<Chunk> lit, boolean skip) throws Exception {
        for (int pass = 0; pass < 2; pass++) {
            for (Chunk chunk : loaded) {
                for (ChunkSection section : chunk.getSections()) {
                    if (section == null) continue;
                    section.lighting().setAllSkyLight((byte) 0);
                    section.lighting().setAllBlockLight((byte) 0);
                    section.lighting().setNeedsInitLighting(true);
                }
                for (ChunkLightingState state : ChunkLightingState.values()) {
                    if (chunk.compareAndSetLightingState(state, ChunkLightingState.NEEDS_LIGHTING)) break;
                }
            }
            for (Chunk chunk : order) {
                assertTrue(chunk.compareAndSetLightingState(ChunkLightingState.NEEDS_LIGHTING, ChunkLightingState.LIGHTING));
                DirectSkyInitializer.prepare(chunk);
                final InitialSubChunkLighter lighter = new InitialSubChunkLighter(chunk, skip);
                for (int sectionY = chunk.getDimensionData().getMinSectionY(); sectionY <= chunk.getDimensionData().getMaxSectionY(); sectionY++) {
                    final ChunkSection section = chunk.getSection(sectionY);
                    if (section == null || !section.needsInitLighting()) continue;
                    assertTrue(lighter.processSection(sectionY));
                    section.setNeedsInitLighting(false);
                }
                assertTrue(chunk.compareAndSetLightingState(ChunkLightingState.LIGHTING, ChunkLightingState.LOADED));
            }
        }

        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] sky = new byte[ChunkSection.SIZE];
        final byte[] block = new byte[ChunkSection.SIZE];
        for (Chunk chunk : lit) {
            for (ChunkSection section : chunk.getSections()) {
                if (section == null) {
                    digest.update((byte) 0);
                    continue;
                }
                section.lighting().copySkyLightTo(sky);
                section.lighting().copyBlockLightTo(block);
                digest.update(sky);
                digest.update(block);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
