package cn.nukkit.compression;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.ThreadCache;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class ZlibThreadLocal implements ZlibProvider {
    private static final ThreadLocal<Inflater> INFLATER = ThreadLocal.withInitial(Inflater::new);
    private static final ThreadLocal<Deflater> DEFLATER = ThreadLocal.withInitial(Deflater::new);
    private static final ThreadLocal<Inflater> INFLATER_RAW = ThreadLocal.withInitial(() -> new Inflater(true));
    private static final ThreadLocal<Deflater> DEFLATER_RAW = ThreadLocal.withInitial(() -> new Deflater(-1, true));
    private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[8192]);

    @Override
    public byte[] deflate(byte[] data, int level, boolean raw) throws IOException {
        Deflater deflater;
        if (raw) {
            deflater = DEFLATER_RAW.get();
        } else {
            deflater = DEFLATER.get();
        }

        // Validate compression level. Allowed values: -1 (DEFAULT), 0..9
        if (level < Deflater.DEFAULT_COMPRESSION || level > Deflater.BEST_COMPRESSION) {
            level = Deflater.DEFAULT_COMPRESSION;
        }
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        try {
            deflater.reset();
            deflater.setLevel(level);
            deflater.setInput(data);
            deflater.finish();
            bos.reset();
            byte[] buffer = BUFFER.get();
            while (!deflater.finished()) {
                int i = deflater.deflate(buffer);
                bos.write(buffer, 0, i);
            }
        } finally {
            deflater.reset();
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    @Override
    public byte[] inflate(byte[] data, int maxSize, boolean raw) throws IOException {
        Inflater inflater;
        if (raw) {
            inflater = INFLATER_RAW.get();
        } else {
            inflater = INFLATER.get();
        }
        try {
            inflater.reset();
            inflater.setInput(data);
            FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
            bos.reset();

            byte[] buffer = BUFFER.get();
            try {
                int length = 0;
                while (!inflater.finished()) {
                    int read = inflater.inflate(buffer);
                    if (read == 0) {
                        // If no data was produced, check if inflater needs more input or is stuck
                        if (inflater.needsInput()) {
                            break; // nothing more to read
                        }
                        // No progress but not finished -> malformed stream
                        throw new IOException("Unable to inflate zlib stream: no progress made");
                    }
                    length += read;
                    if (maxSize > 0 && length > maxSize) {
                        throw new IOException("Inflated data exceeds maximum size");
                    }
                    bos.write(buffer, 0, read);
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
