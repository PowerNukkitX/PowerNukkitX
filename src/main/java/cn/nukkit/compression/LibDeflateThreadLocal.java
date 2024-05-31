package cn.nukkit.compression;

import cn.nukkit.Server;
import cn.nukkit.utils.CleanerHandle;
import cn.nukkit.utils.PNXLibDeflater;
import cn.nukkit.utils.PNXLibInflater;
import cn.powernukkitx.libdeflate.CompressionType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;

public class LibDeflateThreadLocal implements ZlibProvider {
    private final ZlibThreadLocal zlibThreadLocal;

    private static final ThreadLocal<CleanerHandle<PNXLibInflater>> PNX_INFLATER = ThreadLocal.withInitial(() -> new CleanerHandle<>(new PNXLibInflater()));
    private static final ThreadLocal<CleanerHandle<PNXLibDeflater>> PNX_DEFLATER = ThreadLocal.withInitial(() -> new CleanerHandle<>(new PNXLibDeflater()));
    private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[8192]);

    private static final ThreadLocal<ByteBuffer> DIRECT_BUFFER = ThreadLocal.withInitial(() -> {
        var maximumSizePerChunk = CompressionProvider.MAX_INFLATE_LEN;
        if (Server.getInstance() != null) {
            maximumSizePerChunk = Server.getInstance().getSettings().networkSettings().compressionBufferSize();
        }
        if (maximumSizePerChunk < 8192 || maximumSizePerChunk > 1024 * 1024 * 16) {
            return null;
        } else {
            return ByteBuffer.allocateDirect(maximumSizePerChunk).order(ByteOrder.nativeOrder());
        }
    });

    public LibDeflateThreadLocal(ZlibThreadLocal zlibThreadLocal) {
        this.zlibThreadLocal = zlibThreadLocal;
    }

    @Override
    public byte[] deflate(byte[] data, int level, boolean raw) throws IOException {
        PNXLibDeflater deflater = PNX_DEFLATER.get().getResource();
        CompressionType type = raw ? CompressionType.DEFLATE : CompressionType.ZLIB;
        byte[] buffer = deflater.getCompressBound(data.length, type) < 8192 ? BUFFER.get() : new byte[data.length];
        int compressedSize = deflater.compress(data, buffer, type);
        if (compressedSize <= 0) {
            return zlibThreadLocal.deflate(data, level, raw);
        }
        byte[] output = new byte[compressedSize];
        System.arraycopy(buffer, 0, output, 0, compressedSize);
        return output;
    }

    // decompress
    @Override
    public byte[] inflate(byte[] data, int maxSize, boolean raw) throws IOException {
        CompressionType type = raw ? CompressionType.DEFLATE : CompressionType.ZLIB;
        var pnxInflater = PNX_INFLATER.get().getResource();
        try {
            if (maxSize < 8192) {
                byte[] buffer = BUFFER.get();
                var result = pnxInflater.decompressUnknownSize(data, 0, data.length, buffer, 0, buffer.length, type);
                if (result == -1) {
                    return inflateD(data, maxSize, type);
                } else if (maxSize > 0 && result > maxSize) {
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
        PNXLibInflater pnxLibInflater = PNX_INFLATER.get().getResource();
        ByteBuffer directBuffer = null;
        try {
            directBuffer = DIRECT_BUFFER.get();
            if (directBuffer == null || directBuffer.capacity() == 0 || data.length > directBuffer.capacity()) {
                return inflate0(data, maxSize, type);
            }
            long result;
            try {
                result = pnxLibInflater.decompressUnknownSize(ByteBuffer.wrap(data), directBuffer, type);
                if (result == -1) {
                    return inflate0(data, maxSize, type);
                } else if (maxSize > 0 && result > maxSize) {
                    throw new IOException("Inflated data exceeds maximum size");
                }
            } catch (IllegalArgumentException ignore) {
                return inflate0(data, maxSize, type);
            }
            byte[] output = new byte[(int) result];
            directBuffer.get(0, output, 0, output.length);
            return output;
        } catch (DataFormatException e) {
            throw new IOException("Unable to inflate zlib stream", e);
        } finally {
            if (directBuffer != null) {
                directBuffer.clear();
            }
        }
    }

    //Fallback
    public byte[] inflate0(byte[] data, int maxSize, CompressionType type) throws IOException {
        return zlibThreadLocal.inflate(data, maxSize, type == CompressionType.DEFLATE);
    }
}
