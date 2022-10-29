package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.powernukkitx.libdeflate.CompressionType;
import cn.powernukkitx.libdeflate.LibdeflateCompressor;
import cn.powernukkitx.libdeflate.LibdeflateDecompressor;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@PowerNukkitXOnly
@Since("1.19.40-r1")
public class LibDeflateThreadLocal implements ZlibProvider {
    private static final ThreadLocal<Inflater> INFLATER = ThreadLocal.withInitial(Inflater::new);
    private static final ThreadLocal<LibdeflateDecompressor> PNX_INFLATER = ThreadLocal.withInitial(PNXLibInflater::new);
    private static final ThreadLocal<LibdeflateCompressor> DEFLATER = ThreadLocal.withInitial(PNXLibDeflater::new);
    private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[8192]);

    // compress
    @Override
    public byte[] deflate(byte[][] datas, int level) throws IOException {
        var deflater = DEFLATER.get();
        var bos = ThreadCache.fbaos.get();
        bos.reset();
        for (var data : datas) {
            bos.write(data, 0, data.length);
        }
        var data = bos.toByteArray();
        byte[] buffer = deflater.getCompressBound(data.length, CompressionType.ZLIB) < 8192 ? BUFFER.get() : new byte[data.length];
        int compressedSize = deflater.compress(data, buffer, CompressionType.ZLIB);
        return Arrays.copyOf(buffer, compressedSize);
    }

    @Override
    public byte[] deflate(byte[] data, int level) throws IOException {
        var deflater = DEFLATER.get();
        byte[] buffer = deflater.getCompressBound(data.length, CompressionType.ZLIB) < 8192 ? BUFFER.get() : new byte[data.length];
        int compressedSize = deflater.compress(data, buffer, CompressionType.ZLIB);
        byte[] output = new byte[compressedSize];
        System.arraycopy(buffer, 0, output, 0, compressedSize);
        return output;
    }

    // decompress
    @Override
    public byte[] inflate(byte[] data, int maxSize) throws IOException {
        var pnxInflater = PNX_INFLATER.get();
        byte[] buffer = BUFFER.get();
        try {
            var result = pnxInflater.decompressUnknownSize(data, 0, data.length, buffer, 0, buffer.length, CompressionType.ZLIB);
            if (result == -1) {
                return inflate0(data, maxSize);
            } else if (maxSize > 0 && result >= maxSize) {
                throw new IOException("Inflated data exceeds maximum size");
            }
            byte[] output = new byte[(int) result];
            System.arraycopy(buffer, 0, output, 0, output.length);
            return output;
        } catch (DataFormatException e) {
            throw new IOException("Unable to inflate zlib stream", e);
        }
    }

    public byte[] inflate0(byte[] data, int maxSize) throws IOException {
        Inflater inflater = INFLATER.get();
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
