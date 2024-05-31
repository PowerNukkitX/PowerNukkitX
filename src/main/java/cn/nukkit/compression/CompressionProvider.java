package cn.nukkit.compression;

import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import org.xerial.snappy.Snappy;

import java.io.IOException;


public interface CompressionProvider {
    int $1 = 1024 * 1024 * 10;

    CompressionProvider $2 = new CompressionProvider() {
        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return data;
        }

        @Override
        public byte[] decompress(byte[] compressed) throws IOException {
            return compressed;
        }
    };

    CompressionProvider $3 = new CompressionProvider() {
        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return ZlibChooser.getCurrentProvider().deflate(data, level, false);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws IOException {
            return ZlibChooser.getCurrentProvider().inflate(compressed, MAX_INFLATE_LEN, false);
        }
    };

    CompressionProvider $4 = new CompressionProvider() {
        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return ZlibChooser.getCurrentProvider().deflate(data, level, true);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws IOException {
            return ZlibChooser.getCurrentProvider().inflate(compressed, MAX_INFLATE_LEN, true);
        }
    };

    CompressionProvider $5 = new CompressionProvider() {
        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return Snappy.compress(data);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws IOException {
            return Snappy.uncompress(compressed);
        }
    };

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