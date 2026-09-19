package org.powernukkitx.migration.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.Server;
import org.powernukkitx.ServerDBStorageFormat;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationService;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.PositionTrackingMigrationData;
import org.powernukkitx.migration.steps.PositionTrackingV3_1_0Migration;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Orchestrates position-tracking migrations between legacy PNX files, native BDS world storage and the server-global canonical database.
 * Version-specific transformations remain in registered {@link MigrationStep} implementations; this class only reads sources, commits
 * migrated values and removes source data after successful commits.
 *
 * @author Curse
 */
@Slf4j
public final class PositionTrackingMigrationExecutor {
    private static final byte[] PNT_HEADER = new byte[]{12, 32, 32, 'P', 'N', 'P', 'T', 'D', 'B', '1'};
    private static final int PNT_HEADER_SIZE = PNT_HEADER.length + Integer.BYTES * 3;
    private static final int PNT_RECORD_SIZE = 1 + Long.BYTES + Integer.BYTES + Double.BYTES * 3;
    private static final byte[] BDS_MIGRATION_PREFIX = "\0pnx_server_data:position_tracking:migration:bds:".getBytes(StandardCharsets.UTF_8);
    private static final byte[] LEGACY_PNX_MIGRATION_KEY = "\0pnx_server_data:position_tracking:migration:legacy_pnx".getBytes(StandardCharsets.UTF_8);
    private static final String POSITION_PREFIX = new String(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX, StandardCharsets.UTF_8);

    private PositionTrackingMigrationExecutor() {
    }

    /**
     * Migrates the legacy server-global PNX .pnt storage and upgrades existing canonical data when required.
     */
    public static void migrateLegacyPnxIfNeeded(Path legacyPath, DB targetDB, MigrationService migrationService) throws IOException {
        MigrationVersion currentVersion = readMigrationVersion(targetDB, migrationService);
        MigrationVersion targetVersion = migrationService.getLatestVersion(MigrationFormat.POSITION_TRACKING);
        boolean legacyExists = Files.exists(legacyPath);

        if (currentVersion != null && currentVersion.compareTo(targetVersion) < 0) {
            PositionTrackingMigrationData canonical = readCanonicalData(targetDB);
            PositionTrackingMigrationData migrated = migrationService.apply(MigrationFormat.POSITION_TRACKING, currentVersion, canonical);
            writeReplacement(targetDB, migrated, targetVersion);
            log.info("[PositionTracking Migration] Migrated canonical position tracking from {} to {}", currentVersion, targetVersion);
        } else if (currentVersion == null && !legacyExists) {
            writeMigrationVersion(targetDB, targetVersion);
        }

        if (!legacyExists) {
            targetDB.delete(LEGACY_PNX_MIGRATION_KEY);
            migrationService.getContext().clearLegacyPositionTrackingRemap();
            return;
        }

        if (!Files.isDirectory(legacyPath)) {
            throw new IOException("Legacy position-tracking path is not a directory: " + legacyPath);
        }

        LegacyPntData source = readLegacyPnt(legacyPath, collectLevelDimensions(migrationService.getContext().getServer()));
        Reservation reservation = readReservation(targetDB.get(LEGACY_PNX_MIGRATION_KEY));

        if (reservation != null && reservation.sourceLastId() != source.lastId()) {
            throw new IOException("Legacy PNX position-tracking reservation no longer matches the source allocator");
        }

        PositionTrackingMigrationData migrated = migrationService.apply(
                MigrationFormat.POSITION_TRACKING,
                null,
                new PositionTrackingMigrationData(
                        PositionTrackingMigrationData.Source.LEGACY_PNX,
                        source.entries(),
                        source.lastId(),
                        readLastId(targetDB),
                        reservation != null ? reservation.offset() : -1,
                        List.of(),
                        0,
                        0
                )
        );

        writeLegacyPnxImport(targetDB, migrated, targetVersion);
        migrationService.getContext().setLegacyPositionTrackingRemap(source.lastId(), migrated.handleOffset());

        log.info(
                "[PositionTracking Migration] Prepared {} legacy PNX records with handle offset {}",
                migrated.canonicalEntries().size(),
                migrated.handleOffset()
        );
    }

    /**
     * Removes legacy PNX position-tracking source storage after all persisted consumers have been migrated.
     */
    public static void completeLegacyPnxMigration(Path legacyPath, DB targetDB, MigrationService migrationService) throws IOException {
        if (targetDB.get(LEGACY_PNX_MIGRATION_KEY) == null) {
            migrationService.getContext().clearLegacyPositionTrackingRemap();
            return;
        }

        if (Files.exists(legacyPath)) {
            deleteLegacySource(legacyPath);
        }

        targetDB.delete(LEGACY_PNX_MIGRATION_KEY);
        migrationService.getContext().clearLegacyPositionTrackingRemap();
    }

    /**
     * Imports one native BDS world's position-tracking storage after that world's normal LevelDB migration has completed.
     */
    public static void migrateBdsWorldIfNeeded(
            LevelDBStorage storage,
            Path worldPath,
            LevelConfig levelConfig,
            Set<Integer> expectedDimensions,
            DB targetDB,
            MigrationService migrationService
    ) throws IOException {
        DB sourceDB = storage.getDb();
        String worldName = worldPath.getFileName().toString();
        byte[] markerKey = bdsMigrationMarkerKey(worldName);
        byte[] sourceLastIdValue = sourceDB.get(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_LAST_ID_KEY);
        boolean hasAllocator = sourceLastIdValue != null && sourceLastIdValue.length != 0;
        boolean hasRecords = hasNonEmptyPositionRecords(sourceDB);

        if (!hasAllocator && !hasRecords) {
            if (targetDB.get(markerKey) != null) {
                targetDB.delete(markerKey);
            }
            return;
        }
        if (!hasAllocator) {
            throw new IOException("BDS world " + worldName + " has position-tracking records without PositionTrackDB-LastId");
        }

        int sourceLastId = readLastId(sourceDB);
        Map<Integer, String> levelNames = collectBdsLevelNames(levelConfig, expectedDimensions, worldName);
        List<PositionTrackingMigrationData.Entry> entries = readBdsEntries(sourceDB, levelNames);
        int targetLastId = readLastId(targetDB);
        Reservation reservation = readReservation(targetDB.get(markerKey));

        if (reservation != null && reservation.sourceLastId() != sourceLastId) {
            throw new IOException("BDS position-tracking migration reservation no longer matches world " + worldName);
        }

        PositionTrackingMigrationData migrated = migrationService.apply(
                MigrationFormat.POSITION_TRACKING,
                null,
                new PositionTrackingMigrationData(
                        PositionTrackingMigrationData.Source.BDS,
                        entries,
                        sourceLastId,
                        targetLastId,
                        reservation != null ? reservation.offset() : -1,
                        List.of(),
                        0,
                        0
                )
        );

        MigrationVersion targetVersion = migrationService.getLatestVersion(MigrationFormat.POSITION_TRACKING);
        writeBdsImport(targetDB, migrated, targetVersion, markerKey);
        PositionTrackingV3_1_0Migration.remapBdsWorld(sourceDB, sourceLastId, migrated.handleOffset());
        targetDB.delete(markerKey);

        log.info(
                "[PositionTracking Migration] Imported {} BDS records from {} with handle offset {}",
                migrated.canonicalEntries().size(),
                worldName,
                migrated.handleOffset()
        );
    }

    private static LegacyPntData readLegacyPnt(Path legacyPath, Map<String, Integer> levelDimensions) throws IOException {
        List<Path> files;
        try (Stream<Path> stream = Files.list(legacyPath)) {
            files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().matches("\\d+\\.pnt"))
                    .sorted(Comparator.comparingInt(PositionTrackingMigrationExecutor::readPntFileStart))
                    .toList();
        }

        Map<Integer, PositionTrackingMigrationData.Entry> entries = new TreeMap<>();
        int lastId = 0;

        for (Path file : files) {
            try (RandomAccessFile input = new RandomAccessFile(file.toFile(), "r")) {
                byte[] header = new byte[PNT_HEADER.length];
                input.readFully(header);
                int maxStorage = input.readInt();
                int nextIndex = input.readInt();
                int startIndex = input.readInt();

                if (!Arrays.equals(header, PNT_HEADER) || maxStorage <= 0 || startIndex <= 0 || nextIndex < startIndex
                        || nextIndex > startIndex + maxStorage || startIndex != readPntFileStart(file)) {
                    throw new IOException("Invalid legacy position-tracking file: " + file);
                }

                lastId = Math.max(lastId, nextIndex - 1);
                for (int handle = startIndex; handle < nextIndex; handle++) {
                    long recordPosition = PNT_HEADER_SIZE + (long) PNT_RECORD_SIZE * (handle - startIndex);
                    if (recordPosition + PNT_RECORD_SIZE > input.length()) {
                        throw new IOException("Truncated legacy position-tracking record " + handle + " in " + file);
                    }

                    input.seek(recordPosition);
                    boolean enabled = input.readBoolean();
                    long namePosition = input.readLong();
                    int nameLength = input.readInt();
                    double x = input.readDouble();
                    double y = input.readDouble();
                    double z = input.readDouble();

                    if (namePosition == 0) {
                        entries.remove(handle);
                        continue;
                    }
                    if (nameLength <= 0 || namePosition < 0 || namePosition + nameLength > input.length()) {
                        throw new IOException("Invalid level name storage for legacy position-tracking handle " + handle);
                    }

                    byte[] nameBytes = new byte[nameLength];
                    input.seek(namePosition);
                    input.readFully(nameBytes);
                    String levelName = new String(nameBytes, StandardCharsets.UTF_8);
                    Integer dimensionId = levelDimensions.get(levelName);
                    if (dimensionId == null) {
                        throw new IOException("Legacy position-tracking handle " + handle + " references unknown level " + levelName);
                    }

                    entries.put(handle, new PositionTrackingMigrationData.Entry(
                            handle,
                            dimensionId,
                            levelName,
                            x,
                            y,
                            z,
                            enabled ? (byte) 0 : (byte) 1
                    ));
                }
            }
        }

        return new LegacyPntData(new ArrayList<>(entries.values()), lastId);
    }

    private static int readPntFileStart(Path path) {
        String name = path.getFileName().toString();
        return Integer.parseInt(name.substring(0, name.length() - 4));
    }

    private static Map<String, Integer> collectLevelDimensions(Server server) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        Path worlds = Path.of(server.getDataPath(), "worlds");

        try (Stream<Path> stream = Files.list(worlds)) {
            for (Path world : stream.filter(Files::isDirectory).toList()) {
                LevelConfig config = server.getLevelConfig(world.getFileName().toString());
                if (config == null) {
                    continue;
                }

                int dimensionCount = config.generators().size();
                for (LevelConfig.GeneratorConfig generator : config.generators().values()) {
                    String levelName = world.getFileName() + (dimensionCount > 1 ? generator.dimensionData().getSuffix() : "");
                    Integer previous = result.put(levelName, generator.dimensionData().getDimensionId());
                    if (previous != null && previous != generator.dimensionData().getDimensionId()) {
                        throw new IOException("Duplicate PNX level name while migrating position tracking: " + levelName);
                    }
                }
            }
        }
        return result;
    }

    private static Map<Integer, String> collectBdsLevelNames(
            LevelConfig config,
            Set<Integer> expectedDimensions,
            String worldName
    ) throws IOException {
        Map<Integer, String> result = new HashMap<>();
        int dimensionCount = config.generators().size();
        for (LevelConfig.GeneratorConfig generator : config.generators().values()) {
            int dimensionId = generator.dimensionData().getDimensionId();
            String levelName = worldName + (dimensionCount > 1 ? generator.dimensionData().getSuffix() : "");
            String previous = result.put(dimensionId, levelName);
            if (previous != null && !previous.equals(levelName)) {
                throw new IOException("Duplicate BDS dimension " + dimensionId + " while migrating position tracking for " + worldName);
            }
        }

        for (int dimensionId : expectedDimensions) {
            if (!result.containsKey(dimensionId)) {
                throw new IOException("Missing PNX level name for BDS dimension " + dimensionId + " in " + worldName);
            }
        }

        result.putIfAbsent(1, worldName + "_the_nether");
        result.putIfAbsent(2, worldName + "_the_end");
        return result;
    }

    private static List<PositionTrackingMigrationData.Entry> readBdsEntries(
            DB sourceDB,
            Map<Integer, String> levelNames
    ) throws IOException {
        List<PositionTrackingMigrationData.Entry> result = new ArrayList<>();

        try (DBIterator iterator = sourceDB.iterator()) {
            iterator.seek(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX);
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (!isPositionKey(entry.getKey())) {
                    break;
                }
                if (entry.getValue().length == 0) {
                    continue;
                }

                int handle = readHandle(entry.getKey());
                CompoundTag root = readCompound(entry.getValue());
                validateRecordRoot(root, handle, false);
                int dimensionId = root.getInt("dim");
                String levelName = levelNames.get(dimensionId);
                if (levelName == null) {
                    throw new IOException("BDS position-tracking handle " + handle + " references unknown dimension " + dimensionId);
                }

                ListTag<IntTag> pos = root.getList("pos", IntTag.class);
                result.add(new PositionTrackingMigrationData.Entry(
                        handle,
                        dimensionId,
                        levelName,
                        pos.get(0).data,
                        pos.get(1).data,
                        pos.get(2).data,
                        root.getByte("status")
                ));
            }
        }
        return result;
    }

    private static PositionTrackingMigrationData readCanonicalData(DB targetDB) throws IOException {
        int lastId = readLastId(targetDB);
        List<PositionTrackingMigrationData.Entry> entries = new ArrayList<>();

        try (DBIterator iterator = targetDB.iterator()) {
            iterator.seek(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX);
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (!isPositionKey(entry.getKey())) {
                    break;
                }

                int handle = readHandle(entry.getKey());
                CompoundTag root = readCompound(entry.getValue());
                validateRecordRoot(root, handle, true);
                ListTag<IntTag> pos = root.getList("pos", IntTag.class);
                entries.add(new PositionTrackingMigrationData.Entry(
                        handle,
                        root.getInt("dim"),
                        root.getString("level"),
                        pos.get(0).data,
                        pos.get(1).data,
                        pos.get(2).data,
                        root.getByte("status")
                ));
            }
        }

        return new PositionTrackingMigrationData(
                PositionTrackingMigrationData.Source.CANONICAL,
                entries,
                lastId,
                lastId,
                0,
                List.of(),
                0,
                lastId
        );
    }

    private static void validateRecordRoot(CompoundTag root, int handle, boolean requireLevel) throws IOException {
        if (!root.containsByte("version") || root.getByte("version") != 1 || !root.containsString("id")
                || !formatId(handle).equals(root.getString("id")) || !root.containsInt("dim") || !root.containsByte("status")) {
            throw new IOException("Invalid position-tracking record for handle " + handle);
        }
        if (!root.containsList("pos", Tag.TAG_Int)) {
            throw new IOException("Invalid position list for handle " + handle);
        }
        ListTag<IntTag> pos = root.getList("pos", IntTag.class);
        if (pos.size() != 3) {
            throw new IOException("Invalid position list size for handle " + handle + ": " + pos.size());
        }
        if (requireLevel && (!root.containsString("level") || root.getString("level").isEmpty())) {
            throw new IOException("Canonical position-tracking record has no level for handle " + handle);
        }
    }

    private static boolean hasNonEmptyPositionRecords(DB database) throws IOException {
        try (DBIterator iterator = database.iterator()) {
            iterator.seek(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX);
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (!isPositionKey(entry.getKey())) {
                    return false;
                }
                if (entry.getValue().length != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void writeReplacement(
            DB targetDB,
            PositionTrackingMigrationData migrated,
            MigrationVersion version
    ) throws IOException {
        try (WriteBatch batch = targetDB.createWriteBatch()) {
            try (DBIterator iterator = targetDB.iterator()) {
                iterator.seek(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX);
                while (iterator.hasNext()) {
                    byte[] key = iterator.next().getKey();
                    if (!isPositionKey(key)) {
                        break;
                    }
                    batch.delete(key);
                }
            }

            for (PositionTrackingMigrationData.CanonicalEntry entry : migrated.canonicalEntries()) {
                batch.put(positionKey(entry.handle()), writeCanonicalEntry(entry));
            }
            writeAllocator(batch, migrated.migratedLastId());
            batch.put(
                    ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_STORAGE_VERSION_KEY,
                    version.toString().getBytes(StandardCharsets.UTF_8)
            );
            targetDB.write(batch);
        }
    }

    private static void writeLegacyPnxImport(
            DB targetDB,
            PositionTrackingMigrationData migrated,
            MigrationVersion version
    ) throws IOException {
        try (WriteBatch batch = targetDB.createWriteBatch()) {
            for (PositionTrackingMigrationData.CanonicalEntry entry : migrated.canonicalEntries()) {
                byte[] key = positionKey(entry.handle());
                byte[] value = writeCanonicalEntry(entry);
                byte[] existing = targetDB.get(key);

                if (existing != null && !Arrays.equals(existing, value)) {
                    throw new IOException("Position-tracking handle collision while importing legacy PNX handle " + entry.handle());
                }

                batch.put(key, value);
            }

            writeAllocator(batch, migrated.migratedLastId());
            batch.put(
                    ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_STORAGE_VERSION_KEY,
                    version.toString().getBytes(StandardCharsets.UTF_8)
            );
            batch.put(LEGACY_PNX_MIGRATION_KEY, writeReservation(migrated.sourceLastId(), migrated.handleOffset()));
            targetDB.write(batch);
        }
    }

    private static void writeBdsImport(
            DB targetDB,
            PositionTrackingMigrationData migrated,
            MigrationVersion version,
            byte[] markerKey
    ) throws IOException {
        try (WriteBatch batch = targetDB.createWriteBatch()) {
            for (PositionTrackingMigrationData.CanonicalEntry entry : migrated.canonicalEntries()) {
                byte[] key = positionKey(entry.handle());
                byte[] value = writeCanonicalEntry(entry);
                byte[] existing = targetDB.get(key);
                if (existing != null && !Arrays.equals(existing, value)) {
                    throw new IOException("Position-tracking handle collision while importing BDS handle " + entry.handle());
                }
                batch.put(key, value);
            }

            writeAllocator(batch, migrated.migratedLastId());
            batch.put(
                    ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_STORAGE_VERSION_KEY,
                    version.toString().getBytes(StandardCharsets.UTF_8)
            );
            batch.put(markerKey, writeReservation(migrated.sourceLastId(), migrated.handleOffset()));
            targetDB.write(batch);
        }
    }

    private static void writeAllocator(WriteBatch batch, int lastId) throws IOException {
        if (lastId > 0) {
            batch.put(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_LAST_ID_KEY, writeLastId(lastId));
        } else {
            batch.delete(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_LAST_ID_KEY);
        }
    }

    private static byte[] writeCanonicalEntry(PositionTrackingMigrationData.CanonicalEntry entry) throws IOException {
        CompoundTag root = new CompoundTag()
                .putInt("dim", entry.dimensionId())
                .putString("id", formatId(entry.handle()))
                .putList("pos", new ListTag<IntTag>(Tag.TAG_Int)
                        .add(new IntTag(entry.x()))
                        .add(new IntTag(entry.y()))
                        .add(new IntTag(entry.z())))
                .putByte("status", entry.status())
                .putByte("version", 1)
                .putString("level", entry.levelName());
        return writeCompound(root);
    }

    private static int readLastId(DB database) throws IOException {
        byte[] value = database.get(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_LAST_ID_KEY);
        if (value == null || value.length == 0) {
            return 0;
        }

        CompoundTag root = readCompound(value);
        if (!root.containsByte("version") || root.getByte("version") != 1 || !root.containsString("id")) {
            throw new IOException("Invalid PositionTrackDB-LastId record");
        }
        return parseId(root.getString("id"));
    }

    private static byte[] writeLastId(int lastId) throws IOException {
        return writeCompound(new CompoundTag().putString("id", formatId(lastId)).putByte("version", 1));
    }

    private static CompoundTag readCompound(byte[] value) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(value);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            Object tag = nbtInputStream.readTag();
            if (!(tag instanceof NbtMap map)) {
                throw new IOException("Position-tracking value is not a compound");
            }
            return CompoundTag.fromNetwork(map);
        }
    }

    private static byte[] writeCompound(CompoundTag root) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(root.toNetwork());
            return outputStream.toByteArray();
        }
    }

    private static MigrationVersion readMigrationVersion(DB targetDB, MigrationService migrationService) throws IOException {
        byte[] stored = targetDB.get(ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_STORAGE_VERSION_KEY);
        if (stored == null) return null;
        return migrationService.parseStoredVersion(MigrationFormat.POSITION_TRACKING, new String(stored, StandardCharsets.UTF_8));
    }

    private static void writeMigrationVersion(DB targetDB, MigrationVersion version) {
        targetDB.put(
                ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_STORAGE_VERSION_KEY,
                version.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    private static byte[] bdsMigrationMarkerKey(String worldName) {
        byte[] name = worldName.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(BDS_MIGRATION_PREFIX.length + name.length);
        buffer.put(BDS_MIGRATION_PREFIX);
        buffer.put(name);
        return buffer.array();
    }

    private static byte[] writeReservation(int sourceLastId, int offset) {
        return ByteBuffer.allocate(Integer.BYTES * 2).putInt(sourceLastId).putInt(offset).array();
    }

    private static Reservation readReservation(byte[] value) throws IOException {
        if (value == null) {
            return null;
        }
        if (value.length != Integer.BYTES * 2) {
            throw new IOException("Invalid position-tracking migration reservation");
        }
        ByteBuffer buffer = ByteBuffer.wrap(value);
        int sourceLastId = buffer.getInt();
        int offset = buffer.getInt();
        if (sourceLastId < 0 || offset < 0) {
            throw new IOException("Invalid position-tracking migration reservation values");
        }
        return new Reservation(sourceLastId, offset);
    }

    private static byte[] positionKey(int handle) {
        return (POSITION_PREFIX + formatId(handle)).getBytes(StandardCharsets.UTF_8);
    }

    private static boolean isPositionKey(byte[] key) {
        byte[] prefix = ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX;
        if (key.length != prefix.length + 10) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (key[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static int readHandle(byte[] key) throws IOException {
        String id = new String(key, ServerDBStorageFormat.SERVER_DATA_POSITION_TRACKING_PREFIX.length, 10, StandardCharsets.UTF_8);
        int handle = parseId(id);
        if (handle <= 0) {
            throw new IOException("Invalid position-tracking key: " + id);
        }
        return handle;
    }

    private static int parseId(String value) throws IOException {
        if (value.length() != 10 || !value.startsWith("0x")) {
            throw new IOException("Invalid position-tracking id: " + value);
        }
        try {
            long id = Long.parseLong(value.substring(2), 16);
            if (id < 0 || id > Integer.MAX_VALUE) {
                throw new IOException("Position-tracking id is outside the supported range: " + value);
            }
            return (int) id;
        } catch (NumberFormatException e) {
            throw new IOException("Invalid position-tracking id: " + value, e);
        }
    }

    private static String formatId(int handle) {
        return String.format("0x%08x", handle);
    }

    private static void deleteLegacySource(Path legacyPath) throws IOException {
        FileUtils.deleteDirectory(legacyPath.toFile());
        Path servicesPath = legacyPath.getParent();
        if (servicesPath == null || !Files.isDirectory(servicesPath)) {
            return;
        }

        try (Stream<Path> stream = Files.list(servicesPath)) {
            if (stream.findAny().isEmpty()) {
                Files.delete(servicesPath);
            }
        }
        log.info("[PositionTracking Migration] Deleted legacy position-tracking storage");
    }

    /**
     * Represents normalized data loaded from legacy PNX position-tracking files.
     */
    private record LegacyPntData(List<PositionTrackingMigrationData.Entry> entries, int lastId) {
    }

    /**
     * Represents a reserved handle range for an in-progress BDS position-tracking import.
     */
    private record Reservation(int sourceLastId, int offset) {
    }
}
