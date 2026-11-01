package org.powernukkitx.level.format.palette;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powernukkitx.utils.HashUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks that a cached palette entry read matches the full NBT read it replaces, hit or miss. */
class PaletteEntryHashCacheTest {

    @BeforeEach
    void reset() {
        PaletteEntryHashCache.clear();
    }

    @Test
    void identicalEntriesHitAfterTheFirstRead() throws IOException {
        byte[] stone = stored(state("minecraft:stone", NbtMap.EMPTY), 18168865);
        ByteBuf buf = Unpooled.wrappedBuffer(stone, stone, stone);

        int[] expected = fullRead(Unpooled.wrappedBuffer(stone));
        for (int i = 0; i < 3; i++) {
            int[] actual = cachedRead(buf);
            assertEquals(expected[0], actual[0], "stored version");
            assertEquals(expected[1], actual[1], "block state hash");
            assertEquals(stone.length * (i + 1), buf.readerIndex(), "reader position after entry " + i);
        }
        assertEquals(1, PaletteEntryHashCache.size());
    }

    @Test
    void differentVersionsAreCachedSeparatelyAndHashTheSame() throws IOException {
        NbtMap tag = state("minecraft:oak_log", NbtMap.builder().putString("pillar_axis", "y").build());
        byte[] older = stored(tag, 17959425);
        byte[] newer = stored(tag, 18168865);
        byte[] none = stored(tag, 0);

        for (int pass = 0; pass < 2; pass++) {
            int[] a = cachedRead(Unpooled.wrappedBuffer(older));
            int[] b = cachedRead(Unpooled.wrappedBuffer(newer));
            int[] c = cachedRead(Unpooled.wrappedBuffer(none));
            assertEquals(17959425, a[0]);
            assertEquals(18168865, b[0]);
            assertEquals(0, c[0]);
            assertEquals(a[1], b[1], "version is not part of the hash");
            assertEquals(a[1], c[1], "version is not part of the hash");
        }
        assertEquals(3, PaletteEntryHashCache.size());
    }

    @Test
    void hitsMoveEachBufferToItsOwnEnd() throws IOException {
        byte[] entry = stored(state("minecraft:gravel", NbtMap.EMPTY), 18168865);
        cachedRead(Unpooled.wrappedBuffer(entry));

        for (int lead : new int[]{0, 1, 13, 400}) {
            ByteBuf buf = Unpooled.wrappedBuffer(new byte[lead], entry, new byte[]{1, 2, 3});
            buf.readerIndex(lead);
            PaletteEntryHashCache.Probe probe = PaletteEntryHashCache.probe(buf);
            assertNotNull(probe, "lead " + lead);
            assertNotNull(PaletteEntryHashCache.lookup(probe, buf), "lead " + lead);
            assertEquals(lead + entry.length, buf.readerIndex(), "lead " + lead);
        }
        assertEquals(1, PaletteEntryHashCache.size());
    }

    @Test
    void tagsNestedDeeperThanTheReaderAllowsAreNotProbed() {
        // Laid out by hand: the NBT writer refuses this depth.
        int depth = 20;
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(10).writeShortLE(0);
        for (int i = 0; i < depth; i++) buf.writeByte(10).writeShortLE(1).writeByte('c');
        for (int i = 0; i <= depth; i++) buf.writeByte(0);

        assertNull(PaletteEntryHashCache.probe(buf));
        assertEquals(0, buf.readerIndex());
    }

    @Test
    void unknownTagTypesAreNotProbed() throws IOException {
        byte[] entry = stored(state("minecraft:stone", NbtMap.builder().putByte("x", (byte) 1).build()), 18168865);
        // Replace the type byte of "states"'s only child with 13, which no NBT version defines.
        int index = indexOf(entry, new byte[]{1, 1, 0, 'x'});
        assertTrue(index > 0);
        entry[index] = 13;

        assertNull(PaletteEntryHashCache.probe(Unpooled.wrappedBuffer(entry)));
        assertEquals(-1, PaletteEntryHashCache.scanTagEnd(Unpooled.wrappedBuffer(entry), 0));
    }

    @Test
    void simultaneousInsertsOfTheSameEntryStoreItOnce() throws Exception {
        byte[] entry = stored(state("minecraft:sand", NbtMap.EMPTY), 18168865);
        int[] expected = fullRead(Unpooled.wrappedBuffer(entry));
        int threads = 16;
        CyclicBarrier start = new CyclicBarrier(threads);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<int[]>> futures = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                futures.add(pool.submit(() -> {
                    ByteBuf buf = Unpooled.wrappedBuffer(entry);
                    PaletteEntryHashCache.Probe probe = PaletteEntryHashCache.probe(buf);
                    int[] result = fullRead(buf);
                    start.await();
                    PaletteEntryHashCache.remember(probe, buf.readerIndex(), result[0], result[1]);
                    return result;
                }));
            }
            for (Future<int[]> future : futures) assertArrayEquals(expected, future.get(30, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
        }
        assertEquals(1, PaletteEntryHashCache.size());
        assertEquals(entry.length + PaletteEntryHashCache.ENTRY_OVERHEAD, PaletteEntryHashCache.usedBytes(),
                "duplicates must give back their reservation");
    }

    @Test
    void entriesWithCollidingHashesAreKeptApart() throws IOException {
        // "aa" and "bB" have the same Arrays.hashCode (97 * 31 + 97 == 98 * 31 + 66) and length.
        byte[] first = stored(state("minecraft:collide_aa", NbtMap.EMPTY), 18168865);
        byte[] second = stored(state("minecraft:collide_bB", NbtMap.EMPTY), 18168865);
        assertEquals(Arrays.hashCode(first), Arrays.hashCode(second));

        cachedRead(Unpooled.wrappedBuffer(first));
        assertArrayEquals(fullRead(Unpooled.wrappedBuffer(second)), cachedRead(Unpooled.wrappedBuffer(second)));
        assertArrayEquals(fullRead(Unpooled.wrappedBuffer(first)), cachedRead(Unpooled.wrappedBuffer(first)));
        assertEquals(2, PaletteEntryHashCache.size());
    }

    @Test
    void nestedTagsAreScannedToTheSameEndAsTheReader() throws IOException {
        NbtMap inner = NbtMap.builder()
                .putIntArray("ints", new int[]{1, 2, 3})
                .putLongArray("longs", new long[]{4L, 5L})
                .putByteArray("bytes", new byte[]{6, 7})
                .putDouble("d", 1.5)
                .putShort("s", (short) 8)
                .build();
        NbtMap states = NbtMap.builder()
                .putCompound("inner", inner)
                .putList("list", NbtType.COMPOUND, List.of(inner, inner))
                .putList("empty", NbtType.END, List.of())
                .build();
        byte[] entry = stored(state("minecraft:custom_nested", states), 18168865);

        assertEquals(entry.length, PaletteEntryHashCache.scanTagEnd(Unpooled.wrappedBuffer(entry), 0));
        int[] expected = fullRead(Unpooled.wrappedBuffer(entry));
        assertArrayEquals(expected, cachedRead(Unpooled.wrappedBuffer(entry)), "miss");
        assertArrayEquals(expected, cachedRead(Unpooled.wrappedBuffer(entry)), "hit");
    }

    @Test
    void truncatedEntriesAreNeverProbed() throws IOException {
        byte[] entry = stored(state("minecraft:stone", NbtMap.EMPTY), 18168865);
        cachedRead(Unpooled.wrappedBuffer(entry));
        for (int length = 0; length < entry.length; length++) {
            ByteBuf truncated = Unpooled.wrappedBuffer(entry, 0, length);
            assertNull(PaletteEntryHashCache.probe(truncated), "length " + length);
            assertEquals(0, truncated.readerIndex(), "length " + length);
        }
    }

    @Test
    void readerIndexMatchesAFullReadOnMissAndHit() throws IOException {
        byte[] dirt = stored(state("minecraft:dirt", NbtMap.EMPTY), 18168865);
        byte[] lead = new byte[7];
        byte[] trailing = {10, 0, 0};
        ByteBuf buf = Unpooled.wrappedBuffer(lead, dirt, dirt, trailing);
        buf.readerIndex(lead.length);

        PaletteEntryHashCache.Probe probe = PaletteEntryHashCache.probe(buf);
        assertNotNull(probe);
        assertNull(PaletteEntryHashCache.lookup(probe, buf));
        assertEquals(lead.length, buf.readerIndex(), "a miss must leave the buffer untouched");

        cachedRead(buf);
        assertEquals(lead.length + dirt.length, buf.readerIndex(), "after miss and full read");
        cachedRead(buf);
        assertEquals(lead.length + 2 * dirt.length, buf.readerIndex(), "after hit");
    }

    @Test
    void entriesAboveTheSizeLimitAreNotCached() throws IOException {
        NbtMap states = NbtMap.builder().putString("big", "x".repeat(PaletteEntryHashCache.MAX_ENTRY_BYTES)).build();
        byte[] entry = stored(state("minecraft:custom_big", states), 18168865);

        assertNull(PaletteEntryHashCache.probe(Unpooled.wrappedBuffer(entry)));
        assertArrayEquals(fullRead(Unpooled.wrappedBuffer(entry)), cachedRead(Unpooled.wrappedBuffer(entry)));
        assertEquals(0, PaletteEntryHashCache.size());
    }

    @Test
    void byteBudgetIsNeverExceeded() throws IOException {
        // Each entry costs well over 64 bytes, so this is more than the budget holds.
        int attempts = (int) (PaletteEntryHashCache.MAX_BYTES / 64);
        for (int i = 0; i < attempts; i++) {
            byte[] entry = stored(state("minecraft:custom_" + i, NbtMap.EMPTY), 18168865);
            assertArrayEquals(fullRead(Unpooled.wrappedBuffer(entry)), cachedRead(Unpooled.wrappedBuffer(entry)));
        }
        assertTrue(PaletteEntryHashCache.usedBytes() <= PaletteEntryHashCache.MAX_BYTES);
        assertTrue(PaletteEntryHashCache.size() > 0);
        assertTrue(PaletteEntryHashCache.size() < attempts, "the budget must have stopped storing");

        byte[] extra = stored(state("minecraft:custom_after_full", NbtMap.EMPTY), 18168865);
        int sizeBefore = PaletteEntryHashCache.size();
        assertArrayEquals(fullRead(Unpooled.wrappedBuffer(extra)), cachedRead(Unpooled.wrappedBuffer(extra)));
        assertEquals(sizeBefore, PaletteEntryHashCache.size());
    }

    @Test
    void concurrentReadersGetCorrectResultsWithinBudget() throws Exception {
        List<byte[]> entries = new ArrayList<>();
        List<int[]> expected = new ArrayList<>();
        for (int i = 0; i < 20_000; i++) {
            byte[] entry = stored(state("minecraft:concurrent_" + (i % 5_000), NbtMap.EMPTY), i % 3);
            entries.add(entry);
            expected.add(fullRead(Unpooled.wrappedBuffer(entry)));
        }

        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int t = 0; t < 8; t++) {
                final int offset = t * 997;
                futures.add(pool.submit(() -> {
                    for (int i = 0; i < entries.size(); i++) {
                        int index = (i + offset) % entries.size();
                        int[] actual = cachedRead(Unpooled.wrappedBuffer(entries.get(index)));
                        assertArrayEquals(expected.get(index), actual, "entry " + index);
                    }
                    return null;
                }));
            }
            for (Future<?> future : futures) future.get(60, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }
        assertTrue(PaletteEntryHashCache.usedBytes() <= PaletteEntryHashCache.MAX_BYTES);
    }

    /** The head of {@link Palette#addBlockPalette} before the cache. */
    private static int[] fullRead(ByteBuf buf) throws IOException {
        try (NBTInputStream input = NbtUtils.createReaderLE(new ByteBufInputStream(buf))) {
            NbtMap tag = (NbtMap) input.readTag();
            int storedVersion = tag.getInt("version");
            NbtMapBuilder builder = tag.toBuilder();
            builder.remove("version");
            return new int[]{storedVersion, HashUtils.fnv1a_32_nbt(builder.build()), buf.readerIndex()};
        }
    }

    /** The head of {@link Palette#addBlockPalette} with the cache. */
    private static int[] cachedRead(ByteBuf buf) throws IOException {
        int start = buf.readerIndex();
        PaletteEntryHashCache.Probe probe = PaletteEntryHashCache.probe(buf);
        PaletteEntryHashCache.Entry cached = probe == null ? null : PaletteEntryHashCache.lookup(probe, buf);
        if (cached != null) {
            return new int[]{cached.storedVersion(), cached.blockRuntimeId(), buf.readerIndex() - start};
        }
        int[] result = fullRead(buf);
        if (probe != null) PaletteEntryHashCache.remember(probe, buf.readerIndex(), result[0], result[1]);
        result[2] -= start;
        return result;
    }

    private static int indexOf(byte[] haystack, byte[] needle) {
        outer:
        for (int i = 0; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

    private static NbtMap state(String name, NbtMap states) {
        return NbtMap.builder().putString("name", name).putCompound("states", states).build();
    }

    private static byte[] stored(NbtMap tag, int version) throws IOException {
        NbtMap withVersion = version == 0 ? tag : tag.toBuilder().putInt("version", version).build();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTOutputStream output = NbtUtils.createWriterLE(bytes)) {
            output.writeTag(withVersion);
        }
        return bytes.toByteArray();
    }
}
