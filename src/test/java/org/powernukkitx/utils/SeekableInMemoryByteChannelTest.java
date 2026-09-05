package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeekableInMemoryByteChannelTest {

    @Test
    void newChannelIsOpenAtPositionZero() {
        var ch = new SeekableInMemoryByteChannel();
        assertTrue(ch.isOpen());
        assertEquals(0L, ch.position());
        assertEquals(0L, ch.size());
    }

    @Test
    void ctorWithDataSetsSize() {
        var ch = new SeekableInMemoryByteChannel(new byte[]{1, 2, 3, 4});
        assertEquals(4L, ch.size());
    }

    @Test
    void ctorWithIntAllocatesZeroedBuffer() {
        var ch = new SeekableInMemoryByteChannel(8);
        assertEquals(8L, ch.size());
        assertArrayEquals(new byte[8], ch.array());
    }

    @Test
    void writeThenReadRoundTrip() throws IOException {
        var ch = new SeekableInMemoryByteChannel();
        byte[] payload = {10, 20, 30, 40, 50};
        int written = ch.write(ByteBuffer.wrap(payload));
        assertEquals(payload.length, written);
        assertEquals(payload.length, ch.size());
        assertEquals(payload.length, ch.position());

        ch.position(0);
        ByteBuffer dst = ByteBuffer.allocate(payload.length);
        int read = ch.read(dst);
        assertEquals(payload.length, read);
        assertArrayEquals(payload, dst.array());
    }

    @Test
    void readAtEndReturnsMinusOne() throws IOException {
        var ch = new SeekableInMemoryByteChannel(new byte[]{1, 2});
        ch.position(2);
        assertEquals(-1, ch.read(ByteBuffer.allocate(4)));
    }

    @Test
    void readRespectsRemainingBytes() throws IOException {
        var ch = new SeekableInMemoryByteChannel(new byte[]{1, 2, 3});
        ch.position(1);
        ByteBuffer dst = ByteBuffer.allocate(10);
        int read = ch.read(dst);
        assertEquals(2, read);
        assertEquals(3L, ch.position());
    }

    @Test
    void truncateShrinksSizeAndClampsPosition() {
        var ch = new SeekableInMemoryByteChannel(new byte[]{1, 2, 3, 4, 5});
        assertEquals(5L, ch.size());
        ch.truncate(3);
        assertEquals(3L, ch.size());
    }

    @Test
    void truncateLargerThanSizeIsNoop() {
        var ch = new SeekableInMemoryByteChannel(new byte[]{1, 2, 3});
        ch.truncate(100);
        assertEquals(3L, ch.size());
    }

    @Test
    void truncateNegativeThrows() {
        var ch = new SeekableInMemoryByteChannel();
        assertThrows(IllegalArgumentException.class, () -> ch.truncate(-1));
    }

    @Test
    void positionOutOfRangeThrows() {
        var ch = new SeekableInMemoryByteChannel();
        assertThrows(IOException.class, () -> ch.position(-1));
    }

    @Test
    void writeBeyondEndGrowsBuffer() throws IOException {
        var ch = new SeekableInMemoryByteChannel();
        ch.position(4);
        ch.write(ByteBuffer.wrap(new byte[]{9, 9}));
        assertEquals(6L, ch.size());
    }

    @Test
    void operationsAfterCloseThrow() {
        var ch = new SeekableInMemoryByteChannel();
        ch.close();
        assertFalse(ch.isOpen());
        assertThrows(ClosedChannelException.class, () -> ch.read(ByteBuffer.allocate(1)));
        assertThrows(ClosedChannelException.class, () -> ch.write(ByteBuffer.allocate(1)));
    }
}
