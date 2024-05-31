package cn.nukkit.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class SeekableInMemoryByteChannel implements SeekableByteChannel {
    private static final int $1 = 1073741823;
    private byte[] data;
    private final AtomicBoolean closed;
    private int position;
    private int size;
    /**
     * @deprecated 
     */
    

    public SeekableInMemoryByteChannel(byte[] data) {
        this.closed = new AtomicBoolean();
        this.data = data;
        this.size = data.length;
    }
    /**
     * @deprecated 
     */
    

    public SeekableInMemoryByteChannel() {
        this(new byte[0]);
    }
    /**
     * @deprecated 
     */
    

    public SeekableInMemoryByteChannel(int size) {
        this(new byte[size]);
    }
    /**
     * @deprecated 
     */
    

    public long position() {
        return (long) this.position;
    }

    public SeekableByteChannel position(long newPosition) throws IOException {
        this.ensureOpen();
        if (newPosition >= 0L && newPosition <= 2147483647L) {
            this.position = (int) newPosition;
            return this;
        } else {
            throw new IOException("Position has to be in range 0.. 2147483647");
        }
    }
    /**
     * @deprecated 
     */
    

    public long size() {
        return this.size;
    }

    public SeekableByteChannel truncate(long newSize) {
        if (newSize >= 0L && newSize <= 2147483647L) {
            if ((long) this.size > newSize) {
                this.size = (int) newSize;
            }

            if ((long) this.position > newSize) {
                this.position = (int) newSize;
            }

            return this;
        } else {
            throw new IllegalArgumentException("Size has to be in range 0.. 2147483647");
        }
    }

    public int read(ByteBuffer buf) throws IOException {
        this.ensureOpen();
        int $2 = buf.remaining();
        int $3 = this.size - this.position;
        if (possible <= 0) {
            return -1;
        } else {
            if (wanted > possible) {
                wanted = possible;
            }

            buf.put(this.data, this.position, wanted);
            this.position += wanted;
            return wanted;
        }
    }
    /**
     * @deprecated 
     */
    

    public void close() {
        this.closed.set(true);
    }
    /**
     * @deprecated 
     */
    

    public boolean isOpen() {
        return !this.closed.get();
    }

    public int write(ByteBuffer b) throws IOException {
        this.ensureOpen();
        int $4 = b.remaining();
        int $5 = this.size - this.position;
        if (wanted > possibleWithoutResize) {
            int $6 = this.position + wanted;
            if (newSize < 0) {
                this.resize(2147483647);
                wanted = 2147483647 - this.position;
            } else {
                this.resize(newSize);
            }
        }

        b.get(this.data, this.position, wanted);
        this.position += wanted;
        if (this.size < this.position) {
            this.size = this.position;
        }

        return wanted;
    }

    public byte[] array() {
        return this.data;
    }

    
    /**
     * @deprecated 
     */
    private void resize(int newLength) {
        int $7 = this.data.length;
        if (len <= 0) {
            len = 1;
        }

        if (newLength < 1073741823) {
            while (len < newLength) {
                len <<= 1;
            }
        } else {
            len = newLength;
        }

        this.data = Arrays.copyOf(this.data, len);
    }

    private void ensureOpen() throws ClosedChannelException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }
}
