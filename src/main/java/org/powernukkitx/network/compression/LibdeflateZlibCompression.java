package org.powernukkitx.network.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.ZlibCompression;
import org.cloudburstmc.protocol.common.util.Zlib;
import org.powernukkitx.libdeflate.CompressionType;
import org.powernukkitx.libdeflate.Libdeflate;
import org.powernukkitx.libdeflate.LibdeflateCompressor;

/**
 * Raw deflate for outbound batches through libdeflate, which compresses a whole buffer in one call
 * and is markedly faster than zlib at the same level. The client sees an ordinary raw deflate
 * stream: decompressing it yields the same batch, only the compressed bytes differ. Inbound
 * batches are still inflated by zlib.
 * <p>
 * Falls back to zlib where the native library is not available.
 */
@Slf4j
public final class LibdeflateZlibCompression extends ZlibCompression {
    private static final boolean AVAILABLE = checkAvailable();
    private static final FastThreadLocal<LibdeflateCompressor[]> COMPRESSORS = new FastThreadLocal<>() {
        @Override
        protected LibdeflateCompressor[] initialValue() {
            return new LibdeflateCompressor[13];
        }

        @Override
        protected void onRemoval(LibdeflateCompressor[] compressors) {
            for (LibdeflateCompressor compressor : compressors) {
                if (compressor != null) {
                    compressor.close();
                }
            }
        }
    };

    private final SizedZlibCompression fallback;

    public LibdeflateZlibCompression(Zlib zlib) {
        super(zlib);
        this.fallback = new SizedZlibCompression(zlib);
    }

    /**
     * Whether libdeflate loaded on this platform, so batches go through it.
     */
    public static boolean isAvailable() {
        return AVAILABLE;
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);
        this.fallback.setLevel(level);
    }

    @Override
    public ByteBuf encode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (!AVAILABLE) {
            return this.fallback.encode(ctx, msg);
        }
        final int length = msg.readableBytes();
        ByteBuf source = msg;
        if (!msg.isDirect() || msg.nioBufferCount() != 1) {
            source = ctx.alloc().ioBuffer(length);
            source.writeBytes(msg, msg.readerIndex(), length);
        }
        try {
            final LibdeflateCompressor compressor = compressor(this.getLevel());
            final int bound = (int) compressor.getCompressBound(length, CompressionType.DEFLATE);
            final ByteBuf out = ctx.alloc().ioBuffer(bound);
            try {
                final int written = compressor.compress(
                    source.internalNioBuffer(source.readerIndex(), length),
                    out.internalNioBuffer(0, bound),
                    CompressionType.DEFLATE);
                if (written <= 0) {
                    return this.fallback.encode(ctx, msg);
                }
                out.writerIndex(written);
                return out.retain();
            } finally {
                out.release();
            }
        } finally {
            if (source != msg) {
                source.release();
            }
        }
    }

    private static LibdeflateCompressor compressor(int level) {
        final LibdeflateCompressor[] byLevel = COMPRESSORS.get();
        LibdeflateCompressor compressor = byLevel[level];
        if (compressor == null) {
            compressor = new LibdeflateCompressor(level);
            byLevel[level] = compressor;
        }
        return compressor;
    }

    private static boolean checkAvailable() {
        try {
            if (Libdeflate.isAvailable()) {
                return true;
            }
            log.debug("libdeflate is not available, network batches are compressed with zlib", Libdeflate.unavailabilityCause());
        } catch (LinkageError e) {
            log.debug("libdeflate could not be loaded, network batches are compressed with zlib", e);
        }
        return false;
    }
}
