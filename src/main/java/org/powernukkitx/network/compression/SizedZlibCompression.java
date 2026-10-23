package org.powernukkitx.network.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.ZlibCompression;
import org.cloudburstmc.protocol.common.util.Zlib;

/**
 * {@link ZlibCompression} that sizes its output buffer from the deflate worst case instead of eight
 * times the input.
 * <p>
 * Deflate never grows its input by more than a few bytes per block, so eight times the input only
 * cost memory: for a batch of chunks that is megabytes, past what a pooled allocator keeps, which
 * made every large batch allocate and zero a fresh buffer. The compressed bytes are unchanged;
 * the same deflater runs at the same level.
 */
public final class SizedZlibCompression extends ZlibCompression {
    /**
     * Room {@link Zlib#deflate} asks for on top of what it has written before each deflate call.
     */
    private static final int DEFLATE_CHUNK = 8192;

    private final Zlib zlib;

    public SizedZlibCompression(Zlib zlib) {
        super(zlib);
        this.zlib = zlib;
    }

    @Override
    public ByteBuf encode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        final ByteBuf outBuf = ctx.alloc().ioBuffer(deflateBound(msg.readableBytes()) + DEFLATE_CHUNK);
        try {
            this.zlib.deflate(msg, outBuf, this.getLevel());
            return outBuf.retain();
        } finally {
            outBuf.release();
        }
    }

    /**
     * zlib's {@code compressBound}: the largest raw deflate stream {@code length} input bytes can
     * produce, stored blocks included.
     */
    static int deflateBound(int length) {
        return length + (length >>> 12) + (length >>> 14) + (length >>> 25) + 13;
    }
}
