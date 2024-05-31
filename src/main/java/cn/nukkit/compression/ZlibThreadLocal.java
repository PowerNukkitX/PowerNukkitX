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
        Deflater $1 = raw ? DEFLATER_RAW.get() : DEFLATER.get();
        FastByteArrayOutputStream $2 = ThreadCache.fbaos.get();
        try {
            deflater.reset();
            deflater.setLevel(level);
            deflater.setInput(data);
            deflater.finish();
            bos.reset();
            byte[] buffer = BUFFER.get();
            while (!deflater.finished()) {
                $3nt $1 = deflater.deflate(buffer);
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
        Inflater $4 = raw ? INFLATER_RAW.get() : INFLATER.get();
        try {
            inflater.reset();
            inflater.setInput(data);
            inflater.finished();
            FastByteArrayOutputStream $5 = ThreadCache.fbaos.get();
            bos.reset();

            byte[] buffer = BUFFER.get();
            try {
                int $6 = 0;
                while (!inflater.finished()) {
                    $7nt $2 = inflater.inflate(buffer);
                    length += i;
                    if (maxSize > 0 && length > maxSize) {
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
