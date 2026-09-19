package org.powernukkitx.level.format.leveldb;

import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.utils.HashUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * LevelChunkMetaDataDictionary
 * 
 * @author Curse
 */
final class LevelChunkMetaDataDictionary {
    private static final byte[] GLOBAL_KEY =
            LevelDBKeyUtil.getGlobalKey("LevelChunkMetaDataDictionary");

    private final Map<Long, NbtMap> entries = new TreeMap<>(Long::compareUnsigned);
    private boolean dirty;

    LevelChunkMetaDataDictionary(DB db) {
        byte[] data = db.get(GLOBAL_KEY);
        if (data != null) {
            read(data);
        }
    }

    boolean contains(long hash) {
        return entries.containsKey(hash);
    }

    NbtMap get(long hash) {
        return entries.get(hash);
    }

    long register(NbtMap metadata) {
        long hash = HashUtils.computeLevelChunkMetaDataHash(metadata);

        if (!entries.containsKey(hash)) {
            entries.put(hash, metadata);
            dirty = true;
        }

        return hash;
    }

    void write(WriteBatch writeBatch) {
        if (!dirty) {
            return;
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            writeIntLE(outputStream, entries.size());

            for (Map.Entry<Long, NbtMap> entry : entries.entrySet()) {
                writeLongLE(outputStream, entry.getKey());

                NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream);
                nbtOutputStream.writeTag(entry.getValue());
            }

            writeBatch.put(GLOBAL_KEY, outputStream.toByteArray());
            dirty = false;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void read(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

            int count = readIntLE(inputStream);

            for (int i = 0; i < count; i++) {
                long hash = readLongLE(inputStream);

                NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream);
                Object tag = nbtInputStream.readTag();

                if (!(tag instanceof NbtMap metadata)) {
                    throw new IOException("Invalid LevelChunkMetaDataDictionary entry");
                }

                long canonicalHash = HashUtils.computeLevelChunkMetaDataHash(metadata);
                if (hash != canonicalHash) {
                    throw new IOException("Invalid LevelChunkMetaDataDictionary hash: stored "
                            + Long.toUnsignedString(hash) + ", computed " + Long.toUnsignedString(canonicalHash));
                }

                entries.put(hash, metadata);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read LevelChunkMetaDataDictionary", e);
        }
    }

    private static void writeIntLE(ByteArrayOutputStream outputStream, int value) {
        outputStream.write(value & 0xff);
        outputStream.write((value >>> 8) & 0xff);
        outputStream.write((value >>> 16) & 0xff);
        outputStream.write((value >>> 24) & 0xff);
    }

    private static int readIntLE(ByteArrayInputStream inputStream) throws IOException {
        return readUnsignedByte(inputStream)
                | (readUnsignedByte(inputStream) << 8)
                | (readUnsignedByte(inputStream) << 16)
                | (readUnsignedByte(inputStream) << 24);
    }

    private static void writeLongLE(ByteArrayOutputStream outputStream, long value) {
        outputStream.write((int) (value & 0xff));
        outputStream.write((int) ((value >>> 8) & 0xff));
        outputStream.write((int) ((value >>> 16) & 0xff));
        outputStream.write((int) ((value >>> 24) & 0xff));
        outputStream.write((int) ((value >>> 32) & 0xff));
        outputStream.write((int) ((value >>> 40) & 0xff));
        outputStream.write((int) ((value >>> 48) & 0xff));
        outputStream.write((int) ((value >>> 56) & 0xff));
    }

    private static long readLongLE(ByteArrayInputStream inputStream) throws IOException {
        return readUnsignedByte(inputStream)
                | ((long) readUnsignedByte(inputStream) << 8)
                | ((long) readUnsignedByte(inputStream) << 16)
                | ((long) readUnsignedByte(inputStream) << 24)
                | ((long) readUnsignedByte(inputStream) << 32)
                | ((long) readUnsignedByte(inputStream) << 40)
                | ((long) readUnsignedByte(inputStream) << 48)
                | ((long) readUnsignedByte(inputStream) << 56);
    }

    private static int readUnsignedByte(ByteArrayInputStream inputStream) throws IOException {
        int value = inputStream.read();

        if (value < 0) {
            throw new IOException("Unexpected end of LevelChunkMetaDataDictionary");
        }

        return value;
    }

    static byte[] hashToLittleEndian(long hash) {
        return new byte[]{
                (byte) (hash & 0xff),
                (byte) ((hash >>> 8) & 0xff),
                (byte) ((hash >>> 16) & 0xff),
                (byte) ((hash >>> 24) & 0xff),
                (byte) ((hash >>> 32) & 0xff),
                (byte) ((hash >>> 40) & 0xff),
                (byte) ((hash >>> 48) & 0xff),
                (byte) ((hash >>> 56) & 0xff)
        };
    }

    static long hashFromLittleEndian(byte[] data) {
        if (data.length != Long.BYTES) {
            throw new IllegalArgumentException("LevelChunkMetaData hash must contain exactly 8 bytes");
        }

        return (data[0] & 0xffL)
                | ((data[1] & 0xffL) << 8)
                | ((data[2] & 0xffL) << 16)
                | ((data[3] & 0xffL) << 24)
                | ((data[4] & 0xffL) << 32)
                | ((data[5] & 0xffL) << 40)
                | ((data[6] & 0xffL) << 48)
                | ((data[7] & 0xffL) << 56);
    }
}
