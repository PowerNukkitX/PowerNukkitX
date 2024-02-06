package cn.nukkit.network.connection.netty.codec.compression;


import cn.nukkit.network.connection.netty.BedrockBatchWrapper;
import cn.nukkit.network.protocol.types.CompressionAlgorithm;

public interface CompressionStrategy {

    BatchCompression getCompression(BedrockBatchWrapper wrapper);

    BatchCompression getCompression(CompressionAlgorithm algorithm);

    BatchCompression getDefaultCompression();
}
