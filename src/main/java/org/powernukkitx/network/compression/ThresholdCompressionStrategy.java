package org.powernukkitx.network.compression;

import org.cloudburstmc.protocol.bedrock.data.CompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.BatchCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.NoopCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SnappyCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.ZlibCompression;
import org.cloudburstmc.protocol.common.util.Zlib;

/**
 * Sends batches smaller than a threshold uncompressed.
 * <p>
 * The bulk of a tick's outbound traffic is tiny batches - movement, entity deltas, latency pings -
 * where a deflate pass costs more CPU on the IO thread than it saves in bytes, and often makes the
 * payload bigger. Only batches at or above the threshold are handed to the real compressor.
 * <p>
 * Requires the prefixed compression header (protocol 649+), since the client has to be told per
 * batch which algorithm was used.
 */
public final class ThresholdCompressionStrategy implements CompressionStrategy {

    private final BatchCompression compression;
    private final BatchCompression none = new NoopCompression();
    private final BatchCompression zlib;
    private final BatchCompression snappy;
    private final int threshold;

    public ThresholdCompressionStrategy(BatchCompression compression, int threshold) {
        this.compression = compression;
        this.threshold = threshold;

        if (compression.getAlgorithm() == PacketCompressionAlgorithm.ZLIB) {
            this.zlib = compression;
            this.snappy = new SnappyCompression();
        } else if (compression.getAlgorithm() == PacketCompressionAlgorithm.SNAPPY) {
            this.zlib = new ZlibCompression(Zlib.RAW);
            this.snappy = compression;
        } else {
            this.zlib = new ZlibCompression(Zlib.RAW);
            this.snappy = new SnappyCompression();
        }
    }

    @Override
    public BatchCompression getCompression(BedrockBatchWrapper wrapper) {
        var uncompressed = wrapper.getUncompressed();
        if (uncompressed != null && uncompressed.readableBytes() < this.threshold) {
            return this.none;
        }
        return this.compression;
    }

    @Override
    public BatchCompression getCompression(CompressionAlgorithm algorithm) {
        if (algorithm == PacketCompressionAlgorithm.ZLIB) {
            return this.zlib;
        } else if (algorithm == PacketCompressionAlgorithm.SNAPPY) {
            return this.snappy;
        } else if (algorithm == PacketCompressionAlgorithm.NONE) {
            return this.none;
        }
        return this.compression;
    }

    @Override
    public BatchCompression getDefaultCompression() {
        return this.compression;
    }
}
