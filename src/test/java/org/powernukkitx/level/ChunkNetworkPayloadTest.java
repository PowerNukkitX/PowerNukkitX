package org.powernukkitx.level;

import org.powernukkitx.GameMockExtension;
import org.powernukkitx.block.BlockDiamondOre;
import org.powernukkitx.block.BlockGoldOre;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.format.UnsafeChunk;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import it.unimi.dsi.fastutil.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({GameMockExtension.class})
public class ChunkNetworkPayloadTest {
    private static Chunk chunkWithBlock(LevelProvider provider, int chunkX) {
        Chunk chunk = (Chunk) provider.getChunk(chunkX, 40, true);
        chunk.setBlockState(0, 100, 0, BlockGoldOre.PROPERTIES.getDefaultState());
        return chunk;
    }

    /** Builds a payload the way the provider does: begin, read sections, store, all in one batch. */
    private static Chunk.NetworkPayload cachePayload(Chunk chunk) {
        Chunk.NetworkPayload[] result = new Chunk.NetworkPayload[1];
        chunk.batchProcess(unsafeChunk -> {
            chunk.beginNetworkPayload();
            result[0] = storeCurrentSections(chunk, unsafeChunk);
        });
        return result[0];
    }

    private static Chunk.NetworkPayload storeCurrentSections(Chunk chunk, UnsafeChunk unsafeChunk) {
        ChunkSection[] sections = unsafeChunk.getSections();
        Chunk.SectionNetworkPayload[] sectionPayloads = new Chunk.SectionNetworkPayload[sections.length];
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] != null) {
                sectionPayloads[i] = chunk.getSectionNetworkPayload(sections[i]);
            }
        }
        Chunk.NetworkPayload payload = new Chunk.NetworkPayload(sectionPayloads, sections.length, new byte[0]);
        chunk.setNetworkPayload(payload);
        return payload;
    }

    @Test
    void testPayloadReturnedWhileUnchanged(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 0);
        Chunk.NetworkPayload payload = cachePayload(chunk);
        Assertions.assertSame(payload, chunk.getNetworkPayload());
    }

    @Test
    void testSetBlockStateInvalidatesPayload(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 1);
        cachePayload(chunk);
        chunk.setBlockState(0, 100, 0, BlockDiamondOre.PROPERTIES.getDefaultState());
        Assertions.assertNull(chunk.getNetworkPayload());
    }

    @Test
    void testUnsafeSetBlockStateInBatchInvalidatesPayload(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 2);
        cachePayload(chunk);
        chunk.batchProcess(unsafeChunk -> unsafeChunk.setBlockState(0, 100, 0, BlockDiamondOre.PROPERTIES.getDefaultState(), 0));
        Assertions.assertNull(chunk.getNetworkPayload());
    }

    @Test
    void testUnlockedUnsafeSetBlockStateInvalidatesPayload(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 3);
        cachePayload(chunk);
        // Generation features write into loaded neighbours this way, without the chunk lock
        new UnsafeChunk(chunk).setBlockState(0, 100, 0, BlockDiamondOre.PROPERTIES.getDefaultState(), 0);
        Assertions.assertNull(chunk.getNetworkPayload());
    }

    @Test
    void testNewSectionInvalidatesPayload(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 4);
        cachePayload(chunk);
        new UnsafeChunk(chunk).setBlockState(0, 200, 0, BlockGoldOre.PROPERTIES.getDefaultState(), 0);
        Assertions.assertNull(chunk.getNetworkPayload());
    }

    @Test
    void testBiomeChangeInvalidatesPayload(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 5);
        cachePayload(chunk);
        chunk.setBiomeId(0, 100, 0, 10);
        Assertions.assertNull(chunk.getNetworkPayload());
    }

    @Test
    void testEditDuringBuildLeavesPayloadStale(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 6);
        chunk.batchProcess(unsafeChunk -> {
            chunk.beginNetworkPayload();
            unsafeChunk.setBiomeId(0, 100, 0, 10);
            storeCurrentSections(chunk, unsafeChunk);
        });
        Assertions.assertNull(chunk.getNetworkPayload());
    }

    @Test
    void testSaveAndLightDoNotInvalidatePayload(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 7);
        Chunk.NetworkPayload payload = cachePayload(chunk);
        chunk.setChanged(false);
        chunk.setBlockSkyLight(0, 100, 0, 3);
        Assertions.assertSame(payload, chunk.getNetworkPayload());
    }

    @Test
    void testSectionPayloadReusedUntilSectionChanges(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 8);
        ChunkSection section = chunk.getSection(100 >> 4);
        Chunk.SectionNetworkPayload first = chunk.getSectionNetworkPayload(section);
        Assertions.assertSame(first, chunk.getSectionNetworkPayload(section));
        Assertions.assertSame(first.blob(), first.blob());

        chunk.setBlockState(1, 100, 0, BlockDiamondOre.PROPERTIES.getDefaultState());
        Chunk.SectionNetworkPayload second = chunk.getSectionNetworkPayload(section);
        Assertions.assertNotSame(first, second);
        Assertions.assertNotEquals(first.blob().id(), second.blob().id());
    }

    @Test
    void testProviderCachedPayloadMatchesFreshPayload(LevelProvider levelDBProvider) {
        Chunk chunk = chunkWithBlock(levelDBProvider, 9);
        byte[] fresh = requestChunkBytes(levelDBProvider, chunk);
        Assertions.assertNotNull(chunk.getNetworkPayload());
        Assertions.assertArrayEquals(fresh, requestChunkBytes(levelDBProvider, chunk));

        chunk.setBlockState(0, 100, 0, BlockDiamondOre.PROPERTIES.getDefaultState());
        byte[] edited = requestChunkBytes(levelDBProvider, chunk);
        Assertions.assertFalse(java.util.Arrays.equals(fresh, edited));
        Assertions.assertArrayEquals(edited, requestChunkBytes(levelDBProvider, chunk));
    }

    private static byte[] requestChunkBytes(LevelProvider provider, Chunk chunk) {
        Pair<ByteBuf, Integer> pair = provider.requestChunkData(chunk.getX(), chunk.getZ());
        try {
            return ByteBufUtil.getBytes(pair.first());
        } finally {
            pair.first().release();
        }
    }
}
