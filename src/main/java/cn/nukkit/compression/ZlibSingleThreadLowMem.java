package cn.nukkit.compression;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.ThreadCache;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibSingleThreadLowMem implements ZlibProvider {
    private static final int BUFFER_SIZE = 8192;
    private static final Deflater DEFLATER = new Deflater(Deflater.BEST_COMPRESSION);
    private static final Inflater INFLATER = new Inflater();
    private static final byte[] BUFFER = new byte[BUFFER_SIZE];

    @Override
    public synchronized byte[] deflate(byte[] data, int level) throws IOException {
        DEFLATER.reset();
        DEFLATER.setInput(data);
        DEFLATER.finish();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        bos.reset();
        try {
            while (!DEFLATER.finished()) {
                int i = DEFLATER.deflate(BUFFER);
                bos.write(BUFFER, 0, i);
            }
        } finally {
            //deflater.end();
        }
        return bos.toByteArray();
    }

    @Override
    public synchronized byte[] inflate(byte[] data, int maxSize) throws IOException {
        INFLATER.reset();
        INFLATER.setInput(data);
        INFLATER.finished();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        bos.reset();
        try {
            int length = 0;
            while (!INFLATER.finished()) {
                int i = INFLATER.inflate(BUFFER);
                length += i;
                if (maxSize > 0 && length >= maxSize) {
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
