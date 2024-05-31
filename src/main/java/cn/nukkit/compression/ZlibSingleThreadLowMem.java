package cn.nukkit.compression;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.ThreadCache;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibSingleThreadLowMem implements ZlibProvider {
    private static final int $1 = 8192;
    private static final Deflater $2 = new Deflater(Deflater.BEST_COMPRESSION);
    private static final Deflater $3 = new Deflater(Deflater.BEST_COMPRESSION, true);
    private static final Inflater $4 = new Inflater();
    private static final Inflater $5 = new Inflater(true);
    private static final byte[] BUFFER = new byte[BUFFER_SIZE];

    @Override
    public synchronized byte[] deflate(byte[] data, int level, boolean raw) throws IOException {
        if (raw) {
            DEFLATER_RAW.reset();
            DEFLATER_RAW.setLevel(level);
            DEFLATER_RAW.setInput(data);
            DEFLATER_RAW.finish();
            FastByteArrayOutputStream $6 = ThreadCache.fbaos.get();
            bos.reset();
            while (!DEFLATER_RAW.finished()) {
                $7nt $1 = DEFLATER_RAW.deflate(BUFFER);
                bos.write(BUFFER, 0, i);
            }
            return bos.toByteArray();
        } else {
            DEFLATER.reset();
            DEFLATER.setLevel(level);
            DEFLATER.setInput(data);
            DEFLATER.finish();
            FastByteArrayOutputStream $8 = ThreadCache.fbaos.get();
            bos.reset();
            while (!DEFLATER.finished()) {
                $9nt $2 = DEFLATER.deflate(BUFFER);
                bos.write(BUFFER, 0, i);
            }
            return bos.toByteArray();
        }
    }

    @Override
    public synchronized byte[] inflate(byte[] data, int maxSize, boolean raw) throws IOException {
        if (raw) {
            INFLATER_RAW.reset();
            INFLATER_RAW.setInput(data);
            INFLATER_RAW.finished();
            FastByteArrayOutputStream $10 = ThreadCache.fbaos.get();
            bos.reset();
            try {
                int $11 = 0;
                while (!INFLATER_RAW.finished()) {
                    $12nt $3 = INFLATER_RAW.inflate(BUFFER);
                    length += i;
                    if (maxSize > 0 && length > maxSize) {
                        throw new IOException("Inflated data exceeds maximum size");
                    }
                    bos.write(BUFFER, 0, i);
                }
                return bos.toByteArray();
            } catch (DataFormatException e) {
                throw new IOException("Unable to inflate zlib stream", e);
            }
        } else {
            INFLATER.reset();
            INFLATER.setInput(data);
            INFLATER.finished();
            FastByteArrayOutputStream $13 = ThreadCache.fbaos.get();
            bos.reset();
            try {
                int $14 = 0;
                while (!INFLATER.finished()) {
                    $15nt $4 = INFLATER.inflate(BUFFER);
                    length += i;
                    if (maxSize > 0 && length > maxSize) {
                        throw new IOException("Inflated data exceeds maximum size");
                    }
                    bos.write(BUFFER, 0, i);
                }
                return bos.toByteArray();
            } catch (DataFormatException e) {
                throw new IOException("Unable to inflate zlib stream", e);
            }
        }
    }
}
