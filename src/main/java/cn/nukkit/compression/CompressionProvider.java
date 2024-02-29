package cn.nukkit.compression;

import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.BinaryStream;
import org.xerial.snappy.Snappy;

import java.io.IOException;


public interface CompressionProvider {
    CompressionProvider NONE = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws IOException {
            return packet.getBuffer();
        }

        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return data;
        }

        @Override
        public byte[] decompress(byte[] compressed) throws IOException {
            return compressed;
        }
    };

    CompressionProvider ZLIB = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws IOException {
            return ZlibChooser.deflate(packet.getBuffer(), level, false);
        }

        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return ZlibChooser.deflate(data, false);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws IOException {
            return ZlibChooser.inflate(compressed, false);
        }
    };

    CompressionProvider ZLIB_RAW = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws IOException {
            return ZlibChooser.deflate(packet.getBuffer(), level, true);
        }

        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return ZlibChooser.deflate(data, true);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws IOException {
            return ZlibChooser.inflate(compressed, true);
        }
    };

    CompressionProvider SNAPPY = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws IOException {
            return Snappy.compress(packet.getBuffer());
        }

        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return Snappy.compress(data);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws IOException {
            return Snappy.uncompress(compressed);
        }
    };


    byte[] compress(BinaryStream packet, int level) throws IOException;

    byte[] compress(byte[] data, int level) throws IOException;

    byte[] decompress(byte[] compressed) throws IOException;

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