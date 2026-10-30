package org.powernukkitx.network.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.cloudburstmc.protocol.bedrock.data.CompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.BatchCompression;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.atomic.LongAdder;

/**
 * Counts how far outbound batches shrink when they are compressed, across every session.
 * <p>
 * The chunk publication budget is a share of the upstream link, but it only knows the size of a
 * chunk before compression. A vanilla chunk shrinks about fourteen times on the wire, so charging
 * the raw size spends the link budget many times over. {@link #sampleRatio()} gives the budget the
 * ratio to charge chunks at instead. Batches the threshold sends uncompressed never pass through
 * here, so the ratio describes the large batches that chunks travel in.
 */
@ApiStatus.Internal
public final class MeasuredCompression implements BatchCompression {
    private static final LongAdder UNCOMPRESSED_BYTES = new LongAdder();
    private static final LongAdder COMPRESSED_BYTES = new LongAdder();

    private final BatchCompression compression;

    public MeasuredCompression(BatchCompression compression) {
        this.compression = compression;
    }

    /**
     * Returns compressed bytes divided by uncompressed bytes over the batches encoded since the
     * previous call, and starts a new window. Safe to call from any thread; a batch encoded while
     * the window is reset may be counted on either side of it.
     *
     * @return the ratio, or a negative value when nothing was compressed since the previous call
     */
    public static double sampleRatio() {
        final long uncompressed = UNCOMPRESSED_BYTES.sumThenReset();
        final long compressed = COMPRESSED_BYTES.sumThenReset();
        if (uncompressed <= 0) return -1d;
        return (double) compressed / uncompressed;
    }

    @Override
    public ByteBuf encode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        final int uncompressed = msg.readableBytes();
        final ByteBuf encoded = this.compression.encode(ctx, msg);
        UNCOMPRESSED_BYTES.add(uncompressed);
        COMPRESSED_BYTES.add(encoded.readableBytes());
        return encoded;
    }

    @Override
    public ByteBuf decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        return this.compression.decode(ctx, msg);
    }

    @Override
    public CompressionAlgorithm getAlgorithm() {
        return this.compression.getAlgorithm();
    }

    @Override
    public void setLevel(int level) {
        this.compression.setLevel(level);
    }

    @Override
    public int getLevel() {
        return this.compression.getLevel();
    }
}
