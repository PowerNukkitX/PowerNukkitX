package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.api.UsedByReflection;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityMobSpawner;
import org.powernukkitx.blockentity.BlockEntitySpawnable;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.GameRules;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.BiomeState;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.format.UnsafeChunk;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.powernukkitx.level.updater.block.BlockStateUpdaters;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.migration.leveldb.LevelDBMigrationVersionStore;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.BlockUpdateEntry;
import org.powernukkitx.utils.ChunkException;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.SemVersion;
import org.powernukkitx.utils.collection.nb.Long2ObjectNonBlockingMap;

import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.GameType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.iq80.leveldb.WriteBatch;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CoolLoong (PNX Project)
 */
@Slf4j
public class LevelDBProvider implements LevelProvider {
    static final Map<String, LevelDBStorage> CACHE = new ConcurrentHashMap<>();
    private static final int LEVEL_DAT_VERSION = 10;
    private static final int BIOME_STATE_SAMPLE_ATTEMPTS = 10;
    private final ThreadLocal<WeakReference<IChunk>> lastChunk = new ThreadLocal<>();
    protected final Long2ObjectNonBlockingMap<IChunk> chunks = new Long2ObjectNonBlockingMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<IChunk>> loadingChunks = new ConcurrentHashMap<>();
    private final Map<Long, List<LevelDBChunkSerializer.ScheduledTickInfo>> scheduledTicksMap = new ConcurrentHashMap<>();
    private final Map<Long, LevelDBChunkSerializer.RandomTickData> randomTicksMap = new ConcurrentHashMap<>();
    private final Map<Long, List<LevelDBChunkSerializer.NormalTickInfo>> normalTicksMap = new ConcurrentHashMap<>();
    protected final LevelDat levelDat;
    protected final LevelDBStorage storage;
    protected final Level level;
    protected final String path;
    private long runtimeTime;
    private long runtimeCurrentTick;
    private boolean runtimeRaining;
    private int runtimeRainTime;
    private boolean runtimeThundering;
    private int runtimeThunderTime;
    private int runtimeNoSleepNight;
    private final WorldBiomeSnowState biomeSnowState;
    /**
     * Network bytes an absent section serialises to, indexed by section Y offset into the byte
     * range. Sized from {@link Byte} because {@link ChunkSection#y()} is a {@code byte} that the
     * wire format writes as a single signed byte, so a taller dimension cannot widen this without
     * changing the section format first.
     * <p>
     * Shared by every level: the bytes depend only on the section Y, so two dimensions at the
     * same Y - and two worlds of the same dimension - produce identical output. See
     * {@link #emptySectionPayload(int)}.
     */
    private static final AtomicReferenceArray<byte[]> EMPTY_SECTION_PAYLOADS =
            new AtomicReferenceArray<>(1 << Byte.SIZE);

    /**
     * @return int The nether coordinate scale for the world
     */
    public int getNetherScale() {
        return this.levelDat.getNetherScale();
    }

    public LevelDBStorage getStorage() {
        return this.storage;
    }

    /**
     * Advances the world-level biome snow and foliage accumulation state.
     */
    public void tickBiomeSnowAccumulation(float previousRainLevel, float currentRainLevel) {
        if (getDimensionData().getDimensionId() != Level.DIMENSION_OVERWORLD) return;
        biomeSnowState.tick(previousRainLevel, currentRainLevel);
    }

    /**
     * Applies live precipitation snow attempts to a ticking chunk.
     */
    public void tickBiomeSnowPrecipitation(IChunk chunk, int attempts) {
        if (getDimensionData().getDimensionId() != Level.DIMENSION_OVERWORLD || attempts <= 0) return;
        BiomeSnowPrecipitation.tick(level, chunk, attempts, level.getRainLevel());
    }

    /**
     * Performs one BiomeState maintenance pass for a loader ticking view.
     */
    public void tickBiomeSnowReconciliation(int centerChunkX, int centerChunkZ, int radius) {
        if (getDimensionData().getDimensionId() != Level.DIMENSION_OVERWORLD || radius < 0) return;
        sampleBiomeSnowArea(
                centerChunkX - radius,
                centerChunkZ - radius,
                centerChunkX + radius,
                centerChunkZ + radius,
                null
        );
    }

    /**
     * Performs one BiomeState maintenance pass for an explicit ticking area.
     */
    public void tickBiomeSnowReconciliation(TickingArea area) {
        if (getDimensionData().getDimensionId() != Level.DIMENSION_OVERWORLD
                || area.getDimensionId() != Level.DIMENSION_OVERWORLD
                || !level.getName().equals(area.getLevelName())
                || area.getChunks().isEmpty()) {
            return;
        }

        List<TickingArea.ChunkPos> bounds = area.minAndMaxChunkPos();
        TickingArea.ChunkPos min = bounds.get(0);
        TickingArea.ChunkPos max = bounds.get(1);
        sampleBiomeSnowArea(min.x, min.z, max.x, max.z, area);
    }

    private void sampleBiomeSnowArea(int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ, @Nullable TickingArea area) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int attempt = 0; attempt < BIOME_STATE_SAMPLE_ATTEMPTS; attempt++) {
            int chunkX = randomChunkCoordinate(random, minChunkX, maxChunkX);
            int chunkZ = randomChunkCoordinate(random, minChunkZ, maxChunkZ);

            if (area != null && !area.getChunks().contains(new TickingArea.ChunkPos(chunkX, chunkZ))) {
                continue;
            }

            IChunk chunk = level.getChunkIfLoaded(chunkX, chunkZ);
            if (chunk == null || !hasStaleBiomeState(chunk)) {
                continue;
            }

            BiomeSnowChunkReconciler.reconcile(level, chunk);
            refreshBiomeState(chunk);
            return;
        }
    }

    private static int randomChunkCoordinate(ThreadLocalRandom random, int min, int max) {
        return min == max ? min : random.nextInt(min, max + 1);
    }

    @Override
    public boolean deferEntityChunkMove(Entity entity, int targetChunkX, int targetChunkZ) {
        if (!LevelDBLimboEntities.supportsDimension(getDimensionData()) || entity instanceof Player
                || entity.chunk == null || entity.isClosed() || !entity.canBeSavedWithChunk()) {
            return false;
        }

        try {
            LevelDBActorStorage.getActorStorageKey(entity.uniqueIdLong());
        } catch (IllegalArgumentException e) {
            return false;
        }

        entity.saveNBT();
        this.storage.deferActorToLimbo(entity.chunk, entity.getNbt().copy(), targetChunkX, targetChunkZ);
        return true;
    }

    @Override
    public void onChunkInitialized(IChunk chunk) {
        if (LevelDBLimboEntities.supportsDimension(getDimensionData())) {
            this.storage.consumeLimboEntities(chunk);
        }
    }

    private boolean hasStaleBiomeState(IChunk chunk) {
        boolean[] stale = {false};

        chunk.batchProcess(unsafeChunk -> {
            for (var entry : unsafeChunk.getBiomeState().snowAccumulation().int2ByteEntrySet()) {
                int current = (int) (biomeSnowState.getSnowAccumulation(entry.getIntKey()) * 8.0f);
                if (Byte.toUnsignedInt(entry.getByteValue()) != (current & 0xff)) {
                    stale[0] = true;
                    return;
                }
            }
        });

        return stale[0];
    }

    private void refreshBiomeState(IChunk chunk) {
        boolean[] changed = {false};

        chunk.batchProcess(unsafeChunk -> {
            BiomeState biomeState = unsafeChunk.getBiomeState();

            for (var entry : biomeState.snowAccumulation().int2ByteEntrySet()) {
                byte current = (byte) (int) (biomeSnowState.getSnowAccumulation(entry.getIntKey()) * 8.0f);
                if (entry.getByteValue() != current) {
                    entry.setValue(current);
                    changed[0] = true;
                }
            }

            if (changed[0]) {
                biomeState.markStorageChanged();
            }
        });

        if (changed[0]) {
            chunk.setChanged();
        }
    }

    public LevelDBProvider(Level level, String path) throws IOException {
        synchronized (CACHE) {
            this.storage = CACHE.computeIfAbsent(path, p -> {
                try {
                    return new LevelDBStorage(0, p);
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to create LevelDBStorage instance", e);
                }
            });
            this.storage.incrementRefCount();
        }
        this.path = path;
        this.level = level;

        WorldMetadata metadata = this.storage.getWorldMetadata();
        WorldMetadata.LoadResult levelDatLoad = metadata.getOrLoadLevelDat(this::readLevelDat);
        this.levelDat = levelDatLoad.levelDat();
        this.biomeSnowState = metadata.getBiomeSnowState();

        int dimensionId = getDimensionData().getDimensionId();
        if (dimensionId == Level.DIMENSION_OVERWORLD) {
            this.storage.readWorldClocks();
        }

        boolean dimensionMetadataInitialized = metadata.registerDimension(dimensionId);
        WorldMetadata.RuntimeState runtimeState = metadata.getRuntimeState(dimensionId);
        this.runtimeTime = runtimeState.time();
        this.runtimeCurrentTick = runtimeState.currentTick();
        this.runtimeRaining = runtimeState.raining();
        this.runtimeRainTime = runtimeState.rainTime();
        this.runtimeThundering = runtimeState.thundering();
        this.runtimeThunderTime = runtimeState.thunderTime();
        this.runtimeNoSleepNight = runtimeState.noSleepNight();

        this.storage.loadLimboEntities(getDimensionData());

        if (levelDatLoad.created() || dimensionMetadataInitialized) {
            saveLevelData();
        }

        this.storage.getWorldDynamicProperties();
        this.level.getVillageManager().load(this.storage.readVillages(getDimensionData()));
    }

    @UsedByReflection
    public static void generate(String path, String name, LevelConfig.GeneratorConfig generatorConfig) throws IOException {
        File dataDir = new File(path + "/db");
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            throw new IOException("Could not create the directory " + dataDir);
        }

        Path levelDatPath = Path.of(path).resolve("level.dat");
        Path levelDatOldPath = Path.of(path).resolve("level.dat_old");
        if (!Files.exists(levelDatPath) && !Files.exists(levelDatOldPath)) {
            LevelDat levelData = LevelDat.builder().randomSeed(generatorConfig.seed()).name(name).lastPlayed(System.currentTimeMillis() / 1000).build();
            writeLevelDat(path, levelData);
        }

        if (!LevelDBLimboEntities.supportsDimension(generatorConfig.dimensionData())) {
            return;
        }

        LevelDBStorage generationStorage;
        synchronized (CACHE) {
            generationStorage = CACHE.get(path);
            if (generationStorage == null) {
                generationStorage = new LevelDBStorage(0, path);
                CACHE.put(path, generationStorage);
            }
            generationStorage.incrementRefCount();
        }

        try {
            generationStorage.initializeGeneratedLimboEntities(generatorConfig.dimensionData());
        } finally {
            generationStorage.close();
        }
    }

    /**
     * Completes storage metadata for a successfully generated LevelDB world.
     */
    public static void completeGeneration(String path, LevelConfig levelConfig) throws IOException {
        Set<Integer> dimensionIds = new HashSet<>();
        for (LevelConfig.GeneratorConfig generatorConfig : levelConfig.generators().values()) {
            dimensionIds.add(generatorConfig.dimensionData().getDimensionId());
        }

        LevelDBStorage generationStorage;
        synchronized (CACHE) {
            generationStorage = CACHE.get(path);
            if (generationStorage == null) {
                generationStorage = new LevelDBStorage(0, path);
                CACHE.put(path, generationStorage);
            }
            generationStorage.incrementRefCount();
        }

        try {
            LevelDBMigrationVersionStore.writeGeneratedWorldVersions(generationStorage, dimensionIds);
        } finally {
            generationStorage.close();
        }
    }

    @UsedByReflection
    public static boolean isValid(String path) {
        boolean hasLevelDat = new File(path, "level.dat").exists() || new File(path, "level.dat_old").exists();
        return hasLevelDat && isValidStorage(path);
    }

    /**
     * Returns whether the world folder contains existing LevelDB storage.
     *
     * @param path world folder
     * @return whether valid LevelDB storage exists
     */
    public static boolean isValidStorage(String path) {
        File dbFolder = new File(path, "db");
        if (!dbFolder.isDirectory()) {
            return false;
        }

        File[] files = dbFolder.listFiles();
        if (files == null) {
            return false;
        }

        for (File file : files) {
            if (file.getName().endsWith(".ldb")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Writes canonical level.dat data for a world folder.
     *
     * @param pathName world folder path
     * @param levelDat level data
     */
    public static void writeLevelDat(String pathName, LevelDat levelDat) {
        writeLevelDat(pathName, levelDat, null, null);
    }

    static void writeLevelDat(String pathName, LevelDat levelDat, NbtMap dimensionRuntimeStates) {
        writeLevelDat(pathName, levelDat, dimensionRuntimeStates, null);
    }

    static void writeLevelDat(String pathName, LevelDat levelDat, NbtMap dimensionRuntimeStates, NbtMap dimensionSpawns) {
        Path path = Path.of(pathName);
        Path levelDatPath = path.resolve("level.dat");
        Path levelDatOldPath = path.resolve("level.dat_old");
        Path tempPath = null;

        try {
            NbtMap worldData = createWorldDataNBT(levelDat, dimensionRuntimeStates, dimensionSpawns);
            ByteArrayOutputStream nbtOutput = new ByteArrayOutputStream();
            try (var nbtOutputStream = NbtUtils.createWriterLE(nbtOutput)) {
                nbtOutputStream.writeTag(worldData);
            }

            byte[] payload = nbtOutput.toByteArray();
            ByteArrayOutputStream fileOutput = new ByteArrayOutputStream(payload.length + 8);
            writeIntLE(fileOutput, LEVEL_DAT_VERSION);
            writeIntLE(fileOutput, payload.length);
            fileOutput.write(payload);
            byte[] levelDatBytes = fileOutput.toByteArray();

            if (Files.exists(levelDatPath) && Arrays.equals(Files.readAllBytes(levelDatPath), levelDatBytes)) {
                levelDat.setRawData(worldData);
                return;
            }

            tempPath = Files.createTempFile(path, "level.dat.", ".new");
            try (FileOutputStream output = new FileOutputStream(tempPath.toFile())) {
                output.write(levelDatBytes);
                output.getFD().sync();
            }

            if (Files.exists(levelDatPath)) {
                Files.copy(levelDatPath, levelDatOldPath, StandardCopyOption.REPLACE_EXISTING);
            }

            try {
                Files.move(tempPath, levelDatPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(tempPath, levelDatPath, StandardCopyOption.REPLACE_EXISTING);
            }

            levelDat.setRawData(worldData);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write level dat: ", e);
        } finally {
            if (tempPath != null) {
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void writeIntLE(ByteArrayOutputStream output, int value) {
        output.write(value & 0xff);
        output.write((value >>> 8) & 0xff);
        output.write((value >>> 16) & 0xff);
        output.write((value >>> 24) & 0xff);
    }

    private static int readIntLE(byte[] input, int offset) {
        return (input[offset] & 0xff)
                | (input[offset + 1] & 0xff) << 8
                | (input[offset + 2] & 0xff) << 16
                | (input[offset + 3] & 0xff) << 24;
    }

    public IChunk loadChunk(long index, int chunkX, int chunkZ, boolean create) {
        IChunk chunk = this.chunks.get(index);
        if (chunk != null) return chunk;

        CompletableFuture<IChunk> loading = new CompletableFuture<>();
        CompletableFuture<IChunk> existingLoad = this.loadingChunks.putIfAbsent(index, loading);

        if (existingLoad != null) {
            chunk = existingLoad.join();
            if (chunk == null && create) return this.loadChunk(index, chunkX, chunkZ, true);
            return chunk;
        }

        try {
            chunk = this.chunks.get(index);

            if (chunk == null) {
                try {
                    chunk = storage.readChunk(chunkX, chunkZ, this);
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to load chunk", e);
                }
            }

            if (chunk == null) {
                if (create) {
                    chunk = getOrPutChunk(index, this.getEmptyChunk(chunkX, chunkZ));
                }
            } else {
                chunk = getOrPutChunk(index, chunk);

                Level level = this.getLevel();
                restoreBlockTicks(level, chunk);
            }

            loading.complete(chunk);
            this.loadingChunks.remove(index, loading);

            return chunk;
        } catch (RuntimeException | Error e) {
            loading.completeExceptionally(e);
            this.loadingChunks.remove(index, loading);
            throw e;
        }
    }

    public Map<Long, List<LevelDBChunkSerializer.ScheduledTickInfo>> getScheduledTicksMap() {
        return scheduledTicksMap;
    }

    /**
     * Returns the pending random block ticks loaded from LevelDB.
     *
     * @return random ticks indexed by chunk hash
     */
    public Map<Long, LevelDBChunkSerializer.RandomTickData> getRandomTicksMap() {
        return randomTicksMap;
    }

    public Map<Long, List<LevelDBChunkSerializer.NormalTickInfo>> getNormalTicksMap() {
        return normalTicksMap;
    }

    public void restoreBlockTicks(Level level, IChunk chunk) {
        long chunkKey = Level.chunkHash(chunk.getX(), chunk.getZ());
        List<LevelDBChunkSerializer.ScheduledTickInfo> scheduledList = this.scheduledTicksMap.remove(chunkKey);
        LevelDBChunkSerializer.RandomTickData randomTickData = this.randomTicksMap.remove(chunkKey);
        List<LevelDBChunkSerializer.NormalTickInfo> normalList = this.normalTicksMap.remove(chunkKey);

        restoreScheduledTicks(level, chunk, scheduledList);
        restoreRandomTicks(level, chunk, randomTickData);
        restoreNormalTicks(level, normalList);
    }

    private static void restoreRandomTicks(Level level, IChunk chunk, LevelDBChunkSerializer.RandomTickData randomTickData) {
        if (randomTickData == null) return;

        chunk.getRandomBlockUpdateScheduler().setLastTick(
                randomTickData.currentTick
        );

        for (LevelDBChunkSerializer.RandomTickInfo info : randomTickData.ticks) {
            Block block = level.getBlock(info.x, info.y, info.z, 0);
            CompoundTag currentBlockState = CompoundTag.fromNetwork(block.getBlockState().getBlockStateTag());

            if (!currentBlockState.equals(info.blockState)) continue;

            chunk.getRandomBlockUpdateScheduler().add(
                    new BlockUpdateEntry(
                            new Vector3(info.x, info.y, info.z),
                            block, Math.max(info.time, randomTickData.currentTick + 1), 0, true));
        }
    }

    private static void restoreScheduledTicks(Level level, IChunk chunk, List<LevelDBChunkSerializer.ScheduledTickInfo> scheduledList) {
        if (scheduledList == null || scheduledList.isEmpty()) return;

        for (LevelDBChunkSerializer.ScheduledTickInfo info : scheduledList) {
            BlockState blockState = resolveScheduledBlockState(info.blockState);
            if (blockState == null) continue;

            Block block = Block.get(blockState, level, info.x, info.y, info.z, 0);
            chunk.getBlockUpdateScheduler().add(new BlockUpdateEntry(
                    new Vector3(info.x, info.y, info.z),
                    block,
                    level.getCurrentTick() + Math.max(info.delay, 1),
                    0,
                    true
            ));
        }
    }

    @Nullable
    private static BlockState resolveScheduledBlockState(CompoundTag tag) {
        BlockState blockState = ItemHelper.getBlockStateHelper(tag);
        if (blockState != null || !tag.contains("version")) {
            return blockState;
        }

        NbtMap updated = BlockStateUpdaters.updateBlockState(tag.toNetwork(), tag.getInt("version"));
        return ItemHelper.getBlockStateHelper(updated);
    }

    private static void restoreNormalTicks(Level level, List<LevelDBChunkSerializer.NormalTickInfo> normalList) {
        if (normalList == null || normalList.isEmpty()) return;

        for (LevelDBChunkSerializer.NormalTickInfo info : normalList) {
            Block block = level.getBlock(info.x, info.y, info.z, info.layer);

            if (!block.getId().equals(info.id)) continue;

            BlockFace neighbor = info.neighbor >= 0 ? BlockFace.fromIndex(info.neighbor) : null;
            level.getNormalUpdateQueue().add(new Level.QueuedUpdate(block, neighbor));
        }
    }

    public int size() {
        return this.chunks.size();
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
    public Map<Long, IChunk> getLoadedChunks() {
        return Collections.unmodifiableMap(chunks);
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public boolean isChunkLoaded(int X, int Z) {
        return isChunkLoaded(Level.chunkHash(X, Z));
    }

    public IChunk getOrPutChunk(long index, IChunk chunk) {
        IChunk existing = this.chunks.putIfAbsent(index, chunk);
        return existing != null ? existing : chunk;
    }

    public void putChunk(long index, IChunk chunk) {
        if (this.chunks.containsKey(index)) {
            level.getPlayers().values().forEach(player -> {
                synchronized (player.getPlayerChunkManager()) {
                    player.getPlayerChunkManager().getUsedChunks().remove(index);
                }
            });
        }
        chunks.put(index, chunk);
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        return this.chunks.containsKey(hash);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, IChunk chunk) {
        chunk.setPosition(chunkX, chunkZ);
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index) && !Objects.equals(this.chunks.get(index), chunk)) {
            this.unloadChunk(chunkX, chunkZ, false);
        }
        this.lastChunk.remove();//remove cache
        putChunk(index, chunk);
    }

    @Override
    public DimensionData getDimensionData() {
        return level.getDimensionData();
    }

    @Override
    public Pair<ByteBuf, Integer> requestChunkData(int x, int z) {
        IChunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Set");
        }
        AtomicReference<ByteBuf> data = new AtomicReference<>();
        AtomicReference<Integer> subChunkCountRef = new AtomicReference<>();
        chunk.batchProcess(unsafeChunk -> {
            final var byteBuf = PooledByteBufAllocator.DEFAULT.ioBuffer();
            boolean success = false;
            try {
                final ChunkSection[] sections = unsafeChunk.getSections();
                int subChunkCount = unsafeChunk.getDimensionData().getChunkSectionCount();
                while (subChunkCount-- != 0) {
                    if (sections[subChunkCount] != null) {
                        break;
                    }
                }
                int total = subChunkCount + 1;
                final int minSectionY = unsafeChunk.getDimensionData().getMinSectionY();
                //write block
                if (level != null && level.isAntiXrayEnabled()) {
                    for (int i = 0; i < total; i++) {
                        if (sections[i] == null) sections[i] = new ChunkSection((byte) (i + minSectionY), unsafeChunk.getBiomeSections()[i]);
                        sections[i].writeObfuscatedToBuf(level, byteBuf);
                    }
                } else {
                    for (int i = 0; i < total; i++) {
                        final ChunkSection section = sections[i];
                        if (section != null) {
                            section.writeToBuf(byteBuf);
                        } else {
                            byteBuf.writeBytes(emptySectionPayload(i + minSectionY));
                        }
                    }
                }

                // Write biomes
                final var biomeSections = unsafeChunk.getBiomeSections();
                for (int i = 0; i < total; i++) {
                    biomeSections[i].writeToNetwork(byteBuf, Integer::intValue);
                }

                writeBorderBlockData(byteBuf, unsafeChunk);

                // Block entities
                final List<CompoundTag> tagList = new ObjectArrayList<>();
                for (BlockEntity blockEntity : unsafeChunk.getBlockEntities().values()) {
                    if (blockEntity instanceof BlockEntitySpawnable blockEntitySpawnable) {
                        if (blockEntity instanceof BlockEntityMobSpawner spawner && !spawner.hasSpawnEntityType()) continue;
                        tagList.add(blockEntitySpawnable.getSpawnCompound());
                        //Adding NBT to a chunk pack does not show some block entities, and you have to send block entity packets to the player
                        level.addChunkPacket(blockEntitySpawnable.getChunkX(), blockEntitySpawnable.getChunkZ(), blockEntitySpawnable.getSpawnPacket());
                    }
                }
                try (ByteBufOutputStream stream = new ByteBufOutputStream(byteBuf); final NBTOutputStream outputStream = NbtUtils.createNetworkWriter(stream)) {
                    if (tagList.isEmpty()) {
                        stream.writeByte(0);
                    } else {
                        for (CompoundTag nbtMap : tagList) {
                            outputStream.writeTag(nbtMap.toNetwork());
                        }
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
                data.set(byteBuf);
                subChunkCountRef.set(total);
                success = true;
            } finally {
                // only release on early bail-out
                if (!success) {
                    byteBuf.release();
                }
            }
        });
        return Pair.of(data.get(), subChunkCountRef.get());
    }

    @Override
    public LevelProvider.SubChunkRequestData requestSubChunkModeData(int x, int z) {
        IChunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Set");
        }

        AtomicReference<ByteBuf> biomeDataRef = new AtomicReference<>();
        AtomicReference<ByteBuf> borderBlockDataRef = new AtomicReference<>();
        AtomicReference<Integer> requestLimitRef = new AtomicReference<>(0);

        chunk.batchProcess(unsafeChunk -> {
            final ByteBuf biomeData = PooledByteBufAllocator.DEFAULT.ioBuffer();
            final ByteBuf borderBlockData = PooledByteBufAllocator.DEFAULT.ioBuffer();
            boolean success = false;

            try {
                final ChunkSection[] sections = unsafeChunk.getSections();
                int requestLimit = 0;

                for (int i = sections.length - 1; i >= 0; i--) {
                    final ChunkSection section = sections[i];

                    if (section == null) continue;

                    if (!section.blockLayer()[0].isEmpty() || !section.blockLayer()[1].isEmpty()) {
                        requestLimit = i + 1;
                        break;
                    }
                }

                final var biomeSections = unsafeChunk.getBiomeSections();

                for (var biomeSection : biomeSections) {
                    biomeSection.writeToNetwork(
                        biomeData,
                        Integer::intValue
                    );
                }

                writeBorderBlockData(borderBlockData, unsafeChunk);

                biomeDataRef.set(biomeData);
                borderBlockDataRef.set(borderBlockData);
                requestLimitRef.set(requestLimit);

                success = true;
            } finally {
                if (!success) {
                    biomeData.release();
                    borderBlockData.release();
                }
            }
        });

        return new LevelProvider.SubChunkRequestData(
            biomeDataRef.get(),
            borderBlockDataRef.get(),
            requestLimitRef.get()
        );
    }

    /**
     * The serialised form of a section the chunk does not have.
     * <p>
     * A chunk send used to fill each empty slot with a real {@link ChunkSection} and store it
     * back into the chunk, so every chunk ever sent to a player permanently held block palettes,
     * a biome palette and two light arrays for each of its empty sections. A heap dump of a busy
     * server showed 23.8 of a possible 24 sections materialised per chunk, the resulting
     * {@code int[]} and {@code byte[]} being most of the heap.
     * <p>
     * An empty section always serialises to the same bytes for a given section index, so they are
     * produced once from a real {@link ChunkSection} - rather than by hand, which would duplicate
     * the wire format - and reused. That removes both the retention and the per-send allocation.
     * <p>
     * Anti-xray does not use this: obfuscation keeps per-section state and rewrites the palette,
     * so that path still materialises absent sections exactly as it always has.
     *
     * @param sectionY the section's Y, as written into the payload
     * @return bytes to append, which the caller must not modify
     */
    private static byte[] emptySectionPayload(int sectionY) {
        final byte y = (byte) sectionY;
        final int slot = y - Byte.MIN_VALUE;
        byte[] payload = EMPTY_SECTION_PAYLOADS.get(slot);
        if (payload == null) {
            final ByteBuf scratch = Unpooled.buffer();
            try {
                new ChunkSection(y).writeToBuf(scratch);
                payload = ByteBufUtil.getBytes(scratch);
            } finally {
                scratch.release();
            }
            EMPTY_SECTION_PAYLOADS.compareAndSet(slot, null, payload);
            payload = EMPTY_SECTION_PAYLOADS.get(slot);
        }
        return payload;
    }

    private void writeBorderBlockData(ByteBuf byteBuf, UnsafeChunk chunk) {
        int countIndex = byteBuf.writerIndex();
        byteBuf.writeByte(0);

        int count = 0;

        outer:
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                if (!chunk.hasBorderBlock(localX, localZ)) continue;
                if (count >= 255) break outer;

                // Unlike LevelDB 0x38, the network format stores Z in the high nibble and X in the low nibble.
                byteBuf.writeByte((localZ << 4) | localX);
                count++;
            }
        }

        byteBuf.setByte(countIndex, count);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        return this.levelDat.getName();
    }

    @Override
    public boolean isRaining() {
        return this.runtimeRaining;
    }

    @Override
    public void setRaining(boolean raining) {
        this.runtimeRaining = raining;
    }

    @Override
    public float getRainLevel() {
        return this.levelDat.getRainLevel();
    }

    @Override
    public void setRainLevel(float rainLevel) {
        this.levelDat.setRainLevel(rainLevel);
    }

    @Override
    public int getRainTime() {
        return this.runtimeRainTime;
    }

    @Override
    public void setRainTime(int rainTime) {
        this.runtimeRainTime = rainTime;
    }

    @Override
    public boolean isThundering() {
        return this.runtimeThundering;
    }

    @Override
    public void setThundering(boolean thundering) {
        this.runtimeThundering = thundering;
    }

    @Override
    public float getLightningLevel() {
        return this.levelDat.getLightningLevel();
    }

    @Override
    public void setLightningLevel(float lightningLevel) {
        this.levelDat.setLightningLevel(lightningLevel);
    }

    @Override
    public int getThunderTime() {
        return this.runtimeThunderTime;
    }

    @Override
    public void setThunderTime(int thunderTime) {
        this.runtimeThunderTime = thunderTime;
    }

    @Override
    public int getNoSleepNight() {
        return this.runtimeNoSleepNight;
    }

    @Override
    public void setNoSleepNight(int noSleepNight) {
        this.runtimeNoSleepNight = noSleepNight;
    }

    @Override
    public long getCurrentTick() {
        return this.runtimeCurrentTick;
    }

    @Override
    public void setCurrentTick(long currentTick) {
        this.runtimeCurrentTick = currentTick;
    }

    @Override
    public long getTime() {
        return this.runtimeTime;
    }

    @Override
    public void setTime(long value) {
        this.runtimeTime = value;
    }

    @Override
    public long getSeed() {
        return this.levelDat.getRandomSeed();
    }

    @Override
    public void setSeed(long value) {
        this.levelDat.setRandomSeed(value);
    }

    @Override
    public Vector3 getSpawn() {
        WorldMetadata.SpawnState spawn = this.storage.getWorldMetadata().getSpawnState(getDimensionData().getDimensionId());
        return new BlockVector3(spawn.x(), spawn.y(), spawn.z()).asVector3();
    }

    @Override
    public void setSpawn(Vector3 pos) {
        this.storage.getWorldMetadata().setSpawnState(getDimensionData().getDimensionId(), (int) pos.x, (int) pos.y, (int) pos.z);
    }

    @Override
    public GameRules getGamerules() {
        return this.levelDat.getGameRules();
    }

    @Override
    public void setGameRules(GameRules rules) {
        this.levelDat.setGameRules(rules);
    }

    @Override
    public void saveChunks() {
        saveChunks(this.chunks.values());
    }

    @Override
    public void saveChunks(Collection<IChunk> chunks) {
        List<IChunk> changedChunks = chunks.stream()
                .filter(IChunk::hasChanged)
                .toList();

        if (changedChunks.isEmpty()) {
            return;
        }

        try (WriteBatch batch = storage.createBatch()) {
            WriteBatchHelper helper = new WriteBatchHelper();
            Map<IChunk, Long> biomeStateVersions = new ConcurrentHashMap<>();

            CompletableFuture.runAsync(() -> changedChunks.parallelStream().forEach(chunk -> {
                BiomeState biomeState = chunk.getBiomeState();
                if (biomeState.hasStorageChanges()) {
                    biomeStateVersions.put(chunk, biomeState.getStorageChangeVersion());
                }

                // Clear the dirty flag before serializing so a change made
                // mid-save (e.g. taking an item from a chest) re-marks the chunk
                // dirty and gets persisted on the next save instead of being lost.
                chunk.setChanged(false);
                LevelDBChunkSerializer.INSTANCE.serialize(helper, chunk);
            }), Server.getInstance().getComputeThreadPool()).join();

            helper.write(batch);
            helper.close();

            for (IChunk chunk : changedChunks) {
                storage.writeChunkActorData(batch, chunk);
                storage.writeChunkMetaData(batch, chunk);
            }

            storage.writePendingActorDeletions(batch);
            storage.writeLevelChunkMetaDataDictionary(batch);
            storage.writeBatch(batch);

            for (var entry : biomeStateVersions.entrySet()) {
                entry.getKey().getBiomeState().markStorageSaved(entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveChunk(int X, int Z) {
        IChunk chunk = this.getChunk(X, Z);
        if (chunk != null) {
            try {
                storage.writeChunk(chunk);
            } catch (Exception e) {
                throw new ChunkException("Error saving chunk (" + X + ", " + Z + ")", e);
            }
        }
    }

    @Override
    public void saveChunk(int X, int Z, IChunk chunk) {
        chunk.setX(X);
        chunk.setZ(Z);
        try {
            storage.writeChunk(chunk);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LevelDat getLevelData() {
        return this.levelDat;
    }

    @Override
    public void saveLevelData() {
        flushWorldDynamicProperties();
        storage.writeVillages(getDimensionData(), level.getVillageManager().getVillages());
        storage.saveLimboEntities(getDimensionData());
        this.levelDat.setDifficulty(level.getServer().getDifficulty());

        int dimensionId = getDimensionData().getDimensionId();
        if (dimensionId == Level.DIMENSION_OVERWORLD) {
            storage.writeWorldClocks((int) this.runtimeTime);
        }

        storage.getWorldMetadata().saveLevelDat(
                dimensionId,
                this.runtimeTime,
                this.runtimeCurrentTick,
                this.runtimeRaining,
                this.runtimeRainTime,
                this.runtimeThundering,
                this.runtimeThunderTime,
                this.runtimeNoSleepNight
        );
    }

    @Override
    public void updateLevelName(String name) {
        if (!storage.getWorldMetadata().isCanonicalAuthority(getDimensionData().getDimensionId())) {
            return;
        }
        if (!this.getName().equals(name)) {
            this.levelDat.setName(name);
        }
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ) {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index)) {
            return true;
        }
        return loadChunk(index, chunkX, chunkZ, create) != null;
    }

    @Override
    public boolean unloadChunk(int X, int Z) {
        return this.unloadChunk(X, Z, true);
    }

    @Override
    public boolean unloadChunk(int X, int Z, boolean safe) {
        long index = Level.chunkHash(X, Z);
        IChunk chunk = this.chunks.get(index);
        if (chunk != null && chunk.unload(false, safe)) {
            lastChunk.remove();
            this.chunks.remove(index, chunk);
            return true;
        }
        return false;
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Nullable
    protected final IChunk getThreadLastChunk() {
        var ref = lastChunk.get();
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    @Override
    public IChunk getLoadedChunk(int chunkX, int chunkZ) {
        var tmp = getThreadLastChunk();
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        lastChunk.set(new WeakReference<>(tmp = chunks.get(index)));
        return tmp;
    }

    @Override
    public IChunk getLoadedChunk(long hash) {
        var tmp = getThreadLastChunk();
        if (tmp != null && tmp.getIndex() == hash) {
            return tmp;
        }
        lastChunk.set(new WeakReference<>(tmp = chunks.get(hash)));
        return tmp;
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ, boolean create) {
        var tmp = getLoadedChunk(chunkX, chunkZ);
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        lastChunk.set(new WeakReference<>(tmp = chunks.get(index)));
        if (tmp == null) {
            tmp = this.loadChunk(index, chunkX, chunkZ, create);
            lastChunk.set(new WeakReference<>(tmp));
        }
        return tmp;
    }

    @Override
    public IChunk getEmptyChunk(int x, int z) {
        return Chunk.builder().levelProvider(this).emptyChunk(x, z);
    }

    @Override
    public boolean isChunkPopulated(int chunkX, int chunkZ) {
        IChunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.getFinalizationState() == ChunkFinalizationState.DONE;
    }

    @Override
    public void close() {
        flushWorldDynamicProperties();
        storage.getWorldMetadata().unregisterDimension(getDimensionData().getDimensionId());
        storage.close();
    }

    @Override
    public boolean isChunkGenerated(int chunkX, int chunkZ) {
        IChunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING;
    }

    public CompoundTag getWorldDynamicProperties() {
        return this.storage.getWorldDynamicProperties();
    }

    public void setWorldDynamicProperties(CompoundTag tag) {
        this.storage.setWorldDynamicProperties(tag);
    }

    public boolean isWorldDynamicPropertiesDirty() {
        return this.storage.isWorldDynamicPropertiesDirty();
    }

    public void setWorldDynamicPropertiesDirty(boolean dirty) {
        this.storage.setWorldDynamicPropertiesDirty(dirty);
    }

    private void flushWorldDynamicProperties() {
        this.storage.flushWorldDynamicProperties();
    }

    public synchronized LevelDat readLevelDat() throws IOException {
        NbtMap d = readLevelDatNbt();
        if (d == null) return null;

            NbtMap abilities = getCompound(d, "abilities");
            NbtMap experiments = getCompound(d, "experiments");
            GameRules gameRules = readGameRules(d);

            Map<String, Boolean> experimentMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : experiments.entrySet()) {
                Object tag = entry.getValue();
                if (tag instanceof Byte byteTag) {
                    experimentMap.put(entry.getKey(), byteTag != 0);
                }
            }
            LevelDat.Experiments experimentData = new LevelDat.Experiments(experimentMap);

            LevelDat.LevelDatBuilder levelDatBuilder = LevelDat.builder()
                    .biomeOverride(d.getString("BiomeOverride"))
                    .centerMapsToOrigin(this.getBoolean(d, "CenterMapsToOrigin"))
                    .confirmedPlatformLockedContent(this.getBoolean(d, "ConfirmedPlatformLockedContent"))
                    .difficulty(d.getInt("Difficulty"))
                    .flatWorldLayers(d.getString("FlatWorldLayers"))
                    .forceGameType(this.getBoolean(d, "ForceGameType"))
                    .gameType(GameType.from(d.getInt("GameType")))
                    .generator(d.getInt("Generator"))
                    .inventoryVersion(d.getString("InventoryVersion"))
                    .LANBroadcast(this.getBoolean(d, "LANBroadcast"))
                    .LANBroadcastIntent(this.getBoolean(d, "LANBroadcastIntent"))
                    .lastPlayed(d.getLong("LastPlayed"))
                    .name(d.getString("LevelName"))
                    .limitedWorldOriginPoint(new BlockVector3(d.getInt("LimitedWorldOriginX"), d.getInt("LimitedWorldOriginY"), d.getInt("LimitedWorldOriginZ")))
                    .minimumCompatibleClientVersion(SemVersion.from(d.getList("MinimumCompatibleClientVersion", NbtType.INT)))
                    .multiplayerGame(this.getBoolean(d, "MultiplayerGame"))
                    .multiplayerGameIntent(this.getBoolean(d, "MultiplayerGameIntent"))
                    .netherScale(d.getInt("NetherScale"))
                    .networkVersion(d.getInt("NetworkVersion"))
                    .platform(d.getInt("Platform"))
                    .platformBroadcastIntent(d.getInt("PlatformBroadcastIntent"))
                    .randomSeed(d.getLong("RandomSeed"))
                    .spawnV1Villagers(this.getBoolean(d, "SpawnV1Villagers"))
                    .spawnPoint(new BlockVector3(d.getInt("SpawnX"), d.getInt("SpawnY"), d.getInt("SpawnZ")))
                    .storageVersion(d.getInt("StorageVersion"))
                    .time(d.getLong("Time"))
                    .worldVersion(d.getInt("WorldVersion"))
                    .XBLBroadcastIntent(d.getInt("XBLBroadcastIntent"))
                    .gameRules(gameRules)
                    .abilities(LevelDat.Abilities.builder()
                            .attackMobs(abilities.getBoolean("attackmobs"))
                            .attackPlayers(abilities.getBoolean("attackplayers"))
                            .build(abilities.getBoolean("build"))
                            .doorsAndSwitches(abilities.getBoolean("doorsandswitches"))
                            .flySpeed(abilities.getFloat("flySpeed"))
                            .flying(abilities.getBoolean("flying"))
                            .instaBuild(abilities.getBoolean("instabuild"))
                            .invulnerable(abilities.getBoolean("invulnerable"))
                            .lightning(abilities.getBoolean("lightning"))
                            .mayFly(abilities.getBoolean("mayfly"))
                            .mine(abilities.getBoolean("mine"))
                            .op(abilities.getBoolean("op"))
                            .openContainers(abilities.getBoolean("opencontainers"))
                            .teleport(abilities.getBoolean("teleport"))
                            .walkSpeed(abilities.getFloat("walkSpeed"))
                            .build())
                    .baseGameVersion(d.getString("baseGameVersion"))
                    .bonusChestEnabled(this.getBoolean(d, "bonusChestEnabled"))
                    .bonusChestSpawned(this.getBoolean(d, "bonusChestSpawned"))
                    .cheatsEnabled(this.getBoolean(d, "cheatsEnabled"))
                    .commandsEnabled(this.getBoolean(d, "commandsEnabled"))
                    .currentTick(d.getLong("currentTick"))
                    .daylightCycle(d.getInt("daylightCycle"))
                    .editorWorldType(d.getInt("editorWorldType"))
                    .eduOffer(d.getInt("eduOffer"))
                    .educationFeaturesEnabled(this.getBoolean(d, "educationFeaturesEnabled"))
                    .experiments(experimentData)
                    .hasBeenLoadedInCreative(this.getBoolean(d, "hasBeenLoadedInCreative"))
                    .hasLockedBehaviorPack(this.getBoolean(d, "hasLockedBehaviorPack"))
                    .hasLockedResourcePack(this.getBoolean(d, "hasLockedResourcePack"))
                    .immutableWorld(this.getBoolean(d, "immutableWorld"))
                    .isCreatedInEditor(this.getBoolean(d, "isCreatedInEditor"))
                    .isExportedFromEditor(this.getBoolean(d, "isExportedFromEditor"))
                    .isFromLockedTemplate(this.getBoolean(d, "isFromLockedTemplate"))
                    .isFromWorldTemplate(this.getBoolean(d, "isFromWorldTemplate"))
                    .isRandomSeedAllowed(this.getBoolean(d, "isRandomSeedAllowed"))
                    .isSingleUseWorld(this.getBoolean(d, "isSingleUseWorld"))
                    .isWorldTemplateOptionLocked(this.getBoolean(d, "isWorldTemplateOptionLocked"))
                    .lastOpenedWithVersion(SemVersion.from(d.getList("lastOpenedWithVersion", NbtType.INT)))
                    .lightningLevel(d.getFloat("lightningLevel"))
                    .lightningTime(d.getInt("lightningTime"))
                    .limitedWorldDepth(d.getInt("limitedWorldDepth"))
                    .limitedWorldWidth(d.getInt("limitedWorldWidth"))
                    .permissionsLevel(d.getInt("permissionsLevel"))
                    .playerPermissionsLevel(d.getInt("playerPermissionsLevel"))
                    .playersSleepingPercentage(d.getInt("playersSleepingPercentage"))
                    .prid(d.getString("prid"))
                    .rainLevel(d.getFloat("rainLevel"))
                    .rainTime(d.getInt("rainTime"))
                    .randomTickSpeed(d.getInt("randomTickSpeed"))
                    .recipesUnlock(this.getBoolean(d, "recipesUnlock"))
                    .requiresCopiedPackRemovalCheck(this.getBoolean(d, "requiresCopiedPackRemovalCheck"))
                    .serverChunkTickRange(d.getInt("serverChunkTickRange"))
                    .spawnMobs(this.getBoolean(d, "spawnMobs"))
                    .startWithMapEnabled(this.getBoolean(d, "startWithMapEnabled"))
                    .texturePacksRequired(this.getBoolean(d, "texturePacksRequired"))
                    .useMsaGamertagsOnly(this.getBoolean(d, "useMsaGamertagsOnly"))
                    .worldStartCount(d.getLong("worldStartCount"))
                    .worldPolicies(LevelDat.WorldPolicies.builder().build());
            if (d.containsKey("raining")) {
                levelDatBuilder.raining(this.getBoolean(d, "raining"));//PNX Custom field
            }
            if (d.containsKey("thundering")) {
                levelDatBuilder.thundering(this.getBoolean(d, "thundering"));//PNX Custom field
            }
            if (d.containsKey("nosleepnights")) {
                levelDatBuilder.noSleepNight(d.getInt("nosleepnights"));//PNX Custom field
            }
            return levelDatBuilder.rawData(d).build();
    }

    private NbtMap readLevelDatNbt() throws IOException {
        Path levelDatPath = Path.of(path).resolve("level.dat");
        Path levelDatOldPath = Path.of(path).resolve("level.dat_old");
        IOException levelDatError = null;

        if (Files.exists(levelDatPath)) {
            try {
                return readLevelDatNbt(levelDatPath);
            } catch (Exception e) {
                levelDatError = toLevelDatIOException(levelDatPath, e);
            }
        }

        if (Files.exists(levelDatOldPath)) {
            try {
                NbtMap recovered = readLevelDatNbt(levelDatOldPath);
                try {
                    Files.copy(levelDatOldPath, levelDatPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.warn("Loaded level.dat_old but could not restore level.dat: {}", e.getMessage());
                }

                if (levelDatError == null) {
                    log.warn("level.dat missing, level.dat_old used instead");
                } else {
                    log.warn("level.dat corrupted/unreadable, level.dat_old used instead");
                }
                return recovered;
            } catch (Exception e) {
                IOException levelDatOldError = toLevelDatIOException(levelDatOldPath, e);
                if (levelDatError != null) {
                    levelDatOldError.addSuppressed(levelDatError);
                }
                throw levelDatOldError;
            }
        }

        if (levelDatError != null) {
            throw levelDatError;
        }
        return null;
    }

    private static NbtMap readLevelDatNbt(Path file) throws IOException {
        byte[] data = Files.readAllBytes(file);
        if (data.length < 8) {
            throw new IOException("level.dat is smaller than its 8-byte header");
        }

        int version = readIntLE(data, 0);
        int payloadLength = readIntLE(data, 4);
        if (version <= 0) {
            throw new IOException("Invalid level.dat version " + version);
        }
        if (payloadLength < 0 || payloadLength != data.length - 8) {
            throw new IOException("Invalid level.dat payload length " + payloadLength + ", expected " + (data.length - 8));
        }

        try (var input = new ByteArrayInputStream(data, 8, payloadLength);
             var nbtInputStream = NbtUtils.createReaderLE(input)) {
            Object tag = nbtInputStream.readTag();
            if (!(tag instanceof NbtMap levelData)) {
                throw new IOException("level.dat root tag is not a compound");
            }
            return levelData;
        }
    }

    private static IOException toLevelDatIOException(Path file, Exception cause) {
        return cause instanceof IOException ioException ? ioException : new IOException("Failed to read " + file.getFileName(), cause);
    }

    private static NbtMap createWorldDataNBT(LevelDat worldData, NbtMap dimensionRuntimeStates, NbtMap dimensionSpawns) {
        NbtMap rawData = worldData.getRawData();
        NbtMapBuilder levelDat = copyLevelDatNbt(rawData);
        if (dimensionRuntimeStates != null) {
            levelDat.put(WorldMetadata.DIMENSION_RUNTIME_STATES_TAG, dimensionRuntimeStates);
        }
        if (dimensionSpawns != null) {
            levelDat.put(WorldMetadata.DIMENSION_SPAWNS_TAG, dimensionSpawns);
        }
        levelDat.putString("BiomeOverride", worldData.getBiomeOverride());
        levelDat.putBoolean("CenterMapsToOrigin", worldData.isCenterMapsToOrigin());
        levelDat.putBoolean("ConfirmedPlatformLockedContent", worldData.isConfirmedPlatformLockedContent());
        levelDat.putInt("Difficulty", worldData.getDifficulty());
        levelDat.putString("FlatWorldLayers", worldData.getFlatWorldLayers());
        levelDat.putBoolean("ForceGameType", worldData.isForceGameType());
        levelDat.putInt("GameType", worldData.getGameType().ordinal());
        levelDat.putInt("Generator", worldData.getGenerator());
        levelDat.putString("InventoryVersion", worldData.getInventoryVersion());
        levelDat.putBoolean("LANBroadcast", worldData.isLANBroadcast());
        levelDat.putBoolean("LANBroadcastIntent", worldData.isLANBroadcastIntent());
        levelDat.putLong("LastPlayed", worldData.getLastPlayed());
        levelDat.putString("LevelName", worldData.getName());
        levelDat.putInt("LimitedWorldOriginX", worldData.getLimitedWorldOriginPoint().getX());
        levelDat.putInt("LimitedWorldOriginY", worldData.getLimitedWorldOriginPoint().getY());
        levelDat.putInt("LimitedWorldOriginZ", worldData.getLimitedWorldOriginPoint().getZ());
        levelDat.putList("MinimumCompatibleClientVersion", NbtType.INT, worldData.getMinimumCompatibleClientVersion().toTag());
        levelDat.putList("lastOpenedWithVersion", NbtType.INT, worldData.getLastOpenedWithVersion().toTag());
        levelDat.putBoolean("MultiplayerGame", worldData.isMultiplayerGame());
        levelDat.putBoolean("MultiplayerGameIntent", worldData.isMultiplayerGameIntent());
        levelDat.putInt("NetherScale", worldData.getNetherScale());
        levelDat.putInt("NetworkVersion", worldData.getNetworkVersion());
        levelDat.putInt("Platform", worldData.getPlatform());
        levelDat.putInt("PlatformBroadcastIntent", worldData.getPlatformBroadcastIntent());
        levelDat.putLong("RandomSeed", worldData.getRandomSeed());
        levelDat.putBoolean("SpawnV1Villagers", worldData.isSpawnV1Villagers());
        levelDat.putInt("SpawnX", worldData.getSpawnPoint().getX());
        levelDat.putInt("SpawnY", worldData.getSpawnPoint().getY());
        levelDat.putInt("SpawnZ", worldData.getSpawnPoint().getZ());
        levelDat.putInt("StorageVersion", worldData.getStorageVersion());
        levelDat.putLong("Time", worldData.getTime());
        levelDat.putInt("WorldVersion", worldData.getWorldVersion());
        levelDat.putLong(
                "worldStartCount",
                worldData.getWorldStartCount()
        );
        levelDat.putInt("XBLBroadcastIntent", worldData.getXBLBroadcastIntent());
        NbtMapBuilder abilities = copyNbt(getCompound(rawData, "abilities"));
        abilities.putBoolean("attackmobs", worldData.getAbilities().isAttackMobs());
        abilities.putBoolean("attackplayers", worldData.getAbilities().isAttackPlayers());
        abilities.putBoolean("build", worldData.getAbilities().isBuild());
        abilities.putBoolean("doorsandswitches", worldData.getAbilities().isDoorsAndSwitches());
        abilities.putBoolean("flying", worldData.getAbilities().isFlying());
        abilities.putBoolean("instabuild", worldData.getAbilities().isInstaBuild());
        abilities.putBoolean("invulnerable", worldData.getAbilities().isInvulnerable());
        abilities.putBoolean("lightning", worldData.getAbilities().isLightning());
        abilities.putBoolean("mayfly", worldData.getAbilities().isMayFly());
        abilities.putBoolean("mine", worldData.getAbilities().isMine());
        abilities.putBoolean("op", worldData.getAbilities().isOp());
        abilities.putBoolean("opencontainers", worldData.getAbilities().isOpenContainers());
        abilities.putBoolean("teleport", worldData.getAbilities().isTeleport());
        abilities.putFloat("flySpeed", worldData.getAbilities().getFlySpeed());
        abilities.putFloat("walkSpeed", worldData.getAbilities().getWalkSpeed());

        NbtMapBuilder experiments = copyNbt(getCompound(rawData, "experiments"));
        for (Map.Entry<String, Boolean> entry : worldData.getExperiments().getEntries().entrySet()) {
            experiments.putBoolean(entry.getKey(), entry.getValue());
        }
        boolean hasExperiments = worldData.getExperiments().getEntries().values().stream().anyMatch(Boolean::booleanValue);
        if (hasExperiments) {
            experiments.putBoolean("experiments_ever_used", true);
            experiments.putBoolean("saved_with_toggled_experiments", true);
        }

        levelDat.put("abilities", abilities.build());
        levelDat.put("experiments", experiments.build());

        levelDat.putBoolean("bonusChestEnabled", worldData.isBonusChestEnabled());
        levelDat.putBoolean("bonusChestSpawned", worldData.isBonusChestSpawned());
        levelDat.putBoolean("cheatsEnabled", worldData.isCheatsEnabled());
        levelDat.putBoolean("commandsEnabled", worldData.isCommandsEnabled());
        levelDat.putLong("currentTick", worldData.getCurrentTick());
        levelDat.putInt("daylightCycle", worldData.getDaylightCycle());
        levelDat.putInt("editorWorldType", worldData.getEditorWorldType());
        levelDat.putInt("eduOffer", worldData.getEduOffer());
        levelDat.putBoolean("educationFeaturesEnabled", worldData.isEducationFeaturesEnabled());
        levelDat.putFloat("lightningLevel", worldData.getLightningLevel());
        levelDat.putInt("lightningTime", worldData.getLightningTime());
        levelDat.putFloat("rainLevel", worldData.getRainLevel());
        levelDat.putInt("rainTime", worldData.getRainTime());

        Map<GameRule, GameRules.Value<?>> gameRules = worldData.getGameRules().getGameRules();
        for (GameRule rule : GameRule.values()) {
            if (rule.isDeprecated()) continue;
            GameRules.Value<?> value = gameRules.get(rule);
            if (value != null) {
                levelDat.put(rule.getName().toLowerCase(Locale.ROOT), value.getTag());
            }
        }

        //PNX Custom field
        levelDat.putBoolean("raining", worldData.isRaining());
        levelDat.putBoolean("thundering", worldData.isThundering());
        levelDat.putInt("nosleepnights", worldData.getNoSleepNight());
        return levelDat.build();
    }

    private static NbtMapBuilder copyLevelDatNbt(NbtMap source) {
        NbtMapBuilder builder = NbtMap.builder();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            if (!isManagedGameRuleKey(entry.getKey())) builder.put(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    private static NbtMapBuilder copyNbt(NbtMap source) {
        NbtMapBuilder builder = NbtMap.builder();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    private static NbtMap getCompound(NbtMap source, String key) {
        Object value = source.get(key);
        return value instanceof NbtMap map ? map : NbtMap.EMPTY;
    }

    private static boolean isManagedGameRuleKey(String key) {
        for (GameRule rule : GameRule.values()) {
            if (!rule.isDeprecated() && rule.getName().equalsIgnoreCase(key)) return true;
        }
        return false;
    }

    private boolean getBoolean(NbtMap nbtMap, String key) {
        return nbtMap.getByte(key) == (byte) 1;
    }

    private GameRules readGameRules(NbtMap d) {
        GameRules gameRules = GameRules.getDefault();
        for (GameRule rule : GameRule.values()) {
            if (rule.isDeprecated()) continue;
            Object tag = findIgnoreCase(d, rule.getName());
            if (!(tag instanceof Number number)) continue;
            switch (gameRules.getGameRuleType(rule)) {
                case BOOLEAN -> gameRules.setGameRule(rule, number.intValue() != 0);
                case INTEGER -> gameRules.setGameRule(rule, number.intValue());
                case FLOAT -> gameRules.setGameRule(rule, number.floatValue());
            }
        }
        return gameRules;
    }

    private Object findIgnoreCase(NbtMap nbtMap, String key) {
        Object exact = nbtMap.get(key);
        if (exact != null) return exact;
        for (Map.Entry<String, Object> entry : nbtMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) return entry.getValue();
        }
        return null;
    }
}
