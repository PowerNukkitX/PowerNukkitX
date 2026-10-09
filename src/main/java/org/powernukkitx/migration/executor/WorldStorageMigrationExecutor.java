package org.powernukkitx.migration.executor;

import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationService;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.WorldStorageMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.DynamicProperties;
import org.powernukkitx.utils.JSONUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Orchestrates world-global LevelDB storage migrations and commits canonical world metadata.
 *
 * @author Curse
 */
@Slf4j
public final class WorldStorageMigrationExecutor {
    private static final int LEGACY_PNX_LEVEL_DAT_VERSION = 10;
    private static final int LEGACY_PNX_LEVEL_DAT_PAYLOAD_LENGTH = 0x0b44;
    private static final String DIMENSION_RUNTIME_STATES_TAG = "PNXDimensionRuntimeStates";
    private static final String DIMENSION_SPAWNS_TAG = "PNXDimensionSpawns";
    private static final byte[] STORAGE_VERSION_KEY = "\0pnx_migration:world_storage:storage_version".getBytes(StandardCharsets.UTF_8);
    private static final byte[] LEGACY_DYNAMIC_PROPERTIES_KEY = new byte[]{(byte) '~'};
    private static final byte[] DYNAMIC_PROPERTIES_KEY = LevelDBKeyUtil.getGlobalKey(DynamicProperties.ROOT);
    private static final byte[] BIOME_DATA_KEY = LevelDBKeyUtil.getGlobalKey("BiomeData");
    private static final byte[] NETHER_PORTALS_KEY = "portals".getBytes(StandardCharsets.UTF_8);
    private static final byte[] OVERWORLD_DATA_KEY = "Overworld".getBytes(StandardCharsets.UTF_8);
    private static final byte[] NETHER_DATA_KEY = "Nether".getBytes(StandardCharsets.UTF_8);
    private static final byte[] THE_END_DATA_KEY = "TheEnd".getBytes(StandardCharsets.UTF_8);
    private static final String LEGACY_TICKING_AREA_FILE = "tickingarea.json";
    private static final String TICKING_AREA_KEY_PREFIX = "tickingarea_";
    private static final TypeToken<List<LegacyTickingAreaJson>> LEGACY_TICKING_AREA_TYPE = new TypeToken<>() {
    };
    private final LevelDBStorage storage;
    private final Path worldPath;
    private final Set<Integer> expectedDimensions;

    /**
     * Creates a world storage migration executor for one LevelDB world.
     */
    public WorldStorageMigrationExecutor(LevelDBStorage storage, Path worldPath, Set<Integer> expectedDimensions) {
        this.storage = storage;
        this.worldPath = worldPath;
        this.expectedDimensions = Set.copyOf(expectedDimensions);
    }

    /**
     * Migrates legacy PNX level.dat dimension sidecars into the canonical multi-dimension root level.dat.
     */
    public static void migrateLevelDatIfNeeded(String path, Set<Integer> expectedDimensions) throws IOException {
        Path worldPath = Path.of(path);
        Path levelDatPath = worldPath.resolve("level.dat");
        LevelDatMigrationFile root = readLevelDatForMigration(levelDatPath, worldPath.resolve("level.dat_old"));
        if (root == null) return;

        Map<Integer, Path> legacyDimensions = collectLegacyDimensionLevelDat(worldPath);
        NbtMap existingRuntimeStates = getCompound(root.data(), DIMENSION_RUNTIME_STATES_TAG);
        NbtMap existingSpawns = getCompound(root.data(), DIMENSION_SPAWNS_TAG);
        NbtMapBuilder runtimeStates = copyNbt(existingRuntimeStates);
        NbtMapBuilder spawns = copyNbt(existingSpawns);

        for (int dimensionId : expectedDimensions.stream().sorted().toList()) {
            String key = Integer.toString(dimensionId);
            if (!existingRuntimeStates.containsKey(key)) {
                runtimeStates.put(key, createRuntimeState(root.data()));
            }
            if (!existingSpawns.containsKey(key)) {
                spawns.put(key, createSpawnState(root.data()));
            }
        }

        for (int dimensionId : legacyDimensions.keySet().stream().sorted().toList()) {
            Path legacyPath = legacyDimensions.get(dimensionId);
            LevelDatMigrationFile dimension = readLevelDatForMigration(
                    legacyPath,
                    legacyPath.resolveSibling(legacyPath.getFileName() + "_old")
            );
            if (dimension == null) {
                throw new IOException("Missing legacy PNX level.dat for dimension " + dimensionId);
            }

            String key = Integer.toString(dimensionId);
            runtimeStates.put(key, createRuntimeState(dimension.data()));
            spawns.put(key, createSpawnState(dimension.data()));
        }

        NbtMapBuilder migrated = copyNbt(root.data());
        migrated.put(DIMENSION_RUNTIME_STATES_TAG, runtimeStates.build());
        migrated.put(DIMENSION_SPAWNS_TAG, spawns.build());
        NbtMap migratedRoot = migrated.build();

        boolean rootChanged = root.legacyHeader() || !root.source().equals(levelDatPath) || !migratedRoot.equals(root.data());
        if (rootChanged) {
            writeLevelDatForMigration(worldPath, migratedRoot, root.data(), root.version());
        }

        for (Path legacyPath : legacyDimensions.values()) {
            Files.deleteIfExists(legacyPath);
            Files.deleteIfExists(legacyPath.resolveSibling(legacyPath.getFileName() + "_old"));
        }

        if (rootChanged || legacyDimensions.size() != 0) {
            log.info("[LevelDB Migration] Level.dat migration completed ({} legacy dimension sidecars)", legacyDimensions.size());
        }
    }

    /**
     * Returns the generator type stored by the canonical root level.dat.
     */
    public static int readGeneratorType(Path worldPath) throws IOException {
        LevelDatMigrationFile root = readLevelDatForMigration(
                worldPath.resolve("level.dat"),
                worldPath.resolve("level.dat_old")
        );

        if (root == null) {
            throw new IOException("Missing level.dat");
        }

        return getInt(root.data(), "Generator", 1);
    }

    private WorldStorageMigrationData read() throws IOException {
        synchronized (this.storage) {
            DB db = this.storage.getDb();
            CompoundTag legacyDynamicPropertiesRoot = readSingleCompound(
                    db.get(LEGACY_DYNAMIC_PROPERTIES_KEY),
                    "Legacy '~' Dynamic Properties record must contain exactly one NBT root"
            );
            CompoundTag dynamicProperties = null;

            if (legacyDynamicPropertiesRoot != null) {
                dynamicProperties = readSingleCompound(
                        db.get(DYNAMIC_PROPERTIES_KEY),
                        "Canonical Dynamic Properties record must contain exactly one NBT root"
                );
            }

            CompoundTag portals = readSingleCompound(
                    db.get(NETHER_PORTALS_KEY),
                    "Invalid BDS portals record"
            );
            CompoundTag biomeData = readSingleCompound(
                    db.get(BIOME_DATA_KEY),
                    "Invalid BDS BiomeData record"
            );

            List<WorldStorageMigrationData.LegacyTickingArea> legacyTickingAreas = readLegacyTickingAreas();
            Map<UUID, CompoundTag> tickingAreas = legacyTickingAreas == null ? null : this.storage.readTickingAreas();

            return new WorldStorageMigrationData(
                    legacyDynamicPropertiesRoot,
                    dynamicProperties,
                    portals,
                    biomeData,
                    readWorldStartCount(this.worldPath) & 0xffffffffL,
                    legacyTickingAreas,
                    tickingAreas,
                    false,
                    false,
                    false,
                    false
            );
        }
    }

    /**
     * Returns the current persisted world storage migration version.
     */
    public MigrationVersion getCurrentVersion(MigrationService migrationService) throws IOException {
        synchronized (this.storage) {
            byte[] stored = this.storage.getDb().get(STORAGE_VERSION_KEY);
            if (stored == null) return null;

            return migrationService.parseStoredVersion(
                    MigrationFormat.WORLD_STORAGE,
                    new String(stored, StandardCharsets.UTF_8)
            );
        }
    }

    /**
     * Executes pending world storage migrations.
     */
    public void migrate(MigrationService migrationService, MigrationVersion currentVersion) throws IOException {
        if (migrationService.getStepsAfter(MigrationFormat.WORLD_STORAGE, currentVersion).size() == 0) return;

        WorldStorageMigrationData migrated = migrationService.apply(MigrationFormat.WORLD_STORAGE, currentVersion, read());
        commit(migrationService.getLatestVersion(MigrationFormat.WORLD_STORAGE), migrated);
    }

    private void commit(MigrationVersion version, WorldStorageMigrationData value) throws IOException {
        synchronized (this.storage) {
            long currentWorldStartCount = readWorldStartCount(this.worldPath) & 0xffffffffL;
            if (currentWorldStartCount != value.worldStartCount()) {
                writeWorldStartCount(this.worldPath, value.worldStartCount());
            }

            if (value.tickingAreasChanged() && value.tickingAreas() == null) {
                throw new IOException("Missing migrated ticking area data");
            }

            try (WriteBatch batch = this.storage.createBatch()) {
                migrateLimboEntities(batch);

                if (value.dynamicPropertiesChanged()) {
                    batch.put(DYNAMIC_PROPERTIES_KEY, writeLittleEndianCompound(value.dynamicProperties()));
                    batch.delete(LEGACY_DYNAMIC_PROPERTIES_KEY);
                }

                if (value.portalsChanged()) {
                    batch.put(NETHER_PORTALS_KEY, writeLittleEndianCompound(value.portals()));
                }

                if (value.biomeDataChanged()) {
                    if (value.biomeData() == null) {
                        throw new IOException("Missing migrated BiomeData");
                    }
                    batch.put(BIOME_DATA_KEY, writeLittleEndianCompound(value.biomeData()));
                }

                if (value.tickingAreasChanged()) {
                    for (var entry : value.tickingAreas().entrySet()) {
                        batch.put(getTickingAreaKey(entry.getKey()), writeLittleEndianCompound(entry.getValue()));
                    }
                } else {
                    batch.put(STORAGE_VERSION_KEY, version.toString().getBytes(StandardCharsets.UTF_8));
                }

                this.storage.writeBatch(batch);
            }

            if (value.tickingAreasChanged()) {
                Files.deleteIfExists(getLegacyTickingAreaPath());
                try (WriteBatch batch = this.storage.createBatch()) {
                    batch.put(STORAGE_VERSION_KEY, version.toString().getBytes(StandardCharsets.UTF_8));
                    this.storage.writeBatch(batch);
                }
            }

            if (value.dynamicPropertiesChanged()) {
                log.info("[LevelDB Migration] Migrated legacy '~' world Dynamic Properties to canonical 'DynamicProperties'");
            }

            if (value.portalsChanged()) {
                log.info("[LevelDB Migration] Normalized empty PortalRecords to List<Compound>");
            }

            if (value.biomeDataChanged()) {
                log.info("[LevelDB Migration] Initialized canonical BiomeData world snow state");
            }

            if (value.tickingAreasChanged()) {
                int count = value.legacyTickingAreas() == null ? 0 : value.legacyTickingAreas().size();
                log.info("[LevelDB Migration] Migrated {} legacy JSON ticking areas to canonical LevelDB storage", count);
            }
        }
    }

    private void migrateLimboEntities(WriteBatch batch) throws IOException {
        DB db = this.storage.getDb();

        for (int dimensionId : this.expectedDimensions.stream().sorted().toList()) {
            byte[] key = getLimboDimensionKey(dimensionId);
            if (key == null) {
                continue;
            }

            CompoundTag root = readSingleCompound(db.get(key), "Invalid BDS dimension-global data for dimension " + dimensionId);
            boolean changed = false;

            if (root == null) {
                root = new CompoundTag();
                changed = true;
            }

            if (root.contains("data") && !root.containsCompound("data")) {
                throw new IOException("Invalid data compound in dimension-global data for dimension " + dimensionId);
            }

            CompoundTag data = root.containsCompound("data") ? root.getCompound("data").copy() : new CompoundTag();
            if (!root.containsCompound("data")) {
                changed = true;
            }

            if (!isCanonicalLimboEntities(data)) {
                data.putList("LimboEntities", new ListTag<CompoundTag>(Tag.TAG_Compound));
                changed = true;
            }

            if (changed) {
                root.putCompound("data", data);
                batch.put(key, writeLittleEndianCompound(root));
            }
        }
    }

    private static boolean isCanonicalLimboEntities(CompoundTag data) {
        if (!data.containsList("LimboEntities")) {
            return false;
        }

        ListTag<?> buckets = data.getList("LimboEntities");
        if (buckets.type != Tag.TAG_Compound) {
            return false;
        }

        Set<Long> chunks = new HashSet<>();
        for (int i = 0; i < buckets.size(); i++) {
            if (!(buckets.get(i) instanceof CompoundTag bucket)
                    || !bucket.containsInt("ChunkX") || !bucket.containsInt("ChunkZ")
                    || !bucket.containsList("EntityTagList")) {
                return false;
            }

            ListTag<?> entityTags = bucket.getList("EntityTagList");
            if (entityTags.type != Tag.TAG_Compound
                    || !chunks.add(Level.chunkHash(bucket.getInt("ChunkX"), bucket.getInt("ChunkZ")))) {
                return false;
            }
        }

        return true;
    }

    private static byte[] getLimboDimensionKey(int dimensionId) {
        return switch (dimensionId) {
            case Level.DIMENSION_OVERWORLD -> OVERWORLD_DATA_KEY;
            case Level.DIMENSION_NETHER -> NETHER_DATA_KEY;
            case Level.DIMENSION_THE_END -> THE_END_DATA_KEY;
            default -> null;
        };
    }

    private static long readWorldStartCount(Path worldPath) throws IOException {
        LevelDatMigrationFile root = readLevelDatForMigration(
                worldPath.resolve("level.dat"),
                worldPath.resolve("level.dat_old")
        );

        if (root == null) {
            return 0xffffffffL;
        }

        Object value = root.data().get("worldStartCount");
        return value instanceof Number number ? number.longValue() : 0xffffffffL;
    }

    private static void writeWorldStartCount(Path worldPath, long worldStartCount) throws IOException {
        LevelDatMigrationFile root = readLevelDatForMigration(
                worldPath.resolve("level.dat"),
                worldPath.resolve("level.dat_old")
        );

        if (root == null) {
            return;
        }

        NbtMapBuilder migrated = copyNbt(root.data());
        migrated.putLong("worldStartCount", worldStartCount);
        writeLevelDatForMigration(worldPath, migrated.build(), root.data(), root.version());
    }

    private static Map<Integer, Path> collectLegacyDimensionLevelDat(Path worldPath) throws IOException {
        Map<Integer, Path> result = new HashMap<>();
        try (var files = Files.list(worldPath)) {
            for (Path file : files.toList()) {
                String name = file.getFileName().toString();
                if (!name.startsWith("level_Dim") || !name.endsWith(".dat")) continue;

                String dimension = name.substring("level_Dim".length(), name.length() - ".dat".length());
                try {
                    result.put(Integer.parseInt(dimension), file);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return result;
    }

    private static LevelDatMigrationFile readLevelDatForMigration(Path primary, Path fallback) throws IOException {
        IOException primaryError = null;

        if (Files.exists(primary)) {
            try {
                return readLevelDatForMigration(primary);
            } catch (IOException e) {
                primaryError = e;
            }
        }

        if (fallback != null && Files.exists(fallback)) {
            try {
                return readLevelDatForMigration(fallback);
            } catch (IOException e) {
                if (primaryError != null) {
                    e.addSuppressed(primaryError);
                }
                throw e;
            }
        }

        if (primaryError != null) {
            throw primaryError;
        }
        return null;
    }

    private static LevelDatMigrationFile readLevelDatForMigration(Path file) throws IOException {
        byte[] data = Files.readAllBytes(file);
        if (data.length < 8) {
            throw new IOException(file.getFileName() + " is smaller than its 8-byte header");
        }

        int version = readIntLittleEndian(data, 0);
        int payloadLength = readIntLittleEndian(data, 4);
        int actualPayloadLength = data.length - 8;
        boolean legacyHeader = version == LEGACY_PNX_LEVEL_DAT_VERSION
                && payloadLength == LEGACY_PNX_LEVEL_DAT_PAYLOAD_LENGTH
                && payloadLength != actualPayloadLength;

        if (version <= 0) {
            throw new IOException("Invalid " + file.getFileName() + " version " + version);
        }
        if (payloadLength < 0 || (payloadLength != actualPayloadLength && !legacyHeader)) {
            throw new IOException("Invalid " + file.getFileName() + " payload length " + payloadLength + ", expected " + actualPayloadLength);
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data, 8, actualPayloadLength);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            Object tag = nbtInputStream.readTag();
            if (!(tag instanceof NbtMap map)) {
                throw new IOException(file.getFileName() + " root tag is not a compound");
            }
            return new LevelDatMigrationFile(file, map, version, legacyHeader);
        }
    }

    private static NbtMap getCompound(NbtMap root, String key) throws IOException {
        Object value = root.get(key);
        if (value == null) return NbtMap.EMPTY;
        if (!(value instanceof NbtMap map)) {
            throw new IOException("Invalid " + key + " tag in level.dat");
        }
        return map;
    }

    private static NbtMapBuilder copyNbt(NbtMap source) {
        NbtMapBuilder builder = NbtMap.builder();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    private static NbtMap createRuntimeState(NbtMap data) {
        return NbtMap.builder()
                .putLong("Time", getLong(data, "Time", 0))
                .putLong("currentTick", getLong(data, "currentTick", 0))
                .putBoolean("raining", getBoolean(data, "raining", false))
                .putInt("rainTime", getInt(data, "rainTime", 0))
                .putBoolean("thundering", getBoolean(data, "thundering", false))
                .putInt("lightningTime", getInt(data, "lightningTime", 0))
                .putInt("noSleepNights", getInt(data, "nosleepnights", 0))
                .build();
    }

    private static NbtMap createSpawnState(NbtMap data) {
        return NbtMap.builder()
                .putInt("SpawnX", getInt(data, "SpawnX", 0))
                .putInt("SpawnY", getInt(data, "SpawnY", 0))
                .putInt("SpawnZ", getInt(data, "SpawnZ", 0))
                .build();
    }

    private static long getLong(NbtMap tag, String key, long fallback) {
        Object value = tag.get(key);
        return value instanceof Number number ? number.longValue() : fallback;
    }

    private static int getInt(NbtMap tag, String key, int fallback) {
        Object value = tag.get(key);
        return value instanceof Number number ? number.intValue() : fallback;
    }

    private static boolean getBoolean(NbtMap tag, String key, boolean fallback) {
        Object value = tag.get(key);
        return value instanceof Number number ? number.intValue() != 0 : fallback;
    }

    private static void writeLevelDatForMigration(Path worldPath, NbtMap data, NbtMap backupData, int version) throws IOException {
        Path levelDatPath = worldPath.resolve("level.dat");
        Path levelDatOldPath = worldPath.resolve("level.dat_old");
        Path tempPath = null;

        try {
            byte[] levelDatBytes = createLevelDatBytes(data, version);
            tempPath = Files.createTempFile(worldPath, "level.dat.", ".migration");
            try (FileOutputStream output = new FileOutputStream(tempPath.toFile())) {
                output.write(levelDatBytes);
                output.getFD().sync();
            }

            Files.write(levelDatOldPath, createLevelDatBytes(backupData, version));

            try {
                Files.move(tempPath, levelDatPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(tempPath, levelDatPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            if (tempPath != null) {
                Files.deleteIfExists(tempPath);
            }
        }
    }

    private static byte[] createLevelDatBytes(NbtMap data, int version) throws IOException {
        ByteArrayOutputStream nbtOutput = new ByteArrayOutputStream();
        try (NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(nbtOutput)) {
            nbtOutputStream.writeTag(data);
        }

        byte[] payload = nbtOutput.toByteArray();
        ByteArrayOutputStream output = new ByteArrayOutputStream(payload.length + 8);
        writeIntLittleEndian(output, version);
        writeIntLittleEndian(output, payload.length);
        output.write(payload);
        return output.toByteArray();
    }

    private static void writeIntLittleEndian(ByteArrayOutputStream output, int value) {
        output.write(value & 0xff);
        output.write((value >>> 8) & 0xff);
        output.write((value >>> 16) & 0xff);
        output.write((value >>> 24) & 0xff);
    }

    private static int readIntLittleEndian(byte[] data, int offset) {
        return (data[offset] & 0xff)
                | (data[offset + 1] & 0xff) << 8
                | (data[offset + 2] & 0xff) << 16
                | (data[offset + 3] & 0xff) << 24;
    }

    private List<WorldStorageMigrationData.LegacyTickingArea> readLegacyTickingAreas() throws IOException {
        Path path = getLegacyTickingAreaPath();
        if (!Files.exists(path)) return null;

        List<LegacyTickingAreaJson> stored;
        try {
            stored = JSONUtils.from(path.toFile(), LEGACY_TICKING_AREA_TYPE);
        } catch (RuntimeException e) {
            throw new IOException("Invalid legacy tickingarea.json", e);
        }

        if (stored == null) {
            throw new IOException("Legacy tickingarea.json does not contain an array");
        }

        List<WorldStorageMigrationData.LegacyTickingArea> result = new ArrayList<>(stored.size());
        for (LegacyTickingAreaJson area : stored) {
            if (area == null || area.name == null || area.levelName == null || area.levelName.isEmpty() || area.chunks == null) {
                throw new IOException("Invalid legacy ticking area entry");
            }

            List<WorldStorageMigrationData.LegacyTickingAreaChunk> chunks = new ArrayList<>(area.chunks.size());
            for (LegacyTickingAreaChunkJson chunk : area.chunks) {
                if (chunk == null) {
                    throw new IOException("Legacy ticking area '" + area.name + "' contains a null chunk");
                }
                chunks.add(new WorldStorageMigrationData.LegacyTickingAreaChunk(chunk.x, chunk.z));
            }

            result.add(new WorldStorageMigrationData.LegacyTickingArea(
                    area.name,
                    area.levelName,
                    resolveLegacyTickingAreaDimension(area.levelName),
                    chunks
            ));
        }
        return result;
    }

    private int resolveLegacyTickingAreaDimension(String levelName) throws IOException {
        if (this.expectedDimensions.size() == 1) {
            return this.expectedDimensions.iterator().next();
        }

        String baseLevelName = this.worldPath.getFileName().toString();
        int dimensionId;
        if (levelName.equals(baseLevelName)) {
            dimensionId = Level.DIMENSION_OVERWORLD;
        } else if (levelName.equals(baseLevelName + "_the_nether")) {
            dimensionId = Level.DIMENSION_NETHER;
        } else if (levelName.equals(baseLevelName + "_the_end")) {
            dimensionId = Level.DIMENSION_THE_END;
        } else {
            throw new IOException("Cannot resolve legacy ticking area dimension from level name '" + levelName + "'");
        }

        if (!this.expectedDimensions.contains(dimensionId)) {
            throw new IOException("Legacy ticking area level '" + levelName + "' resolves to an unavailable dimension " + dimensionId);
        }
        return dimensionId;
    }

    private Path getLegacyTickingAreaPath() {
        return this.worldPath.resolve(LEGACY_TICKING_AREA_FILE);
    }

    private static byte[] getTickingAreaKey(UUID uuid) {
        return (TICKING_AREA_KEY_PREFIX + uuid).getBytes(StandardCharsets.UTF_8);
    }

    private static CompoundTag readSingleCompound(byte[] data, String invalidRootMessage) throws IOException {
        if (data == null) return null;

        List<CompoundTag> roots = readLittleEndianCompounds(data);
        if (roots.size() != 1) {
            throw new IOException(invalidRootMessage);
        }
        return roots.getFirst();
    }

    private static List<CompoundTag> readLittleEndianCompounds(byte[] data) throws IOException {
        ByteBuf buffer = Unpooled.wrappedBuffer(data);
        try (NBTInputStream input = NbtUtils.createReaderLE(new ByteBufInputStream(buffer))) {
            List<CompoundTag> compounds = new ArrayList<>();
            while (buffer.readableBytes() > 0) {
                Object tag = input.readTag();
                if (!(tag instanceof NbtMap map)) {
                    throw new IOException("Expected compound NBT root");
                }
                compounds.add(CompoundTag.fromNetwork(map));
            }
            return compounds;
        } finally {
            buffer.release();
        }
    }

    private static byte[] writeLittleEndianCompound(CompoundTag tag) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (NBTOutputStream nbt = NbtUtils.createWriterLE(output)) {
            nbt.writeTag(tag.toNetwork());
        }
        return output.toByteArray();
    }

    /**
     * Represents a level.dat source loaded for migration.
     */
    private record LevelDatMigrationFile(Path source, NbtMap data, int version, boolean legacyHeader) {}

    /**
     * Represents one legacy ticking area JSON entry.
     */
    private static final class LegacyTickingAreaJson {
        String name;
        String levelName;
        List<LegacyTickingAreaChunkJson> chunks;
    }

    /**
     * Represents one chunk coordinate from a legacy ticking area JSON entry.
     */
    private static final class LegacyTickingAreaChunkJson {
        int x;
        int z;
    }
}
