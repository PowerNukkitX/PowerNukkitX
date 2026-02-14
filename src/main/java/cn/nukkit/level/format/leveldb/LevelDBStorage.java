package cn.nukkit.level.format.leveldb;

import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelProvider;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;
import org.iq80.leveldb.impl.Iq80DBFactory;
import cn.nukkit.level.util.LevelDBKeyUtil;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteOrder;
import java.nio.file.Path;

public final class LevelDBStorage {
    private final DB db;
    private final String path;
    private int dimSum;

    public LevelDBStorage(int dimSum, String path) throws IOException {
        this(dimSum, path, new Options()
                .createIfMissing(true)
                .compressionType(CompressionType.ZLIB_RAW)
                .blockSize(64 * 1024));
    }

    public LevelDBStorage(int dimSum, String pathFolder, Options options) throws IOException {
        this.dimSum = dimSum;
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

    public IChunk readChunk(int x, int z, LevelProvider levelProvider) throws IOException {
        Chunk.Builder builder = Chunk.builder()
                .chunkX(x)
                .chunkZ(z)
                .levelProvider(levelProvider);
        LevelDBChunkSerializer.INSTANCE.deserialize(this.db, builder);
        return builder.build();
    }

    public void writeChunk(IChunk chunk) throws IOException {
        try (WriteBatch writeBatch = this.db.createWriteBatch()) {
            LevelDBChunkSerializer.INSTANCE.serialize(writeBatch, chunk);
            WriteOptions writeOptions = new WriteOptions();
            writeOptions.sync(false);
            this.db.write(writeBatch, writeOptions);
        }
    }

    public CompoundTag readWorldDynamicProperties() {
        try {
            byte[] bytes = this.db.get(LevelDBKeyUtil.WORLD_DYNAMIC_PROPERTIES.getGlobalKey());
            if (bytes == null) return null;
            return NBTIO.read(bytes, ByteOrder.LITTLE_ENDIAN);
        } catch (Exception e) {
            return null;
        }
    }

    public void writeWorldDynamicProperties(CompoundTag tag) {
        try (WriteBatch writeBatch = this.db.createWriteBatch()) {
            byte[] key = LevelDBKeyUtil.WORLD_DYNAMIC_PROPERTIES.getGlobalKey();
            CompoundTag safe = (tag == null) ? new CompoundTag() : tag;
            writeBatch.put(key, NBTIO.write(safe, ByteOrder.LITTLE_ENDIAN));

            WriteOptions writeOptions = new WriteOptions().sync(false);
            this.db.write(writeBatch, writeOptions);
        } catch (Exception ignored) {
        }
    }

    public synchronized void close() {
        dimSum--;
        if (dimSum <= 0) {
            try {
                db.close();
                LevelDBProvider.CACHE.remove(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
