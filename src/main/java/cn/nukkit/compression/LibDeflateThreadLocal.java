package cn.nukkit.compression;

import cn.nukkit.Server;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.PNXLibDeflater;
import cn.nukkit.utils.PNXLibInflater;
import cn.nukkit.utils.ThreadCache;
import cn.powernukkitx.libdeflate.CompressionType;
import cn.powernukkitx.libdeflate.LibdeflateCompressor;
import cn.powernukkitx.libdeflate.LibdeflateDecompressor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


public class LibDeflateThreadLocal implements ZlibProvider {
    private static final ThreadLocal<Inflater> INFLATER = ThreadLocal.withInitial(Inflater::new);
    private static final ThreadLocal<Inflater> INFLATER_RAW = ThreadLocal.withInitial(() -> new Inflater(true));
    private static final ThreadLocal<LibdeflateDecompressor> PNX_INFLATER = ThreadLocal.withInitial(PNXLibInflater::new);
    private static final ThreadLocal<LibdeflateCompressor> PNX_DEFLATER = ThreadLocal.withInitial(PNXLibDeflater::new);
    private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[8192]);

    private static final ThreadLocal<ByteBuffer> DIRECT_BUFFER = ThreadLocal.withInitial(() -> {
        var maximumSizePerChunk = 1024 * 1024;
        if (Server.getInstance() != null) {
            maximumSizePerChunk = Server.getInstance().compressionBufferSize();
        }
        if (maximumSizePerChunk < 8192 || maximumSizePerChunk > 1024 * 1024 * 16) {
            return null;
        } else {
            return ByteBuffer.allocateDirect(maximumSizePerChunk).order(ByteOrder.nativeOrder());
        }
    });

    @Override
    public byte[] deflate(byte[] data, int level, boolean raw) throws IOException {
        var deflater = PNX_DEFLATER.get();
        CompressionType type = raw ? CompressionType.DEFLATE : CompressionType.ZLIB;
        byte[] buffer = deflater.getCompressBound(data.length, type) < 8192 ? BUFFER.get() : new byte[data.length];
        int compressedSize = deflater.compress(data, buffer, type);
        byte[] output = new byte[compressedSize];
        System.arraycopy(buffer, 0, output, 0, compressedSize);
        return output;
    }

    // decompress
    @Override
    public byte[] inflate(byte[] data, int maxSize, boolean raw) throws IOException {
        CompressionType type = raw ? CompressionType.DEFLATE : CompressionType.ZLIB;
        var pnxInflater = PNX_INFLATER.get();
        try {
            if (maxSize < 8192) {
                byte[] buffer = BUFFER.get();
                var result = pnxInflater.decompressUnknownSize(data, 0, data.length, buffer, 0, buffer.length, type);
                if (result == -1) {
                    return inflateD(data, maxSize, type);
                } else if (maxSize > 0 && result >= maxSize) {
                    throw new IOException("Inflated data exceeds maximum size");
                }
                byte[] output = new byte[(int) result];
                System.arraycopy(buffer, 0, output, 0, output.length);
                return output;
            } else {
                return inflateD(data, maxSize, type);
            }
        } catch (DataFormatException e) {
            throw new IOException("Unable to inflate zlib stream", e);
        }
    }

    public byte[] inflateD(byte[] data, int maxSize, CompressionType type) throws IOException {
        var pnxInflater = PNX_INFLATER.get();
        try {
            var directBuffer = DIRECT_BUFFER.get();
            if (directBuffer == null || directBuffer.capacity() == 0) {
                return inflate0(data, maxSize, type);
            }
            var result = pnxInflater.decompressUnknownSize(ByteBuffer.wrap(data), directBuffer, type);
            if (result == -1) {
                return inflate0(data, maxSize, type);
            } else if (maxSize > 0 && result >= maxSize) {
                throw new IOException("Inflated data exceeds maximum size");
            }
            byte[] output = new byte[(int) result];
            directBuffer.get(0, output, 0, output.length);
            directBuffer.clear();
            return output;
        } catch (DataFormatException e) {
            throw new IOException("Unable to inflate zlib stream", e);
        }
    }

    public byte[] inflate0(byte[] data, int maxSize, CompressionType type) throws IOException {
        Inflater inflater;
        if (type == CompressionType.DEFLATE) {
            inflater = INFLATER_RAW.get();
        } else {
            inflater = INFLATER.get();
        }
        try {
            inflater.reset();
            inflater.setInput(data);
            inflater.finished();
            FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
            bos.reset();

            byte[] buffer = BUFFER.get();
            try {
                int length = 0;
                while (!inflater.finished()) {
                    int i = inflater.inflate(buffer);
                    length += i;
                    if (maxSize > 0 && length >= maxSize) {
                        throw new IOException("Inflated data exceeds maximum size");
                    }
                    bos.write(buffer, 0, i);
                }
                return bos.toByteArray();
            } catch (DataFormatException e) {
                throw new IOException("Unable to inflate zlib stream", e);
            }
        } finally {
            inflater.reset();
        }
    }
}
