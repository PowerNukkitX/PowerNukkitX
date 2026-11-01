package org.powernukkitx.level.format.palette;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Caches the stored version and block state hash of persistent palette entries by their exact stored
 * bytes, so a repeated entry skips the NBT parse. Thread-safe; an entry is remembered only when the
 * skip-only scan ends exactly where the real NBT reader stopped, otherwise the normal path is taken.
 */
final class PaletteEntryHashCache {
    static final int MAX_ENTRY_BYTES = 512;
    /** Heap budget for stored entries; once spent, entries are read normally instead of evicting. */
    static final long MAX_BYTES = 2L * 1024 * 1024;
    /** Heap cost of one entry besides its bytes: key, value, map node, table slot, array header. */
    static final int ENTRY_OVERHEAD = 112;
    private static final int MAX_DEPTH = 16;
    private static final ConcurrentHashMap<Probe, Entry> CACHE = new ConcurrentHashMap<>();
    private static final AtomicLong USED_BYTES = new AtomicLong();

    record Entry(int storedVersion, int blockRuntimeId) {
    }

    private PaletteEntryHashCache() {
    }

    /** @return a probe for the entry at the reader index, or {@code null} if it cannot be cached */
    static @Nullable Probe probe(ByteBuf buf) {
        final int start = buf.readerIndex();
        final int end = scanTagEnd(buf, start);
        if (end < 0 || end - start > MAX_ENTRY_BYTES) return null;
        return new Probe(buf, start, end);
    }

    /** On a hit the reader index is moved past the entry; on a miss the buffer is left untouched. */
    static @Nullable Entry lookup(Probe probe, ByteBuf buf) {
        final Entry entry = CACHE.get(probe);
        if (entry != null) buf.readerIndex(probe.end);
        return entry;
    }

    static void remember(Probe probe, int readerEnd, int storedVersion, int blockRuntimeId) {
        if (probe.end != readerEnd) return;
        final long cost = probe.bytes.length + ENTRY_OVERHEAD;
        if (USED_BYTES.get() + cost > MAX_BYTES) return;
        if (USED_BYTES.addAndGet(cost) > MAX_BYTES) {
            USED_BYTES.addAndGet(-cost);
            return;
        }
        if (CACHE.putIfAbsent(probe, new Entry(storedVersion, blockRuntimeId)) != null) {
            USED_BYTES.addAndGet(-cost);
        }
    }

    static long usedBytes() {
        return USED_BYTES.get();
    }

    static int size() {
        return CACHE.size();
    }

    static synchronized void clear() {
        CACHE.clear();
        USED_BYTES.set(0);
    }

    /** @return the index just past the root tag at {@code start}, or -1 if it cannot be scanned */
    static int scanTagEnd(ByteBuf buf, int start) {
        final int limit = (int) Math.min(buf.writerIndex(), (long) start + MAX_ENTRY_BYTES);
        if ((long) start + 3 > limit) return -1;
        final int type = buf.getUnsignedByte(start);
        if (type == 0) return -1;
        return skipPayload(buf, type, fits((long) start + 3 + buf.getUnsignedShortLE(start + 1), limit), limit, MAX_DEPTH);
    }

    private static int skipPayload(ByteBuf buf, int type, int pos, int limit, int depth) {
        if (pos < 0) return -1;
        switch (type) {
            case 1: return fits((long) pos + 1, limit);                   // byte
            case 2: return fits((long) pos + 2, limit);                   // short
            case 3: case 5: return fits((long) pos + 4, limit);           // int, float
            case 4: case 6: return fits((long) pos + 8, limit);           // long, double
            case 7: return array(buf, pos, limit, 1);                     // byte[]
            case 8:                                                       // string
                if ((long) pos + 2 > limit) return -1;
                return fits((long) pos + 2 + buf.getUnsignedShortLE(pos), limit);
            case 9: {                                                     // list
                if (depth <= 0 || (long) pos + 5 > limit) return -1;
                final int elementType = buf.getUnsignedByte(pos);
                final int count = buf.getIntLE(pos + 1);
                pos += 5;
                if (count < 0) return -1;
                if (count == 0) return pos;
                if (elementType == 0) return -1;
                for (int i = 0; i < count && pos >= 0; i++) pos = skipPayload(buf, elementType, pos, limit, depth - 1);
                return pos;
            }
            case 10: {                                                    // compound
                if (depth <= 0) return -1;
                while (true) {
                    if ((long) pos + 1 > limit) return -1;
                    final int childType = buf.getUnsignedByte(pos++);
                    if (childType == 0) return pos;
                    if ((long) pos + 2 > limit) return -1;
                    pos = fits((long) pos + 2 + buf.getUnsignedShortLE(pos), limit);
                    pos = skipPayload(buf, childType, pos, limit, depth - 1);
                    if (pos < 0) return -1;
                }
            }
            case 11: return array(buf, pos, limit, 4);                    // int[]
            case 12: return array(buf, pos, limit, 8);                    // long[]
            default: return -1;
        }
    }

    private static int array(ByteBuf buf, int pos, int limit, int elementSize) {
        if ((long) pos + 4 > limit) return -1;
        final int length = buf.getIntLE(pos);
        if (length < 0) return -1;
        return fits((long) pos + 4 + (long) length * elementSize, limit);
    }

    private static int fits(long end, int limit) {
        return end <= limit ? (int) end : -1;
    }

    /** Copy of an entry's stored bytes and its end index; keeps no reference to the source buffer. */
    static final class Probe {
        private final byte[] bytes;
        private final int hash;
        private final int end;

        private Probe(ByteBuf buf, int start, int end) {
            this.end = end;
            this.bytes = new byte[end - start];
            buf.getBytes(start, this.bytes);
            this.hash = Arrays.hashCode(this.bytes);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Probe other && other.hash == hash && Arrays.equals(other.bytes, bytes);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
