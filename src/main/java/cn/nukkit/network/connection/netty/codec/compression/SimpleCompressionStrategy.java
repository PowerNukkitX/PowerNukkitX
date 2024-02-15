package cn.nukkit.network.connection.netty.codec.compression;


import cn.nukkit.compression.CompressionProvider;
import cn.nukkit.network.connection.netty.BedrockBatchWrapper;
import cn.nukkit.network.protocol.types.CompressionAlgorithm;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;

/**
 * A simple compression strategy that uses the same compression for all packets, but
 * supports decompression using all Bedrock protocol supported algorithms.
 */
public class SimpleCompressionStrategy implements CompressionStrategy {
    private final BatchCompression compression;
    private final BatchCompression none;
    private final BatchCompression zlib;
    private final BatchCompression snappy;

    public SimpleCompressionStrategy(BatchCompression compression) {
        this.compression = compression;
        this.none = new NoopCompression();
        if (compression.getAlgorithm() == PacketCompressionAlgorithm.ZLIB) {
            this.zlib = compression;
            this.snappy = new SnappyCompression();
        } else if (compression.getAlgorithm() == PacketCompressionAlgorithm.SNAPPY) {
            this.zlib = new ZlibCompression(CompressionProvider.ZLIB_RAW);
            this.snappy = compression;
        } else {
            this.zlib = new ZlibCompression(CompressionProvider.ZLIB_RAW);
            this.snappy = new SnappyCompression();
        }
    }

    @Override
    public BatchCompression getCompression(BedrockBatchWrapper wrapper) {
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

    @Override
    public String toString() {
        return "SimpleCompressionStrategy{" +
                "compression=" + compression +
                '}';
    }
}
