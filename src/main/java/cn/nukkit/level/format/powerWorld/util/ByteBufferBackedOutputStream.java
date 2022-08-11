package cn.nukkit.level.format.powerWorld.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class ByteBufferBackedOutputStream extends OutputStream {
    ByteBuffer buf;

    public ByteBufferBackedOutputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    public void write(int b) throws IOException {
        buf.put((byte) b);
    }

    public void write(@NotNull byte[] bytes, int off, int len) throws IOException {
        buf.put(bytes, off, len);
    }

}
