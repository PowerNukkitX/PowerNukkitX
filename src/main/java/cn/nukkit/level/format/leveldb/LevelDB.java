package cn.nukkit.level.format.leveldb;

import cn.nukkit.Nukkit;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeLegacyId2StringIdMap;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.leveldb.datas.*;
import cn.nukkit.level.format.leveldb.util.LDBIO;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.ThreadCache;
import cn.nukkit.utils.Utils;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class LevelDB implements LevelProvider {

    private static final int CHUNK_VERSION = 27;

    private final Level level;
    private final String path;
    private final File levelFile;
    private final File pnxLevelFile;
    private final File dbDirectory;
    private final DB db;

    private boolean closed = false;

    private LDBLevelData levelData;
    private CompoundTag pnxData;

    private final Long2ObjectMap<LDBChunk> chunks = new Long2ObjectOpenHashMap<>();
    private final LongOpenHashSet isChunkGeneratedSet = new LongOpenHashSet();
    private final LongOpenHashSet isChunkPopulatedSet = new LongOpenHashSet();
    private final AtomicReference<LDBChunk> lastChunk = new AtomicReference<>();

    public LevelDB(Level level, String path) throws IOException {
        this.level = level;
        this.path = path;
        this.levelFile = new File(path, "level.dat");
        this.pnxLevelFile = new File(path, "level.pnx.dat");
        this.dbDirectory = new File(path, "db");
        this.db = net.daporkchop.ldbjni.LevelDB.PROVIDER.open(dbDirectory, new Options().createIfMissing(true));
        init();
    }

    private void init() throws IOException {
        levelData = LDBIO.readLevelData(levelFile);
        if (!pnxLevelFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            pnxLevelFile.createNewFile();
        }
        try (var fis = new FileInputStream(pnxLevelFile)) {
            pnxData = NBTIO.readCompressed(fis, ByteOrder.BIG_ENDIAN);
        }
        if (!this.pnxData.contains("generatorName")) {
            this.pnxData.putString("generatorName", Generator.getGenerator(levelData.getWorldType()).getSimpleName().toLowerCase());
        }
        if (!this.pnxData.contains("generatorOptions")) {
            this.pnxData.putString("generatorOptions", "");
        }
        if (this.pnxData.contains("chunkGenerated")) {
            for (var each : this.pnxData.getList("chunkGenerated", LongTag.class).getAll()) {
                this.isChunkGeneratedSet.add(each.data);
            }
        }
        if (this.pnxData.contains("chunkPopulated")) {
            for (var each : this.pnxData.getList("chunkPopulated", LongTag.class).getAll()) {
                this.isChunkPopulatedSet.add(each.data);
            }
        }
        this.pnxData.putList(new ListTag<>("ServerBrand").add(new StringTag("", Nukkit.CODENAME)));
    }

    @Override
    public AsyncTask requestChunkTask(int X, int Z) {
        var chunk = this.getChunk(X, Z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Set");
        }

        long timestamp = chunk.getChanges();

        byte[] blockEntities = EmptyArrays.EMPTY_BYTES;

        if (!chunk.getBlockEntities().isEmpty()) {
            List<CompoundTag> tagList = new ArrayList<>();

            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof BlockEntitySpawnable) {
                    tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
                }
            }

            try {
                blockEntities = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        BinaryStream stream = ThreadCache.binaryStream.get().reset();
        int count = 0;
        cn.nukkit.level.format.ChunkSection[] sections = chunk.getSections();
        for (int i = sections.length - 1; i >= 0; i--) {
            if (!sections[i].isEmpty()) {
                count = i + 1;
                break;
            }
        }

        for (int i = 0; i < count; i++) {
            sections[i].writeTo(stream);
        }

        final byte[] biomeData = serializeBiome(chunk);
        for (int i = 0; i < 25; i++) {
            if (i >= count) {
                stream.putByte((byte) ((127 << 1) | 1)); //255
            } else {
                stream.put(biomeData);
            }
        }
        stream.putByte((byte) 0); // 教育版边界方块数据，这里用0b表示无此数据
        stream.putUnsignedVarInt(0); // 一个不知道作用的8字节，貌似全写0就可以
        stream.put(blockEntities);

        this.getLevel().chunkRequestCallback(timestamp, X, Z, count, stream.getBuffer());

        return null;
    }

    private byte[] serializeBiome(LDBChunk chunk) {
        final BinaryStream stream = new BinaryStream();
        final PalettedBlockStorage blockStorage = new PalettedBlockStorage();
        // 这个神奇的palette默认值害得我花了巨量世界调试
        // 务必清除默认值再进行群系操作
        blockStorage.palette.clear();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    final int bid = chunk.getBiomeId(x, z); // TODO: 2022/3/24 支持发送3D生物群系
                    if (BiomeLegacyId2StringIdMap.INSTANCE.legacy2String(bid) == null) {
                        blockStorage.setBlock((x << 8) | (z << 4) | y, 0); //回退到0群系，防止客户端崩溃
                    } else {
                        blockStorage.setBlock((x << 8) | (z << 4) | y, bid);
                    }
                }
            }
        }
        blockStorage.writeTo(stream);
        return stream.getBuffer();
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getGenerator() {
        return pnxData.getString("generatorName");
    }

    @Override
    public Map<String, Object> getGeneratorOptions() {
        return new HashMap<>() {
            {
                put("preset", pnxData.getString("generatorOptions"));
            }
        };
    }

    @Override
    public LDBChunk getLoadedChunk(int X, int Z) {
        var tmp = lastChunk.get();
        if (tmp != null && tmp.getX() == X && tmp.getZ() == Z) {
            return tmp;
        }
        var index = Level.chunkHash(X, Z);
        synchronized (chunks) {
            lastChunk.set(tmp = chunks.get(index));
        }
        return tmp;
    }

    @Override
    public LDBChunk getLoadedChunk(long hash) {
        var tmp = lastChunk.get();
        if (tmp != null && tmp.getIndex() == hash) {
            return tmp;
        }
        synchronized (chunks) {
            lastChunk.set(tmp = chunks.get(hash));
        }
        return tmp;
    }

    @Override
    public LDBChunk getChunk(int X, int Z) {
        return getChunk(X, Z, false);
    }

    @Override
    public LDBChunk getChunk(int X, int Z, boolean create) {
        var tmp = lastChunk.get();
        if (tmp != null && tmp.getX() == X && tmp.getZ() == Z) {
            return tmp;
        }
        var index = Level.chunkHash(X, Z);
        synchronized (chunks) {
            lastChunk.set(tmp = chunks.get(index));
        }
        if (tmp != null) {
            return tmp;
        } else {
            tmp = this.loadChunk(index, X, Z, create);
            lastChunk.set(tmp);
            return tmp;
        }
    }

    @Override
    public LDBChunk getEmptyChunk(int x, int z) {
        try {
            var chunk = new LDBChunk(this, level.getDimension(), x, z);
            chunk.terrainGenerated = false;
            chunk.terrainPopulated = false;
            return chunk;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveChunks() {
        synchronized (chunks) {
            for (var chunk : this.chunks.values()) {
                if (chunk.getChanges() != 0) {
                    chunk.setChanged(false);
                    this.saveChunk(chunk.getX(), chunk.getZ());
                }
            }
        }
    }

    @Override
    public void saveChunk(int X, int Z) {
        var chunk = this.getChunk(X, Z);
        try {
            saveChunk(chunk);
        } catch (IOException e) {
            throw new ChunkException("Error saving chunk (" + X + ", " + Z + ")", e);
        }
    }

    @Override
    public void saveChunk(int X, int Z, FullChunk chunk) {
        if (chunk instanceof LDBChunk ldbChunk) {
            ldbChunk.setX(X);
            ldbChunk.setZ(Z);
            try {
                saveChunk(ldbChunk);
            } catch (IOException e) {
                throw new ChunkException("Error saving chunk (" + X + ", " + Z + ")", e);
            }
        } else {
            throw new ChunkException("Invalid Chunk class");
        }
    }

    @Override
    public void unloadChunks() {
        var iter = chunks.values().iterator();
        while (iter.hasNext()) {
            iter.next().unload(true, false);
            iter.remove();
        }
    }

    @Override
    public boolean loadChunk(int X, int Z) {
        return loadChunk(X, Z, true);
    }

    @Override
    public boolean loadChunk(int X, int Z, boolean create) {
        long index = Level.chunkHash(X, Z);
        synchronized (chunks) {
            if (this.chunks.containsKey(index)) {
                return true;
            }
        }
        return loadChunk(index, X, Z, create) != null;
    }

    public synchronized LDBChunk loadChunk(long hash, int X, int Z, boolean create) {
        if (this.closed) {
            throw new IllegalStateException("Cannot retrieve chunk on closed provider.");
        }

        if (hash != Level.chunkHash(X, Z)) {
            throw new IllegalArgumentException("Invalid chunk hash");
        }

        this.level.timings.syncChunkLoadDataTimer.startTiming();

        var dimension = level.getDimension();
        if (!hasChunk(dimension, X, Z) && !create) {
            return null;
        }

        try {
            // 提取区块版本
            var chunkVersion = this.getChunkVersion(dimension, X, Z);
            // 提取区块高度图和群系网格
            var chunkDataPalette = this.getChunkData(dimension, X, Z);
            // 提取区块中的方块实体
            var blockEntities = this.getBlockEntitiesNbt(dimension, X, Z);
            // 提取区块中的生物实体
            var entities = this.getEntitiesNbt(dimension, X, Z);
            var chunk = new LDBChunk(this, dimension, X, Z);
            chunk.setVersion(chunkVersion);
            chunk.setHeightMap(chunkDataPalette.getHeightMap());
            chunk.setBiomeMap(chunkDataPalette.getBiomeMap());
            // 提取子区块
            if (dimension == 0) {
                for (int subChunkIndex = -4; subChunkIndex < 20; subChunkIndex++) {
                    chunk.setSection(subChunkIndex + 4, getSubChunk(dimension, X, Z, subChunkIndex + 4));
                }
            } else {
                for (int subChunkIndex = 0; subChunkIndex < 16; subChunkIndex++) {
                    chunk.setSection(subChunkIndex, getSubChunk(dimension, X, Z, subChunkIndex));
                }
            }
            for (var blockEntityNBT : blockEntities) {
                chunk.addInitialBlockEntityNbt(blockEntityNBT);
            }
            for (var entityNBT : entities) {
                chunk.addInitialEntityNbt(entityNBT);
            }
            chunks.put(hash, chunk);
            this.level.timings.syncChunkLoadDataTimer.stopTiming();
            return chunk;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.level.timings.syncChunkLoadDataTimer.stopTiming();
        return null;
    }

    @Override
    public boolean unloadChunk(int X, int Z) {
        return unloadChunk(X, Z, true);
    }

    @Override
    public boolean unloadChunk(int X, int Z, boolean safe) {
        long index = Level.chunkHash(X, Z);
        synchronized (chunks) {
            var chunk = this.chunks.get(index);
            if (chunk != null && chunk.unload(false, safe)) {
                lastChunk.set(null);
                this.chunks.remove(index, chunk);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isChunkGenerated(int X, int Z) {
        return isChunkGeneratedSet.contains(Level.chunkHash(X, Z));
    }

    @Override
    public boolean isChunkPopulated(int X, int Z) {
        return isChunkPopulatedSet.contains(Level.chunkHash(X, Z));
    }

    @Override
    public boolean isChunkLoaded(int X, int Z) {
        return isChunkLoaded(Level.chunkHash(X, Z));
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        synchronized (chunks) {
            return this.chunks.containsKey(hash);
        }
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (chunk instanceof LDBChunk ldbChunk) {
            ldbChunk.setProvider(this);
            ldbChunk.setPosition(chunkX, chunkZ);
            var index = Level.chunkHash(chunkX, chunkZ);
            synchronized (chunks) {
                if (this.chunks.containsKey(index) && !this.chunks.get(index).equals(ldbChunk)) {
                    this.unloadChunk(chunkX, chunkZ, false);
                }
                this.chunks.put(index, ldbChunk);
            }
        } else {
            throw new ChunkException("Invalid Chunk class");
        }
    }

    @Override
    public String getName() {
        return levelData.getName();
    }

    @Override
    public boolean isRaining() {
        return levelData.getRainLevel() > 0;
    }

    @Override
    public void setRaining(boolean raining) {
        levelData.setRainLevel(raining ? 1 : 0);
    }

    @Override
    public int getRainTime() {
        return levelData.getRainTime();
    }

    @Override
    public void setRainTime(int rainTime) {
        levelData.setRainTime(rainTime);
    }

    @Override
    public boolean isThundering() {
        return levelData.getLightningLevel() > 0;
    }

    @Override
    public void setThundering(boolean thundering) {
        levelData.setLightningLevel(thundering ? 1 : 0);
    }

    @Override
    public int getThunderTime() {
        return levelData.getLightningTime();
    }

    @Override
    public void setThunderTime(int thunderTime) {
        levelData.setLightningTime(thunderTime);
    }

    @Override
    public long getCurrentTick() {
        return levelData.getCurrentTick();
    }

    @Override
    public void setCurrentTick(long currentTick) {
        levelData.setCurrentTick(currentTick);
    }

    @Override
    public long getTime() {
        return levelData.getTime();
    }

    @Override
    public void setTime(long value) {
        levelData.setTime(value);
    }

    @Override
    public long getSeed() {
        return levelData.getSeed();
    }

    @Override
    public void setSeed(long value) {
        levelData.setSeed(value);
    }

    @Override
    public Vector3 getSpawn() {
        return levelData.getWorldSpawn();
    }

    @Override
    public void setSpawn(Vector3 pos) {
        levelData.setWorldSpawn(pos);
    }

    @Override
    public Map<Long, ? extends FullChunk> getLoadedChunks() {
        synchronized (chunks) {
            return ImmutableMap.copyOf(chunks);
        }
    }

    @Override
    public void doGarbageCollection() {
        // TODO: 2022/3/23 LDB地图垃圾回收
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public void close() {
        if (this.closed) {
            throw new IllegalStateException("This provider is already closed.");
        }
        this.closed = true;
        try {
            this.db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveLevelData() {
        try {
            LDBIO.writeLevelData(this.levelFile, this.levelData);
            this.pnxData.putList(new ListTag<>("chunkGenerated").addAll(isChunkGeneratedSet.longStream()
                    .mapToObj(value -> new LongTag("", value)).collect(Collectors.toSet())));
            this.pnxData.putList(new ListTag<>("chunkPopulated").addAll(isChunkPopulatedSet.longStream()
                    .mapToObj(value -> new LongTag("", value)).collect(Collectors.toSet())));
            NBTIO.write(pnxData, pnxLevelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLevelName(String name) {
        levelData.setName(name);
    }

    @Override
    public GameRules getGamerules() {
        return levelData.getGameRules();
    }

    @Override
    public void setGameRules(GameRules rules) {
        levelData.setGameRules(rules);
    }

    public boolean isClosed() {
        return closed;
    }

    private LDBChunkSection getSubChunk(int dimension, int x, int z, int subChunkIndex) throws IOException {
        if (this.isClosed()) {
            throw new IllegalStateException("Cannot retrieve chunk on closed provider.");
        }

        byte[] subChunkKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            subChunkKey = LDBChunkKey.SUB_CHUNK_DATA.getLevelDBKey(x, z, subChunkIndex);
        } else {
            subChunkKey = LDBChunkKey.SUB_CHUNK_DATA.getLevelDBKeyWithDimension(x, z, dimension, subChunkIndex);
        }

        byte[] subChunkData = this.db.get(subChunkKey);
        if (subChunkData == null) {
            subChunkData = new byte[]{8, 0};
        }

        ByteBuf buffer = Unpooled.wrappedBuffer(subChunkData);
        buffer.readerIndex(0);

        try {
            return LDBIO.readSubChunk(buffer);
        } finally {
            buffer.release();
        }
    }

    private boolean hasChunk(int dimension, int x, int z) {
        byte[] chunkKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            chunkKey = LDBChunkKey.VERSION.getLevelDBKey(x, z);
        } else {
            chunkKey = LDBChunkKey.VERSION.getLevelDBKeyWithDimension(x, z, dimension);
        }

        return this.db.get(chunkKey) != null;
    }

    private byte getChunkVersion(int dimension, int x, int z) {
        byte[] versionKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            versionKey = LDBChunkKey.VERSION.getLevelDBKey(x, z);
        } else {
            versionKey = LDBChunkKey.VERSION.getLevelDBKey(x, z, dimension);
        }

        byte[] versionData = this.db.get(versionKey);
        if (versionData == null) {
            versionData = new byte[]{(byte) CHUNK_VERSION};
            this.db.put(versionKey, versionData);
        }

        return versionData[0];
    }

    private void saveChunkVersion(int x, int z, int dimension, byte version) {
        byte[] versionKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            versionKey = LDBChunkKey.VERSION.getLevelDBKey(x, z);
        } else {
            versionKey = LDBChunkKey.VERSION.getLevelDBKey(x, z, dimension);
        }

        this.db.put(versionKey, new byte[]{version});
    }

    private LDBChunkData getChunkData(int dimension, int x, int z) throws IOException {
        byte[] heightAnd3DBiomeKey;
        byte[] heightAnd2DBiomeKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            heightAnd2DBiomeKey = LDBChunkKey.DATA_2D.getLevelDBKey(x, z);
            heightAnd3DBiomeKey = LDBChunkKey.DATA_3D.getLevelDBKey(x, z);
        } else {
            heightAnd2DBiomeKey = LDBChunkKey.DATA_2D.getLevelDBKeyWithDimension(x, z, dimension);
            heightAnd3DBiomeKey = LDBChunkKey.DATA_3D.getLevelDBKeyWithDimension(x, z, dimension);
        }


        byte[] heightAnd3DBiomeData = this.db.get(heightAnd3DBiomeKey);
        byte[] heightAnd2DBiomeData = this.db.get(heightAnd2DBiomeKey);

        // Check for 3D data before 2D data
        LDBChunkData chunkData;
        if (heightAnd3DBiomeData == null && heightAnd2DBiomeData == null) {
            // Biome and height map data does not exist.
            chunkData = new LDBChunkData(new LDBChunkHeightMap(), new LDBChunkBiomeMap());

            byte[] biomeData = LDBIO.biomeMapToBytes(chunkData.getBiomeMap());
            byte[] heightData = LDBIO.heightMapToBytes(chunkData.getHeightMap());

            // Put data together to form complete 3D data
            byte[] data = new byte[biomeData.length + heightData.length];
            System.arraycopy(heightData, 0, data, 0, heightData.length);
            System.arraycopy(biomeData, 0, data, heightData.length, biomeData.length);

            this.db.put(heightAnd3DBiomeKey, data);
        } else if (heightAnd3DBiomeData != null) {
            // 3D biome data
            chunkData = LDBIO.read3DChunkData(heightAnd3DBiomeData);
        } else {
            // 2D biome data
            chunkData = LDBIO.read2DChunkData(heightAnd2DBiomeData);
        }

        return chunkData;
    }

    private void saveData(int dimension, int x, int z, LDBChunkData chunkData) throws IOException {
        byte[] heightAnd3DBiomeKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            heightAnd3DBiomeKey = LDBChunkKey.DATA_3D.getLevelDBKey(x, z);
        } else {
            heightAnd3DBiomeKey = LDBChunkKey.DATA_3D.getLevelDBKeyWithDimension(x, z, dimension);
        }

        byte[] biomeData = LDBIO.biomeMapToBytes(chunkData.getBiomeMap());
        byte[] heightData = LDBIO.heightMapToBytes(chunkData.getHeightMap());

        // Put data together to form complete 3D data
        byte[] data = new byte[biomeData.length + heightData.length];
        System.arraycopy(heightData, 0, data, 0, heightData.length);
        System.arraycopy(biomeData, 0, data, heightData.length, biomeData.length);

        this.db.put(heightAnd3DBiomeKey, data);
    }

    private Set<CompoundTag> getBlockEntitiesNbt(int dimension, int x, int z) throws IOException {
        byte[] blockEntityKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            blockEntityKey = LDBChunkKey.BLOCK_ENTITIES.getLevelDBKey(x, z);
        } else {
            blockEntityKey = LDBChunkKey.BLOCK_ENTITIES.getLevelDBKeyWithDimension(x, z, dimension);
        }

        byte[] blockEntityData = this.db.get(blockEntityKey);
        if (blockEntityData == null) {
            blockEntityData = new byte[0];
        }

        var blockEntities = new HashSet<CompoundTag>();
        try (InputStream blockEntityDataStream = new ByteArrayInputStream(blockEntityData)) {
            while (blockEntityDataStream.available() > 0) {
                var blockEntityNBT = (CompoundTag) NBTIO.readTag(blockEntityDataStream, ByteOrder.LITTLE_ENDIAN, false);
                blockEntities.add(blockEntityNBT);
            }
        }

        return blockEntities;
    }

    private void saveBlockEntities(int dimension, int x, int z, Collection<BlockEntity> blockEntities) throws IOException {
        byte[] blockEntityKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            blockEntityKey = LDBChunkKey.BLOCK_ENTITIES.getLevelDBKey(x, z);
        } else {
            blockEntityKey = LDBChunkKey.BLOCK_ENTITIES.getLevelDBKeyWithDimension(x, z, dimension);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Write block entities nbt
            for (var blockEntity : blockEntities) {
                NBTIO.write(blockEntity.namedTag, outputStream, ByteOrder.LITTLE_ENDIAN, false);
            }
            byte[] data = outputStream.toByteArray();
            this.db.put(blockEntityKey, data);
        }
    }

    private Set<CompoundTag> getEntitiesNbt(int dimension, int x, int z) throws IOException {
        byte[] entitiesKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            entitiesKey = LDBChunkKey.ENTITIES.getLevelDBKey(x, z);
        } else {
            entitiesKey = LDBChunkKey.ENTITIES.getLevelDBKeyWithDimension(x, z, dimension);
        }

        byte[] entitiesData = this.db.get(entitiesKey);
        if (entitiesData == null) {
            entitiesData = new byte[0];
        }

        var entities = new HashSet<CompoundTag>();
        try (InputStream entityDataStream = new ByteArrayInputStream(entitiesData)) {
            while (entityDataStream.available() > 0) {
                entities.add((CompoundTag) NBTIO.readTag(entityDataStream, ByteOrder.LITTLE_ENDIAN, false));
            }
        }

        return entities;
    }

    private void saveEntities(int dimension, int x, int z, Collection<Entity> entities) throws IOException {
        byte[] entitiesKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            entitiesKey = LDBChunkKey.ENTITIES.getLevelDBKey(x, z);
        } else {
            entitiesKey = LDBChunkKey.ENTITIES.getLevelDBKeyWithDimension(x, z, dimension);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (var entity : entities) {
                NBTIO.write(entity.namedTag, outputStream, ByteOrder.LITTLE_ENDIAN, false);
            }
            byte[] data = outputStream.toByteArray();
            this.db.put(entitiesKey, data);
        }
    }

    private void saveChunk(LDBChunk bedrockChunk) throws IOException {
        if (this.isClosed()) {
            throw new IllegalStateException("Cannot retrieve chunk on closed provider.");
        }

        // Save chunk data
        this.saveChunkVersion(bedrockChunk.getDimension(), bedrockChunk.getX(), bedrockChunk.getZ(), bedrockChunk.getVersion());
        this.saveData(bedrockChunk.getDimension(), bedrockChunk.getX(), bedrockChunk.getZ(), new LDBChunkData(bedrockChunk.getHeightMap(), bedrockChunk.getBiomeMap()));
        this.saveBlockEntities(bedrockChunk.getDimension(), bedrockChunk.getX(), bedrockChunk.getZ(), bedrockChunk.getBlockEntities().values());
        this.saveEntities(bedrockChunk.getDimension(), bedrockChunk.getX(), bedrockChunk.getZ(), bedrockChunk.getEntities().values());

        // Save every subchunk
        if (bedrockChunk.getDimension() == Level.DIMENSION_OVERWORLD) {
            for (int subChunkIndex = -4; subChunkIndex < 20; subChunkIndex++) {
                var subChunk = bedrockChunk.getSection(subChunkIndex + 4);
                if (subChunk instanceof LDBChunkSection ldbChunkSection) {
                    this.saveSubChunk(bedrockChunk.getDimension(), bedrockChunk.getX(), bedrockChunk.getZ(), subChunkIndex, ldbChunkSection);
                }
            }
        } else {
            for (int subChunkIndex = 0; subChunkIndex < 16; subChunkIndex++) {
                var subChunk = bedrockChunk.getSection(subChunkIndex);
                if (subChunk instanceof LDBChunkSection ldbChunkSection) {
                    this.saveSubChunk(bedrockChunk.getDimension(), bedrockChunk.getX(), bedrockChunk.getZ(), subChunkIndex, ldbChunkSection);
                }
            }
        }
    }

    private void saveSubChunk(int dimension, int x, int z, int subChunkIndex, LDBChunkSection subChunk) throws IOException {
        if (this.isClosed()) {
            throw new IllegalStateException("Cannot retrieve chunk on closed provider.");
        }

        byte[] subChunkKey;
        if (dimension == Level.DIMENSION_OVERWORLD) {
            subChunkKey = LDBChunkKey.SUB_CHUNK_DATA.getLevelDBKey(x, z, subChunkIndex);
        } else {
            subChunkKey = LDBChunkKey.SUB_CHUNK_DATA.getLevelDBKeyWithDimension(x, z, dimension, subChunkIndex);
        }

        ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            LDBIO.writeSubChunk(buffer, subChunk);
            byte[] data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);
            this.db.put(subChunkKey, data);
        } finally {
            buffer.release();
        }
    }

    public File getDbDirectory() {
        return dbDirectory;
    }

    public DB getDb() {
        return db;
    }

    public static LDBChunkSection createChunkSection(int y) {
        var section = new LDBChunkSection(y);
        section.hasSkyLight = true;
        return section;
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator) throws IOException {
        generate(path, name, seed, generator, new HashMap<>());
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed resource leak")
    public static void generate(String path, String name, long seed, Class<? extends Generator> generator, Map<String, String> options) throws IOException {
        var dbDir = new File(path + "/db");
        if (!dbDir.exists() && !dbDir.mkdirs()) {
            throw new IOException("Could not create the directory " + dbDir.getAbsolutePath());
        }
        var db = net.daporkchop.ldbjni.LevelDB.PROVIDER.open(dbDir, new Options().createIfMissing(true));
        db.close();

        var pnxData = new CompoundTag()
                .putString("generatorName", Generator.getGeneratorName(generator))
                .putString("generatorOptions", options.getOrDefault("preset", ""));

        LDBIO.writeLevelData(new File(path, "level.dat"), LDBLevelData.getDefault(name, seed, Generator.getGeneratorType(generator)));

        Utils.safeWrite(new File(path, "level.pnx.dat"), file -> {
            try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream out = new BufferedOutputStream(fos)) {
                NBTIO.writeGZIPCompressed(pnxData, out, ByteOrder.BIG_ENDIAN);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public static String getProviderName() {
        return "leveldb";
    }

    public static byte getProviderOrder() {
        return ORDER_YZX;
    }

    public static boolean usesChunkSection() {
        return true;
    }

    public static boolean isValid(String path) {
        var dbDir = new File(path + "/db/");
        return (new File(path + "/level.dat").exists())
                && dbDir.exists() && dbDir.isDirectory()
                && new File(dbDir, "CURRENT").exists();
    }
}
