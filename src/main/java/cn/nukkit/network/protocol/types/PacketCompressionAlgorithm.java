package cn.nukkit.network.protocol.types;

public enum PacketCompressionAlgorithm implements CompressionAlgorithm {
    ZLIB,
    SNAPPY,
    NONE
}