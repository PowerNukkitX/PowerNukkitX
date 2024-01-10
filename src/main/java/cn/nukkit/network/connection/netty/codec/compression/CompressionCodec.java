package cn.nukkit.network.connection.netty.codec.compression;


import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;

public interface CompressionCodec {
    int BUFFER_LEN = 2 * 1024 * 1024;
    ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[BUFFER_LEN]);

    String NAME = "compression-codec";
    int MAX_DECOMPRESSED_BYTES = Integer.getInteger("bedrock.maxDecompressedBytes", 1024 * 1024 * 10);

    int getLevel();

    void setLevel(int level);

    PacketCompressionAlgorithm getAlgorithm();
}
