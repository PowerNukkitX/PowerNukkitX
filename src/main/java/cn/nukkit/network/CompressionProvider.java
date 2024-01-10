package cn.nukkit.network;

import cn.nukkit.network.connection.netty.codec.compression.ZlibCompressionCodec;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.SnappyCompression;
import cn.nukkit.utils.Zlib;


public interface CompressionProvider {

    CompressionProvider NONE = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return packet.getBuffer();
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return compressed;
        }
    };

    CompressionProvider ZLIB = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return Zlib.deflate(packet.getBuffer(), level);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return Zlib.inflate(compressed);
        }
    };


    CompressionProvider SNAPPY = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return SnappyCompression.compress(packet.getBuffer());
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return SnappyCompression.decompress(compressed);
        }
    };


    byte[] compress(BinaryStream packet, int level) throws Exception;

    byte[] decompress(byte[] compressed) throws Exception;

    static CompressionProvider from(PacketCompressionAlgorithm algorithm) {
        if (algorithm == null) {
            return NONE;
        } else if (algorithm == PacketCompressionAlgorithm.ZLIB) {
            return ZLIB;
        } else if (algorithm == PacketCompressionAlgorithm.SNAPPY) {
            return SNAPPY;
        }
        throw new UnsupportedOperationException();
    }
}