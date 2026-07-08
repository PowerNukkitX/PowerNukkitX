package org.powernukkitx.level.format.palette;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.bitarray.BitArrayVersion;
import org.powernukkitx.registry.Registries;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Correctness guard for the H1 change: {@code Palette} now keeps an {@code Object2IntOpenHashMap} (value -> first index)
 * in sync with the backing list instead of calling {@code List.indexOf} on every {@code paletteIndexFor}. A desync would
 * silently corrupt chunk data, so this test verifies that get/set, resize, {@code copyTo} and a storage round-trip all
 * stay consistent, and that the index map exactly matches {@code List.indexOf} for every value.
 *
 * <p>Placed in the palette package so it can read the protected {@code palette} / {@code paletteIndex} fields directly.</p>
 */
public class PaletteConsistencyTest {

    private static BlockState[] states;

    @BeforeAll
    static void init() {
        Registries.POTION.init();
        Registries.BLOCK.init();
        Registries.ITEM.init();
        Registries.ITEM_RUNTIMEID.init();
        String[] ids = {
                BlockID.AIR, BlockID.STONE, BlockID.DIRT, BlockID.GRASS_BLOCK,
                BlockID.GLOWSTONE, BlockID.WATER, BlockID.OAK_LEAVES, BlockID.SAND,
                BlockID.GRAVEL, BlockID.OAK_LOG, BlockID.COBBLESTONE, BlockID.BEDROCK,
                BlockID.DIAMOND_ORE, BlockID.IRON_ORE, BlockID.COAL_ORE, BlockID.GOLD_ORE,
                BlockID.OAK_LEAVES, BlockID.BIRCH_LOG, BlockID.CLAY, BlockID.MOSS_BLOCK,
        };
        states = new BlockState[ids.length];
        for (int i = 0; i < ids.length; i++) {
            states[i] = Block.get(ids[i]).getBlockState();
        }
    }

    /**
     * When the index map is present (large palettes), it must agree with a fresh List.indexOf for every value.
     * Small palettes keep paletteIndex == null and rely on linear indexOf, which is trivially correct.
     */
    private static <V> void assertIndexConsistent(Palette<V> palette) {
        if (palette.paletteIndex == null) {
            return;
        }
        for (V value : palette.palette) {
            Assertions.assertEquals(palette.palette.indexOf(value), palette.paletteIndex.getInt(value),
                    "paletteIndex disagrees with List.indexOf for value " + value);
        }
        // No stale entries: index size must not exceed distinct palette size.
        Assertions.assertTrue(palette.paletteIndex.size() <= palette.palette.size(),
                "paletteIndex has more entries than the palette");
    }

    @Test
    void setGetRoundTripAndResize() {
        BlockPalette palette = new BlockPalette(states[0]);
        BlockState[] expected = new BlockState[ChunkSection.SIZE];

        // Fill with enough distinct states to force at least one onResize (V2 -> larger).
        for (int i = 0; i < ChunkSection.SIZE; i++) {
            BlockState s = states[i % states.length];
            expected[i] = s;
            palette.set(i, s);
        }

        for (int i = 0; i < ChunkSection.SIZE; i++) {
            Assertions.assertEquals(expected[i], palette.get(i), "get(" + i + ") mismatch after sets");
        }
        assertIndexConsistent(palette);

        // Overwrite a swath with a single state and re-verify.
        for (int i = 0; i < 512; i++) {
            expected[i] = states[1];
            palette.set(i, states[1]);
        }
        for (int i = 0; i < ChunkSection.SIZE; i++) {
            Assertions.assertEquals(expected[i], palette.get(i), "get(" + i + ") mismatch after overwrite");
        }
        assertIndexConsistent(palette);
    }

    @Test
    void copyToKeepsIndexConsistent() {
        BlockPalette src = new BlockPalette(states[0]);
        for (int i = 0; i < ChunkSection.SIZE; i++) {
            src.set(i, states[i % states.length]);
        }

        BlockPalette dst = new BlockPalette(states[0]);
        // Dirty dst first so copyTo must fully replace its palette + index.
        for (int i = 0; i < 64; i++) dst.set(i, states[(i + 3) % states.length]);

        src.copyTo(dst);

        for (int i = 0; i < ChunkSection.SIZE; i++) {
            Assertions.assertEquals(src.get(i), dst.get(i), "copyTo value mismatch at " + i);
        }
        assertIndexConsistent(dst);

        // After copy, setting new values on dst must still resolve indices correctly.
        dst.set(0, states[5]);
        Assertions.assertEquals(states[5], dst.get(0));
        assertIndexConsistent(dst);
    }

    @Test
    void storageRuntimeRoundTrip() {
        BlockPalette original = new BlockPalette(states[0], BitArrayVersion.V2);
        for (int i = 0; i < ChunkSection.SIZE; i++) {
            original.set(i, states[i % states.length]);
        }

        ByteBuf buf = Unpooled.buffer();
        try {
            original.writeToStorageRuntime(buf, BlockState::blockStateHash, null);

            BlockPalette restored = new BlockPalette(states[0], BitArrayVersion.V2);
            restored.readFromStorageRuntime(buf, hash -> Registries.BLOCKSTATE.get(hash), null);

            for (int i = 0; i < ChunkSection.SIZE; i++) {
                Assertions.assertEquals(original.get(i), restored.get(i), "round-trip mismatch at " + i);
            }
            // The deserialize path clears+appends via the routed helpers — index must be consistent afterwards.
            assertIndexConsistent(restored);

            // And the restored palette must still accept new sets correctly.
            restored.set(10, states[7]);
            Assertions.assertEquals(states[7], restored.get(10));
            assertIndexConsistent(restored);
        } finally {
            buf.release();
        }
    }
}
