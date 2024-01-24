package cn.nukkit.compression;

import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.BinaryStream;
import org.xerial.snappy.Snappy;


public interface CompressionProvider {

    CompressionProvider NONE = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return packet.getBuffer();
        }

        @Override
        public byte[] compress(byte[] data, int level) throws Exception {
            return data;
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return compressed;
        }
    };

    CompressionProvider ZLIB = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return ZlibChooser.deflate(packet.getBuffer(), level);
        }

        @Override
        public byte[] compress(byte[] data, int level) throws Exception {
            return ZlibChooser.deflate(data);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return ZlibChooser.inflate(compressed);
        }
    };


    CompressionProvider SNAPPY = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return Snappy.compress(packet.getBuffer());
        }

        @Override
        public byte[] compress(byte[] data, int level) throws Exception {
            return Snappy.compress(data);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return Snappy.uncompress(compressed);
        }
    };


    byte[] compress(BinaryStream packet, int level) throws Exception;

    byte[] compress(byte[] data, int level) throws Exception;

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