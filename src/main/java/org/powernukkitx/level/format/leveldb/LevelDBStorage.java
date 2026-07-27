package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.level.village.Village;
import org.powernukkitx.level.village.VillageDwellers;
import org.powernukkitx.level.village.VillageInfo;
import org.powernukkitx.level.village.VillagePlayers;
import org.powernukkitx.level.village.VillagePois;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public final class LevelDBStorage {
    private static final List<String> VILLAGE_COMPONENTS = List.of("DWELLERS", "INFO", "POI", "PLAYERS");
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

    public List<Village> readVillages(DimensionData dimension) {
        String prefix = getVillagePrefix(dimension);
        Pattern pattern = getVillageKeyPattern(prefix);
        Map<UUID, Map<String, CompoundTag>> villageData = new HashMap<>();

        byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);
        try (DBIterator iterator = this.db.iterator()) {
            for (iterator.seek(prefixBytes); iterator.hasNext(); iterator.next()) {
                var entry = iterator.peekNext();
                if (!startsWith(entry.getKey(), prefixBytes)) {
                    break;
                }
                String key = new String(entry.getKey(), StandardCharsets.UTF_8);
                var matcher = pattern.matcher(key);
                if (!matcher.matches()) {
                    continue;
                }
                UUID uuid = UUID.fromString(matcher.group(1));
                villageData.computeIfAbsent(uuid, ignored -> new HashMap<>())
                        .put(matcher.group(2), readLittleEndianCompound(entry.getValue()));
            }
        }

        List<Village> villages = new ArrayList<>();
        villageData.forEach((uuid, data) -> {
            if (data.keySet().containsAll(VILLAGE_COMPONENTS)) {
                villages.add(new Village(uuid,
                        VillageDwellers.fromCompound(data.get("DWELLERS")),
                        VillageInfo.fromCompound(data.get("INFO")),
                        VillagePlayers.fromCompound(data.get("PLAYERS")),
                        VillagePois.fromCompound(data.get("POI")), null));
            }
        });
        return villages;
    }

    public void writeVillages(DimensionData dimension, List<Village> villages) {
        String prefix = getVillagePrefix(dimension);
        Pattern pattern = getVillageKeyPattern(prefix);
        byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);
        try (WriteBatch batch = this.db.createWriteBatch(); DBIterator iterator = this.db.iterator()) {
            for (iterator.seek(prefixBytes); iterator.hasNext(); iterator.next()) {
                byte[] key = iterator.peekNext().getKey();
                if (!startsWith(key, prefixBytes)) {
                    break;
                }
                if (pattern.matcher(new String(key, StandardCharsets.UTF_8)).matches()) {
                    batch.delete(key);
                }
            }

            for (Village village : villages) {
                String villagePrefix = prefix + village.uuid() + '_';
                batch.put(getVillageKey(villagePrefix, "DWELLERS"), writeLittleEndianCompound(village.dwellers().toCompound()));
                batch.put(getVillageKey(villagePrefix, "INFO"), writeLittleEndianCompound(village.info().toCompound()));
                batch.put(getVillageKey(villagePrefix, "POI"), writeLittleEndianCompound(village.pois().toCompound()));
                batch.put(getVillageKey(villagePrefix, "PLAYERS"), writeLittleEndianCompound(village.players().toCompound()));
            }
            this.db.write(batch, new WriteOptions().sync(false));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write villages", e);
        }
    }

    private static String getVillagePrefix(DimensionData dimension) {
        return switch (dimension.getDimensionId()) {
            case Level.DIMENSION_OVERWORLD -> "VILLAGE_";
            case Level.DIMENSION_NETHER -> "VILLAGE_NETHER_";
            case Level.DIMENSION_THE_END -> "VILLAGE_THE_END_";
            default -> "VILLAGE_" + dimension.getDimensionName().replace("minecraft:", "")
                    .toUpperCase().replace(':', '_') + '_';
        };
    }

    private static Pattern getVillageKeyPattern(String prefix) {
        return Pattern.compile(Pattern.quote(prefix)
                + "([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})_"
                + "(DWELLERS|INFO|POI|PLAYERS)");
    }

    private static byte[] getVillageKey(String prefix, String component) {
        return (prefix + component).getBytes(StandardCharsets.UTF_8);
    }

    private static boolean startsWith(byte[] key, byte[] prefix) {
        if (key.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (key[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static CompoundTag readLittleEndianCompound(byte[] data) {
        try (var input = new ByteArrayInputStream(data); var reader = NbtUtils.createReaderLE(input)) {
            return CompoundTag.fromNetwork((NbtMap) reader.readTag());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static byte[] writeLittleEndianCompound(CompoundTag tag) {
        try (var output = new ByteArrayOutputStream(); var writer = NbtUtils.createWriterLE(output)) {
            writer.writeTag(tag.toNetwork());
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
