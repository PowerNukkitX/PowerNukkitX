package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public final class LevelDBStorage {
    private final DB db;
    private final String path;
    private int refCount;

    public DB getDb() {
        return this.db;
    }

    public LevelDBStorage(int refCount, String path) throws IOException {
        this(refCount, path, new Options()
                .createIfMissing(true)
                .compressionType(CompressionType.ZLIB_RAW)
                .blockSize(64 * 1024));
    }

    public LevelDBStorage(int refCount, String pathFolder, Options options) throws IOException {
        this.refCount = refCount;
        this.path = pathFolder;
        Path path = Path.of(pathFolder);
        File folder = path.toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.isDirectory()) throw new IllegalArgumentException("The path must be a folder");

        File dbFolder = path.resolve("db").toFile();
        if (!dbFolder.exists()) dbFolder.mkdirs();
        db = new Iq80DBFactory().open(dbFolder, options);
    }

    public synchronized void incrementRefCount() {
        this.refCount++;
    }

    public IChunk readChunk(int x, int z, LevelProvider levelProvider) throws IOException {
        Chunk.Builder builder = Chunk.builder()
                .chunkX(x)
                .chunkZ(z)
                .levelProvider(levelProvider);
        if (!LevelDBChunkSerializer.INSTANCE.deserialize(this.db, builder)) {
            return null;
        }
        return builder.build();
    }

    WriteBatch createBatch() {
        return this.db.createWriteBatch();
    }

    public void writeChunk(IChunk chunk) throws IOException {
        try (WriteBatch writeBatch = createBatch()) {
            LevelDBChunkSerializer.INSTANCE.serialize(writeBatch, chunk);
            writeBatch(writeBatch);
        }
    }

    void writeBatch(WriteBatch writeBatch) {
        WriteOptions writeOptions = new WriteOptions();
        writeOptions.sync(false);
        this.db.write(writeBatch, writeOptions);
    }

    public CompoundTag readWorldDynamicProperties() {
        try {
            byte[] bytes = this.db.get(LevelDBKeyUtil.WORLD_DYNAMIC_PROPERTIES.getGlobalKey());
            if (bytes == null) return null;
            try (var inputStream = new ByteArrayInputStream(bytes);
                 var nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
                return CompoundTag.fromNetwork((NbtMap) nbtInputStream.readTag());
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void writeWorldDynamicProperties(CompoundTag tag) {
        try (WriteBatch writeBatch = this.db.createWriteBatch()) {
            byte[] key = LevelDBKeyUtil.WORLD_DYNAMIC_PROPERTIES.getGlobalKey();
            NbtMap safe = (tag == null) ? NbtMap.EMPTY : tag.toNetwork();
            try (var outputStream = new ByteArrayOutputStream();
                 var nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
                nbtOutputStream.writeTag(safe);
                writeBatch.put(key, outputStream.toByteArray());
            }

            WriteOptions writeOptions = new WriteOptions().sync(false);
            this.db.write(writeBatch, writeOptions);
        } catch (Exception ignored) {
        }
    }

    public synchronized void close() {
        synchronized (LevelDBProvider.CACHE) {
            refCount--;
            if (refCount <= 0) {
                try {
                    db.close();
                    LevelDBProvider.CACHE.remove(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }
}
