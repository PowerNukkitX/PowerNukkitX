package cn.nukkit.compression;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.ThreadCache;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibOriginal implements ZlibProvider {

    @Override
    public byte[] deflate(byte[] data, int level, boolean raw) throws IOException {
        Deflater deflater = new Deflater(level, raw);
        deflater.setInput(data);
        deflater.finish();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        bos.reset();
        byte[] buf = new byte[1024];
        try {
            while (!deflater.finished()) {
                int i = deflater.deflate(buf);
                bos.write(buf, 0, i);
            }
        } finally {
            deflater.end();
        }
        return bos.toByteArray();
    }

    @Override
    public byte[] inflate(byte[] data, int maxSize, boolean raw) throws IOException {
        Inflater inflater = new Inflater(raw);
        inflater.setInput(data);
        inflater.finished();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        bos.reset();

        byte[] buffer = new byte[1024];
        try {
            int length = 0;
            while (!inflater.finished()) {
                int i = inflater.inflate(buffer);
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
    }
}
