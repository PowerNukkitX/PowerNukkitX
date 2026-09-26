package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.format.BiomeState;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelChunkMetaData;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.ChunkGenerationState;
import org.powernukkitx.level.portal.PortalManager;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.level.village.Village;
import org.powernukkitx.level.village.VillageDwellers;
import org.powernukkitx.level.village.VillageInfo;
import org.powernukkitx.level.village.VillagePlayers;
import org.powernukkitx.level.village.VillagePois;
import org.powernukkitx.level.village.VillageRaid;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DynamicProperties;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;

import com.google.common.base.Preconditions;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public final class LevelDBStorage {
    private static final List<String> VILLAGE_COMPONENTS = List.of("DWELLERS", "INFO", "POI", "PLAYERS");
    private static final byte[] THE_END_KEY = "TheEnd".getBytes(StandardCharsets.UTF_8);
    private static final byte[] PORTALS_KEY = "portals".getBytes(StandardCharsets.UTF_8);
    private static final byte[] BIOME_DATA_KEY = LevelDBKeyUtil.getGlobalKey("BiomeData");
    private static final byte[] WORLD_CLOCKS_KEY = LevelDBKeyUtil.getGlobalKey("WorldClocks");
    private static final byte[] DYNAMIC_PROPERTIES_KEY = LevelDBKeyUtil.getGlobalKey("DynamicProperties");
    private static final byte[] PNX_EXTRA_DATA_KEY = LevelDBKeyUtil.PNX_EXTRA_DATA.getGlobalKey();
    private static final String TICKING_AREA_KEY_PREFIX = "tickingarea_";
    private static final byte[] TICKING_AREA_PREFIX = TICKING_AREA_KEY_PREFIX.getBytes(StandardCharsets.UTF_8);
    private static final String ACTOR_STORAGE_KEY_PLACEHOLDER = "PNXSKKEY";
    private static final byte[] ACTOR_STORAGE_KEY_TAG_MARKER = {
            8, 10, 0,
            'S', 't', 'o', 'r', 'a', 'g', 'e', 'K', 'e', 'y',
            8, 0,
            'P', 'N', 'X', 'S', 'K', 'K', 'E', 'Y'
    };
    private final DB db;
    private final String path;
    private final WorldMetadata worldMetadata;
    private final DynamicProperties dynamicProperties;
    private final LevelChunkMetaDataDictionary levelChunkMetaDataDictionary;
    private final LevelDBLimboEntities limboEntities;
    private final Set<Long> pendingActorDeletions = new HashSet<>();
    private List<LevelDBWorldClocksCodec.WorldClock> worldClocks;
    private CompoundTag worldDynamicProperties;
    private boolean worldDynamicPropertiesDirty;
    private PortalManager portalManager;
    private int refCount;

    public DB getDb() {
        return this.db;
    }

    WorldMetadata getWorldMetadata() {
        return this.worldMetadata;
    }

    /**
     * Returns the dynamic properties backed by this world storage.
     *
     * @return dynamic properties
     */
    public DynamicProperties getDynamicProperties() {
        return this.dynamicProperties;
    }

    /**
     * Returns the portal manager backed by this world storage.
     *
     * @return portal manager
     */
    public synchronized PortalManager getPortalManager() {
        if (portalManager == null) {
            portalManager = new PortalManager(this);
        }
        return portalManager;
    }

    public LevelDBStorage(int refCount, String path) throws IOException {
        this(refCount, path, createDefaultOptions());
    }

    private static Options createDefaultOptions() {
        long totalMemory = getTotalPhysicalMemory();

        long cacheSize;
        int writeBufferSize;

        if (totalMemory <= 230L * 1024L * 1024L) {
            cacheSize = 20L * 1024L * 1024L;
            writeBufferSize = 1 * 1024 * 1024;
        } else if (totalMemory <= 2L * 1024L * 1024L * 1024L) {
            cacheSize = 40L * 1024L * 1024L;
            writeBufferSize = 4 * 1024 * 1024;
        } else {
            cacheSize = 80L * 1024L * 1024L;
            writeBufferSize = 4 * 1024 * 1024;
        }

        return new Options()
                .createIfMissing(true)
                .writeBufferSize(writeBufferSize)
                .compressionType(CompressionType.ZLIB_RAW)
                .blockSize(160 * 1024)
                .cacheSize(cacheSize);
    }

    private static long getTotalPhysicalMemory() {
        var operatingSystem = ManagementFactory.getOperatingSystemMXBean();

        if (operatingSystem instanceof com.sun.management.OperatingSystemMXBean systemBean) {
            return systemBean.getTotalMemorySize();
        }

        return Runtime.getRuntime().maxMemory();
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
        db = LevelDBBackendFactory.open(dbFolder, options);
        this.worldMetadata = new WorldMetadata(pathFolder, this);
        levelChunkMetaDataDictionary = new LevelChunkMetaDataDictionary(db);
        limboEntities = new LevelDBLimboEntities(this);
        dynamicProperties = new DynamicProperties(this::getWorldDynamicProperties, this::setWorldDynamicProperties);
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

        DimensionData dimensionData = levelProvider.getDimensionData();
        builder.levelChunkMetaData(readChunkMetaData(x, z, dimensionData));

        Chunk chunk = builder.build();
        Preconditions.checkState(
                chunk.compareAndSetGenerationState(
                        ChunkGenerationState.NEEDS_GENERATION,
                        ChunkGenerationState.NEEDS_CFRD),
                "Loaded chunk (%s, %s) has an unexpected runtime generation state",
                x,
                z
        );
        return chunk;
    }

    private LevelChunkMetaData readChunkMetaData(int x, int z, DimensionData dimensionData) {
        byte[] key = LevelDBKeyUtil.LEVEL_CHUNK_METADATA.getKey(x, z, dimensionData);
        byte[] value = this.db.get(key);

        if (value == null) {
            return LevelChunkMetaData.uninitialized();
        }

        if (value.length != Long.BYTES) {
            return LevelChunkMetaData.invalid();
        }

        long hash = LevelChunkMetaDataDictionary.hashFromLittleEndian(value);
        NbtMap metadata = levelChunkMetaDataDictionary.get(hash);

        if (metadata == null) {
            return LevelChunkMetaData.unresolved(hash);
        }

        return LevelChunkMetaData.resolved(hash, metadata);
    }

    void loadLimboEntities(DimensionData dimensionData) {
        this.limboEntities.loadDimension(dimensionData);
    }

    void saveLimboEntities(DimensionData dimensionData) {
        this.limboEntities.saveDimension(dimensionData);
    }

    void deferActorToLimbo(IChunk sourceChunk, CompoundTag actorTag, int targetChunkX, int targetChunkZ) {
        this.limboEntities.deferActor(sourceChunk, actorTag, targetChunkX, targetChunkZ);
    }

    void consumeLimboEntities(IChunk chunk) {
        this.limboEntities.consumeChunk(chunk);
    }

    void initializeGeneratedLimboEntities(DimensionData dimensionData) {
        this.limboEntities.initializeGeneratedDimension(dimensionData);
    }

    /**
     * Creates a write batch for this LevelDB instance.
     *
     * @return write batch
     */
    public WriteBatch createBatch() {
        return this.db.createWriteBatch();
    }

    public void writeChunk(IChunk chunk) throws IOException {
        if (chunk.getGenerationState() != ChunkGenerationState.COMPLETE) return;

        LevelChunkMetaData persistedMetaData = null;
        BiomeState biomeState = chunk.getBiomeState();
        long biomeStateVersion = biomeState.hasStorageChanges() ? biomeState.getStorageChangeVersion() : -1;

        try (WriteBatch writeBatch = createBatch()) {
            LevelDBChunkSerializer.INSTANCE.serialize(writeBatch, chunk);
            writePendingActorDeletions(writeBatch);
            writeChunkActorData(writeBatch, chunk);
            persistedMetaData = prepareChunkMetaData(writeBatch, chunk);
            writeLevelChunkMetaDataDictionary(writeBatch);
            writeBatch(writeBatch);
        }

        if (biomeStateVersion >= 0) {
            biomeState.markStorageSaved(biomeStateVersion);
        }
        if (persistedMetaData != null) {
            chunk.setLevelChunkMetaData(persistedMetaData);
        }
    }

    /**
     * Queues deletion of persisted actor data.
     *
     * @param actorUniqueId actor unique ID
     */
    public synchronized void queueActorDeletion(long actorUniqueId) {
        if (actorUniqueId == 0) return;
        pendingActorDeletions.add(LevelDBActorStorage.getActorStorageKey(actorUniqueId));
    }

    synchronized void writePendingActorDeletions(WriteBatch writeBatch) throws IOException {
        if (pendingActorDeletions.isEmpty()) return;

        byte[] digestPrefix = LevelDBActorStorage.getDigestPrefix();

        try (DBIterator iterator = this.db.iterator()) {
            for (iterator.seek(digestPrefix); iterator.hasNext(); iterator.next()) {
                var entry = iterator.peekNext();
                if (!startsWith(entry.getKey(), digestPrefix)) break;
                List<Long> actorStorageKeys = LevelDBActorStorage.readDigest(entry.getValue());

                if (actorStorageKeys.removeIf(pendingActorDeletions::contains)) {
                    writeBatch.put(entry.getKey(), LevelDBActorStorage.writeDigest(actorStorageKeys));
                }
            }
        }

        for (long actorStorageKey : pendingActorDeletions) {
            writeBatch.delete(LevelDBActorStorage.getActorKey(actorStorageKey));
        }

        pendingActorDeletions.clear();
    }

    void writeChunkActorData(WriteBatch writeBatch, IChunk chunk) {
        List<Entity> entitySnapshot = new ArrayList<>(chunk.getEntities().values());
        List<Long> actorStorageKeys = new ArrayList<>();

        for (Entity entity : entitySnapshot) {
            if (entity instanceof Player || entity.closed || !entity.canBeSavedWithChunk()) continue;

            try {
                CompoundTag tag = entity.serializationSnapshot;

                if (tag != null) {
                    entity.serializationSnapshot = null;
                } else {
                    entity.saveNBT();
                    tag = entity.getNbt().copy();
                }

                long actorStorageKey = LevelDBActorStorage.getActorStorageKey(entity.uniqueIdLong());
                actorStorageKeys.add(actorStorageKey);
                writeBatch.put(LevelDBActorStorage.getActorKey(actorStorageKey), writeActorCompound(tag, actorStorageKey));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to serialize entity " + entity.getIdentifier() + " in chunk [" + chunk.getX() + "," + chunk.getZ() + "]", e);
            }
        }

        writeBatch.put(LevelDBActorStorage.getDigestKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData()), LevelDBActorStorage.writeDigest(actorStorageKeys));
        writeBatch.put(LevelDBKeyUtil.ACTOR_DIGEST_VERSION.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData()), new byte[]{0});
    }

    static byte[] writeActorCompound(CompoundTag tag, long actorStorageKey) {
        CompoundTag internalComponents = tag.containsCompound("internalComponents")
                ? tag.getCompound("internalComponents").copy()
                : new CompoundTag();
        CompoundTag storageKeyComponent = internalComponents.containsCompound("EntityStorageKeyComponent")
                ? internalComponents.getCompound("EntityStorageKeyComponent").copy()
                : new CompoundTag();

        storageKeyComponent.putString("StorageKey", ACTOR_STORAGE_KEY_PLACEHOLDER);
        internalComponents.putCompound("EntityStorageKeyComponent", storageKeyComponent);
        tag.putCompound("internalComponents", internalComponents);

        byte[] serialized = writeLittleEndianCompound(tag);
        int storageKeyOffset = findActorStorageKeyOffset(serialized);
        byte[] actorKey = LevelDBActorStorage.getActorKey(actorStorageKey);
        System.arraycopy(actorKey, actorKey.length - Long.BYTES, serialized, storageKeyOffset, Long.BYTES);
        return serialized;
    }

    private static int findActorStorageKeyOffset(byte[] serialized) {
        int result = -1;

        for (int i = 0; i <= serialized.length - ACTOR_STORAGE_KEY_TAG_MARKER.length; i++) {
            boolean matches = true;
            for (int j = 0; j < ACTOR_STORAGE_KEY_TAG_MARKER.length; j++) {
                if (serialized[i + j] != ACTOR_STORAGE_KEY_TAG_MARKER[j]) {
                    matches = false;
                    break;
                }
            }

            if (!matches) continue;

            Preconditions.checkState(result == -1, "Actor StorageKey marker occurs more than once");
            result = i + ACTOR_STORAGE_KEY_TAG_MARKER.length - Long.BYTES;
        }

        Preconditions.checkState(result >= 0, "Actor StorageKey marker not found");
        return result;
    }

    private LevelChunkMetaData prepareChunkMetaData(WriteBatch writeBatch, IChunk chunk) {
        return prepareChunkMetaData(
                writeBatch,
                chunk,
                null,
                LevelChunkMetaData.CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION
        );
    }

    /**
     * Writes the chunk metadata reference using the chunk's provider generator type.
     *
     * @param writeBatch target write batch
     * @param chunk chunk to persist
     */
    public void writeChunkMetaData(WriteBatch writeBatch, IChunk chunk) {
        prepareChunkMetaData(writeBatch, chunk);
    }

    /**
     * Writes the chunk metadata reference using the specified generator type.
     *
     * @param writeBatch target write batch
     * @param chunk chunk to persist
     * @param generatorType generator type
     */
    public void writeChunkMetaData(WriteBatch writeBatch, IChunk chunk, int generatorType) {
        prepareChunkMetaData(
                writeBatch,
                chunk,
                generatorType,
                LevelChunkMetaData.CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION
        );
    }

    /**
     * Writes the chunk metadata reference using the specified metadata versions.
     *
     * @param writeBatch target write batch
     * @param chunk chunk to persist
     * @param generatorType generator type
     * @param neighborAwareBlockUpgradeVersion neighbor-aware block upgrade version
     */
    public void writeChunkMetaData(WriteBatch writeBatch, IChunk chunk, int generatorType, int neighborAwareBlockUpgradeVersion) {
        prepareChunkMetaData(writeBatch, chunk, generatorType, neighborAwareBlockUpgradeVersion);
    }

    private LevelChunkMetaData prepareChunkMetaData(WriteBatch writeBatch, IChunk chunk, Integer generatorType, int neighborAwareBlockUpgradeVersion) {
        LevelChunkMetaData chunkMetaData = chunk.getLevelChunkMetaData();
        NbtMap metadata;

        if (chunkMetaData.getState() == LevelChunkMetaData.State.UNINITIALIZED) {
            if (generatorType == null) {
                Preconditions.checkState(chunk.getProvider() instanceof LevelDBProvider, "LevelChunkMetaData requires LevelDBProvider");
                LevelDBProvider provider = (LevelDBProvider) chunk.getProvider();
                generatorType = provider.getLevelData().getGenerator();
            }

            NbtMap originalMetadata = LevelChunkMetaDataFactory.createForChunk(
                    chunk,
                    generatorType,
                    neighborAwareBlockUpgradeVersion
            );
            levelChunkMetaDataDictionary.register(originalMetadata);
            metadata = LevelChunkMetaDataFactory.updateForSave(originalMetadata, chunk);
        } else if (chunkMetaData.getState() == LevelChunkMetaData.State.RESOLVED) {
            metadata = LevelChunkMetaDataFactory.updateForSave(chunkMetaData.getMetadata(), chunk);
        } else {
            return null;
        }

        long hash = levelChunkMetaDataDictionary.register(metadata);

        if (chunkMetaData.hasPersistedHash() && hash == chunkMetaData.getPersistedHash()) {
            return null;
        }

        byte[] metadataKey = LevelDBKeyUtil.LEVEL_CHUNK_METADATA.getKey(
                chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData());
        writeBatch.put(metadataKey, LevelChunkMetaDataDictionary.hashToLittleEndian(hash));
        return LevelChunkMetaData.resolved(hash, metadata);
    }

    /**
     * Writes pending LevelChunkMetaData dictionary entries.
     *
     * @param writeBatch target write batch
     */
    public void writeLevelChunkMetaDataDictionary(WriteBatch writeBatch) {
        levelChunkMetaDataDictionary.write(writeBatch);
    }

    /**
     * Commits a LevelDB write batch without forcing synchronous disk flush.
     *
     * @param writeBatch batch to commit
     */
    public void writeBatch(WriteBatch writeBatch) {
        WriteOptions writeOptions = new WriteOptions();
        writeOptions.sync(false);
        this.db.write(writeBatch, writeOptions);
    }

    /**
     * Reads the global {@code TheEnd} compound.
     *
     * @return stored data, or {@code null} when absent
     */
    public CompoundTag readTheEndData() {
        return readGlobalCompound(THE_END_KEY);
    }

    /**
     * Writes the global {@code TheEnd} compound.
     *
     * @param tag data to persist, or {@code null} to delete it
     */
    public void writeTheEndData(CompoundTag tag) {
        writeGlobalCompound(THE_END_KEY, tag);
    }

    /**
     * Reads the global portal data compound.
     *
     * @return stored portal data, or {@code null} when absent
     */
    public CompoundTag readPortalsData() {
        return readGlobalCompound(PORTALS_KEY);
    }

    /**
     * Writes the global portal data compound.
     *
     * @param tag data to persist, or {@code null} to delete it
     */
    public void writePortalsData(CompoundTag tag) {
        writeGlobalCompound(PORTALS_KEY, tag);
    }

    synchronized List<LevelDBWorldClocksCodec.WorldClock> readWorldClocks() {
        if (this.worldClocks == null) {
            CompoundTag tag = readGlobalCompound(WORLD_CLOCKS_KEY);
            this.worldClocks = tag == null ? new ArrayList<>() : new ArrayList<>(LevelDBWorldClocksCodec.decode(tag));
        }
        return List.copyOf(this.worldClocks);
    }

    synchronized void writeWorldClocks(int time) {
        List<LevelDBWorldClocksCodec.WorldClock> clocks = new ArrayList<>(readWorldClocks());
        boolean overworldFound = false;

        for (int i = 0; i < clocks.size(); i++) {
            LevelDBWorldClocksCodec.WorldClock clock = clocks.get(i);
            if (!LevelDBWorldClocksCodec.OVERWORLD_CLOCK_NAME.equals(clock.name())) continue;

            clocks.set(i, new LevelDBWorldClocksCodec.WorldClock(clock.name(), time, clock.paused()));
            overworldFound = true;
            break;
        }

        if (!overworldFound) {
            clocks.add(new LevelDBWorldClocksCodec.WorldClock(LevelDBWorldClocksCodec.OVERWORLD_CLOCK_NAME, time, false));
        }

        this.worldClocks = clocks;
        writeGlobalCompound(WORLD_CLOCKS_KEY, LevelDBWorldClocksCodec.encode(clocks));
    }

    /**
     * Reads the PNX-specific global extra-data compound.
     *
     * @return stored extra data, or an empty compound when absent
     */
    public synchronized CompoundTag readPNXExtraData() {
        byte[] bytes = this.db.get(PNX_EXTRA_DATA_KEY);
        return bytes == null ? new CompoundTag() : readBigEndianCompound(bytes);
    }

    /**
     * Writes the PNX-specific global extra-data compound.
     *
     * @param tag data to persist, or {@code null} or empty to delete it
     */
    public synchronized void writePNXExtraData(CompoundTag tag) {
        try (WriteBatch writeBatch = this.db.createWriteBatch()) {
            if (tag == null || tag.isEmpty()) {
                writeBatch.delete(PNX_EXTRA_DATA_KEY);
            } else {
                writeBatch.put(PNX_EXTRA_DATA_KEY, writeBigEndianCompound(tag));
            }
            this.db.write(writeBatch, new WriteOptions().sync(false));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write PNX extra data", e);
        }
    }

    /**
     * Reads all persisted ticking-area compounds.
     *
     * @return ticking areas indexed by UUID
     */
    public Map<UUID, CompoundTag> readTickingAreas() {
        Map<UUID, CompoundTag> areas = new HashMap<>();
        try (DBIterator iterator = this.db.iterator()) {
            for (iterator.seek(TICKING_AREA_PREFIX); iterator.hasNext(); iterator.next()) {
                var entry = iterator.peekNext();
                if (!startsWith(entry.getKey(), TICKING_AREA_PREFIX)) {
                    break;
                }
                String key = new String(entry.getKey(), StandardCharsets.UTF_8);
                String uuidText = key.substring(TICKING_AREA_KEY_PREFIX.length());
                try {
                    UUID uuid = UUID.fromString(uuidText);
                    if (uuid.toString().equals(uuidText)) {
                        areas.put(uuid, readLittleEndianCompound(entry.getValue()));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return areas;
    }

    /**
     * Writes a persisted ticking area.
     *
     * @param uuid ticking-area UUID
     * @param tag ticking-area data
     */
    public void writeTickingArea(UUID uuid, CompoundTag tag) {
        writeGlobalCompound(getTickingAreaKey(uuid), tag);
    }

    /**
     * Deletes a persisted ticking area.
     *
     * @param uuid ticking-area UUID
     */
    public void deleteTickingArea(UUID uuid) {
        writeGlobalCompound(getTickingAreaKey(uuid), null);
    }

    private static byte[] getTickingAreaKey(UUID uuid) {
        return (TICKING_AREA_KEY_PREFIX + uuid).getBytes(StandardCharsets.UTF_8);
    }

    NbtMap readBiomeData() {
        CompoundTag tag = readGlobalCompound(BIOME_DATA_KEY);
        return tag == null ? NbtMap.EMPTY : tag.toNetwork();
    }

    void writeBiomeData(NbtMap biomeData) {
        if (biomeData == null || biomeData.isEmpty()) return;
        writeGlobalCompound(BIOME_DATA_KEY, CompoundTag.fromNetwork(biomeData));
    }

    CompoundTag readGlobalCompound(byte[] key) {
        byte[] bytes = this.db.get(key);
        return bytes == null ? null : readLittleEndianCompound(bytes);
    }

    void writeGlobalCompound(byte[] key, CompoundTag tag) {
        try (WriteBatch writeBatch = this.db.createWriteBatch()) {
            if (tag == null) {
                writeBatch.delete(key);
            } else {
                writeBatch.put(key, writeLittleEndianCompound(tag));
            }
            this.db.write(writeBatch, new WriteOptions().sync(false));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write global LevelDB compound", e);
        }
    }

    /**
     * Returns the cached world dynamic-properties compound, loading it when required.
     *
     * @return world dynamic-properties data
     */
    public synchronized CompoundTag getWorldDynamicProperties() {
        if (this.worldDynamicProperties == null) {
            CompoundTag tag = readWorldDynamicProperties();
            this.worldDynamicProperties = tag == null ? new CompoundTag() : tag;
        }
        return this.worldDynamicProperties;
    }

    /**
     * Replaces the cached world dynamic-properties data and marks it dirty.
     *
     * @param tag world dynamic-properties data
     */
    public synchronized void setWorldDynamicProperties(CompoundTag tag) {
        this.worldDynamicProperties = tag == null ? new CompoundTag() : tag;
        this.worldDynamicPropertiesDirty = true;
    }

    /**
     * Returns whether world dynamic properties contain unsaved changes.
     *
     * @return whether the cached data is dirty
     */
    public synchronized boolean isWorldDynamicPropertiesDirty() {
        return this.worldDynamicPropertiesDirty;
    }

    /**
     * Sets the world dynamic-properties dirty state.
     *
     * @param dirty dirty state
     */
    public synchronized void setWorldDynamicPropertiesDirty(boolean dirty) {
        this.worldDynamicPropertiesDirty = dirty;
    }

    /**
     * Persists world dynamic properties when they contain unsaved changes.
     */
    public synchronized void flushWorldDynamicProperties() {
        if (!this.worldDynamicPropertiesDirty) return;
        writeWorldDynamicProperties(this.worldDynamicProperties);
    }

    public CompoundTag readWorldDynamicProperties() {
        try {
            byte[] bytes = this.db.get(DYNAMIC_PROPERTIES_KEY);
            if (bytes == null) return null;
            try (var inputStream = new ByteArrayInputStream(bytes);
                 var nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
                return CompoundTag.fromNetwork((NbtMap) nbtInputStream.readTag());
            }
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized void writeWorldDynamicProperties(CompoundTag tag) {
        this.worldDynamicProperties = tag == null ? new CompoundTag() : tag;
        try (WriteBatch writeBatch = this.db.createWriteBatch()) {
            byte[] key = DYNAMIC_PROPERTIES_KEY;
            NbtMap safe = this.worldDynamicProperties.toNetwork();
            try (var outputStream = new ByteArrayOutputStream();
                 var nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
                nbtOutputStream.writeTag(safe);
                writeBatch.put(key, outputStream.toByteArray());
            }

            WriteOptions writeOptions = new WriteOptions().sync(false);
            this.db.write(writeBatch, writeOptions);
            this.worldDynamicPropertiesDirty = false;
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
                        VillagePois.fromCompound(data.get("POI")),
                        data.containsKey("RAID") ? VillageRaid.fromCompound(data.get("RAID")) : null));
            }
        });
        return villages;
    }

    public void writeVillages(DimensionData dimension, List<Village> villages) {
        String prefix = getVillagePrefix(dimension);
        Pattern pattern = getVillageKeyPattern(prefix);
        Set<UUID> villageIds = new HashSet<>();
        villages.forEach(village -> villageIds.add(village.uuid()));
        byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);
        try (WriteBatch batch = this.db.createWriteBatch(); DBIterator iterator = this.db.iterator()) {
            for (iterator.seek(prefixBytes); iterator.hasNext(); iterator.next()) {
                byte[] key = iterator.peekNext().getKey();
                if (!startsWith(key, prefixBytes)) break;
                var matcher = pattern.matcher(new String(key, StandardCharsets.UTF_8));
                if (matcher.matches() && !villageIds.contains(UUID.fromString(matcher.group(1)))) batch.delete(key);
            }

            for (Village village : villages) {
                String villagePrefix = prefix + village.uuid() + '_';
                batch.put(getVillageKey(villagePrefix, "DWELLERS"), writeLittleEndianCompound(village.dwellers().toCompound()));
                batch.put(getVillageKey(villagePrefix, "INFO"), writeLittleEndianCompound(village.info().toCompound()));
                batch.put(getVillageKey(villagePrefix, "PLAYERS"), writeLittleEndianCompound(village.players().toCompound()));
                batch.put(getVillageKey(villagePrefix, "POI"), writeLittleEndianCompound(village.pois().toCompound()));
                byte[] raidKey = getVillageKey(villagePrefix, "RAID");
                if (village.raid() == null) batch.delete(raidKey);
                else batch.put(raidKey, writeLittleEndianCompound(village.raid().toCompound()));
            }
            this.db.write(batch, new WriteOptions().sync(false));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write villages", e);
        }
    }

    private static String getVillagePrefix(DimensionData dimension) {
        String dimensionName = switch (dimension.getDimensionId()) {
            case Level.DIMENSION_OVERWORLD -> "Overworld";
            case Level.DIMENSION_NETHER -> "Nether";
            case Level.DIMENSION_THE_END -> "TheEnd";
            default -> dimension.getDimensionName();
        };
        return "VILLAGE_" + dimensionName + '_';
    }

    private static Pattern getVillageKeyPattern(String prefix) {
        return Pattern.compile(Pattern.quote(prefix)
                + "([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})_"
                + "(DWELLERS|INFO|PLAYERS|POI|RAID)");
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

    private static CompoundTag readBigEndianCompound(byte[] data) {
        try (var input = new ByteArrayInputStream(data); var reader = NbtUtils.createReader(input)) {
            return CompoundTag.fromNetwork((NbtMap) reader.readTag());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static byte[] writeBigEndianCompound(CompoundTag tag) {
        try (var output = new ByteArrayOutputStream(); var writer = NbtUtils.createWriter(output)) {
            writer.writeTag(tag.toNetwork());
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static CompoundTag readLittleEndianCompound(byte[] data) {
        try (var input = new ByteArrayInputStream(data); var reader = NbtUtils.createReaderLE(input)) {
            return CompoundTag.fromNetwork((NbtMap) reader.readTag());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static byte[] writeLittleEndianCompound(CompoundTag tag) {
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
                    flushWorldDynamicProperties();
                    if (portalManager != null) {
                        portalManager.save();
                    }
                    db.close();
                    LevelDBProvider.CACHE.remove(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }
}
