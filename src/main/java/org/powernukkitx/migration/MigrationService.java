package org.powernukkitx.migration;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.Server;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.migration.executor.ActorMigrationExecutor;
import org.powernukkitx.migration.executor.BlockEntityMigrationExecutor;
import org.powernukkitx.migration.executor.ChunkMigrationExecutor;
import org.powernukkitx.migration.executor.DynamicPropertiesMigrationExecutor;
import org.powernukkitx.migration.executor.PlayerMigrationExecutor;
import org.powernukkitx.migration.executor.PositionTrackingMigrationExecutor;
import org.powernukkitx.migration.executor.ScoreboardMigrationExecutor;
import org.powernukkitx.migration.executor.StructureMigrationExecutor;
import org.powernukkitx.migration.executor.WorldStorageMigrationExecutor;
import org.powernukkitx.migration.leveldb.LevelDBMigrationChunkSerializer;
import org.powernukkitx.migration.leveldb.LevelDBMigrationVersionStore;
import org.powernukkitx.migration.steps.LevelDBActorV3_1_0Migration;
import org.powernukkitx.migration.steps.LevelDBBlockEntityV3_1_0Migration;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.JSONUtils;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Coordinates startup storage migrations and executes registered versioned migration steps.
 *
 * @author Curse
 */
@Slf4j
public final class MigrationService {
    private static final String MIGRATION_SIGNATURE_KEY = "migrationSignature";
    private static final String DIMENSION_MIGRATION_SIGNATURE_KEY_PREFIX = "migrationSignatureDimension_";

    private final MigrationContext context;

    /**
     * Creates a migration service for the supplied server.
     */
    public MigrationService(Server server) {
        this.context = new MigrationContext(server);
    }

    /**
     * Returns the shared migration context.
     */
    public MigrationContext getContext() {
        return this.context;
    }

    /**
     * Migrates global structure storage and imports native BDS structure templates.
     */
    public void migrateStructures() throws IOException {
        StructureMigrationExecutor.migrateIfNeeded(Path.of(this.context.getServer().getDataPath()), this);
    }

    /**
     * Migrates legacy or canonical player storage.
     */
    public void migratePlayers(DB sourceDB, DB targetDB, BiConsumer<UUID, CompoundTag> uniqueIdAssigner) throws IOException {
        PlayerMigrationExecutor.migrate(sourceDB, targetDB, uniqueIdAssigner, this);
    }

    /**
     * Migrates legacy server-global position tracking storage.
     */
    public void migrateLegacyPositionTracking(Path legacyPath, DB targetDB) throws IOException {
        PositionTrackingMigrationExecutor.migrateLegacyPnxIfNeeded(legacyPath, targetDB, this);
    }

    /**
     * Migrates every valid stored LevelDB world before runtime levels are loaded.
     */
    public boolean migrateWorlds(DB serverDataDB, BiConsumer<UUID, CompoundTag> uniqueIdAssigner) throws IOException {
        Server server = this.context.getServer();
        Path worldsPath = Path.of(server.getDataPath(), "worlds");
        if (!Files.isDirectory(worldsPath)) {
            throw new IOException("World storage path is not a directory: " + worldsPath);
        }

        List<Path> worlds;
        try (Stream<Path> stream = Files.list(worldsPath)) {
            worlds = stream.filter(Files::isDirectory)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
        }

        boolean scoreboardMigrated = false;

        for (Path worldPath : worlds) {
            scoreboardMigrated |= migrateWorld(server, worldPath, serverDataDB, uniqueIdAssigner);
        }

        scoreboardMigrated |= ScoreboardMigrationExecutor.migrateLegacyPnxIfNeeded(
                Path.of(server.getDataPath()),
                serverDataDB,
                this
        );

        PositionTrackingMigrationExecutor.completeLegacyPnxMigration(
                Path.of(server.getDataPath(), "services", "position_tracking_db"),
                serverDataDB,
                this
        );

        return scoreboardMigrated;
    }

    /**
     * Migrates scoreboard storage when required.
     */
    public void migrateScoreboard(DB targetDB) throws IOException {
        ScoreboardMigrationExecutor.migrateIfNeeded(Path.of(this.context.getServer().getDataPath()), targetDB, this);
    }

    private boolean migrateWorld(
            Server server,
            Path worldPath,
            DB serverDataDB,
            BiConsumer<UUID, CompoundTag> uniqueIdAssigner
    ) throws IOException {
        String worldName = worldPath.getFileName().toString();
        Path configPath = worldPath.resolve("config.json");
        boolean levelDBWorld = LevelDBProvider.isValid(worldPath.toString());

        if (!Files.isRegularFile(configPath) && !levelDBWorld) return false;

        LevelConfig levelConfig;
        try {
            levelConfig = Files.isRegularFile(configPath)
                    ? JSONUtils.from(configPath.toFile(), LevelConfig.class)
                    : server.getLevelConfig(worldName);
        } catch (Exception e) {
            throw new IOException("Failed to read level configuration for world \"" + worldName + "\"", e);
        }

        if (levelConfig == null) {
            throw new IOException("Missing or invalid level configuration for world \"" + worldName + "\"");
        }

        if (!"leveldb".equalsIgnoreCase(levelConfig.format())) return false;

        boolean levelDBStorageExists = Files.exists(worldPath.resolve("db"));
        if (levelDBStorageExists && !LevelDBProvider.isValidStorage(worldPath.toString())) {
            throw new IOException("Configured LevelDB world \"" + worldName + "\" does not contain valid LevelDB storage");
        }

        try {
            return migrateLevelDBWorld(worldPath, levelConfig, serverDataDB, levelDBStorageExists, uniqueIdAssigner);
        } catch (Exception e) {
            throw new IOException("Mandatory storage migration failed for world \"" + worldName + "\"", e);
        }
    }

    private boolean migrateLevelDBWorld(
            Path worldPath,
            LevelConfig levelConfig,
            DB serverDataDB,
            boolean levelDBStorageExists,
            BiConsumer<UUID, CompoundTag> uniqueIdAssigner
    ) throws IOException {
        if (levelConfig.generators() == null || levelConfig.generators().size() == 0) {
            throw new IOException("Stage configuration failed: world contains no configured dimensions");
        }

        Map<Integer, LevelConfig.GeneratorConfig> generatorsByDimension = new HashMap<>();
        for (LevelConfig.GeneratorConfig generatorConfig : levelConfig.generators().values()) {
            if (generatorConfig == null || generatorConfig.dimensionData() == null) {
                throw new IOException("Stage configuration failed: world contains an invalid dimension configuration");
            }

            int dimensionId = generatorConfig.dimensionData().getDimensionId();
            if (generatorsByDimension.putIfAbsent(dimensionId, generatorConfig) != null) {
                throw new IOException("Stage configuration failed: duplicate dimension " + dimensionId);
            }
        }

        Set<Integer> expectedDimensions = Set.copyOf(generatorsByDimension.keySet());

        try {
            WorldStorageMigrationExecutor.migrateLevelDatIfNeeded(worldPath.toString(), expectedDimensions);
        } catch (Exception e) {
            throw new IOException("Stage level.dat failed", e);
        }

        if (!levelDBStorageExists) return false;

        int generatorType;
        try {
            generatorType = WorldStorageMigrationExecutor.readGeneratorType(worldPath);
        } catch (Exception e) {
            throw new IOException("Stage level.dat generator failed", e);
        }

        LevelDBStorage storage;
        try {
            storage = new LevelDBStorage(1, worldPath.toString());
        } catch (Exception e) {
            throw new IOException("Stage LevelDB open failed", e);
        }

        try {
            synchronized (storage) {
                DB db = storage.getDb();
                String dimensionMigrationSignature = LevelDBMigrationVersionStore.getMigrationSignature();
                String worldMigrationSignature = LevelDBMigrationVersionStore.getWorldMigrationSignature();
                CompoundTag globalMarker = LevelDBMigrationVersionStore.readGlobalMarker(db);

                WorldStorageMigrationExecutor worldStorage = new WorldStorageMigrationExecutor(storage, worldPath, expectedDimensions);
                MigrationVersion currentWorldStorageVersion = worldStorage.getCurrentVersion(this);

                if (globalMarker != null
                        && worldMigrationSignature.equals(globalMarker.getString(MIGRATION_SIGNATURE_KEY))
                        && getLatestVersion(MigrationFormat.WORLD_STORAGE).equals(currentWorldStorageVersion)) {
                    return migrateBdsGlobalStorage(
                            storage,
                            worldPath,
                            levelConfig,
                            expectedDimensions,
                            serverDataDB,
                            uniqueIdAssigner
                    );
                }

                if (globalMarker == null) {
                    globalMarker = new CompoundTag();
                }

                if (globalMarker.contains(MIGRATION_SIGNATURE_KEY)) {
                    globalMarker.remove(MIGRATION_SIGNATURE_KEY);
                    LevelDBMigrationVersionStore.writeGlobalMarker(storage, globalMarker);
                }

                try {
                    worldStorage.migrate(this, currentWorldStorageVersion);
                } catch (Exception e) {
                    throw new IOException("Stage WORLD_STORAGE failed", e);
                }

                Map<UUID, Long> migratedActorUniqueIdsByUuid = new HashMap<>();

                for (var entry : generatorsByDimension.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                    int dimensionId = entry.getKey();
                    LevelConfig.GeneratorConfig generatorConfig = entry.getValue();
                    String dimensionSignatureKey = getDimensionMigrationSignatureKey(dimensionId);

                    if (dimensionMigrationSignature.equals(globalMarker.getString(dimensionSignatureKey))) {
                        continue;
                    }

                    LevelProvider migrationProvider = createMigrationProvider(worldPath, generatorConfig);

                    try {
                        migrateDimension(
                                storage,
                                migrationProvider,
                                generatorConfig.dimensionData(),
                                generatorType,
                                migratedActorUniqueIdsByUuid
                        );
                    } catch (Exception e) {
                        throw new IOException("Stage dimension migration failed for dimension " + dimensionId, e);
                    }

                    globalMarker.putString(dimensionSignatureKey, dimensionMigrationSignature);
                    LevelDBMigrationVersionStore.writeGlobalMarker(storage, globalMarker);
                }

                if (!areAllDimensionsMigrated(globalMarker, expectedDimensions, dimensionMigrationSignature)) {
                    throw new IOException("Stage dimension verification failed");
                }

                for (Map.Entry<UUID, Long> entry : migratedActorUniqueIdsByUuid.entrySet()) {
                    this.context.getMigratedActorUniqueIdsByUuid().putIfAbsent(entry.getKey(), entry.getValue());
                }

                try {
                    DynamicPropertiesMigrationExecutor.normalizeWorld(
                            storage,
                            Server.getDefaultDynamicPropertiesGroupUUID()
                    );
                } catch (Exception e) {
                    throw new IOException("Stage BDS DynamicProperties migration failed", e);
                }

                try {
                    ActorMigrationExecutor.reconcileActorStorage(storage, db);
                } catch (Exception e) {
                    throw new IOException("Stage actor reconciliation failed", e);
                }

                boolean scoreboardMigrated = migrateBdsGlobalStorage(
                        storage,
                        worldPath,
                        levelConfig,
                        expectedDimensions,
                        serverDataDB,
                        uniqueIdAssigner
                );

                globalMarker.putString(MIGRATION_SIGNATURE_KEY, worldMigrationSignature);

                try {
                    LevelDBMigrationVersionStore.writeGlobalMarker(storage, globalMarker);
                } catch (Exception e) {
                    throw new IOException("Stage completion marker failed", e);
                }

                log.info("[LevelDB Migration] World storage migrations completed ({})", worldMigrationSignature);
                return scoreboardMigrated;
            }
        } finally {
            storage.close();
        }
    }

    private boolean migrateBdsGlobalStorage(
            LevelDBStorage storage,
            Path worldPath,
            LevelConfig levelConfig,
            Set<Integer> expectedDimensions,
            DB serverDataDB,
            BiConsumer<UUID, CompoundTag> uniqueIdAssigner
    ) throws IOException {
        try {
            PositionTrackingMigrationExecutor.migrateBdsWorldIfNeeded(
                    storage,
                    worldPath,
                    levelConfig,
                    expectedDimensions,
                    serverDataDB,
                    this
            );
        } catch (Exception e) {
            throw new IOException("Stage PositionTracking migration failed", e);
        }

        boolean hasBdsPlayerStorage;
        try {
            hasBdsPlayerStorage = PlayerMigrationExecutor.hasBdsWorldPlayerStorage(storage.getDb());
        } catch (Exception e) {
            throw new IOException("Stage BDS player storage inspection failed", e);
        }

        Map<Long, Long> migratedBdsPlayerUniqueIds = Map.of();

        if (hasBdsPlayerStorage) {
            try {
                migratedBdsPlayerUniqueIds = PlayerMigrationExecutor.migrateBdsWorldPlayers(
                        storage,
                        serverDataDB,
                        uniqueIdAssigner
                );
            } catch (Exception e) {
                throw new IOException("Stage BDS player migration failed", e);
            }
        }

        boolean scoreboardMigrated;
        try {
            scoreboardMigrated = ScoreboardMigrationExecutor.migrateBdsWorldIfNeeded(
                    storage,
                    serverDataDB,
                    migratedBdsPlayerUniqueIds
            );
        } catch (Exception e) {
            throw new IOException("Stage BDS scoreboard migration failed", e);
        }

        if (hasBdsPlayerStorage) {
            try {
                PlayerMigrationExecutor.removeBdsWorldPlayerStorage(storage);
            } catch (Exception e) {
                throw new IOException("Stage BDS player storage cleanup failed", e);
            }
        }

        return scoreboardMigrated;
    }

    private void migrateDimension(LevelDBStorage storage, LevelProvider migrationProvider, DimensionData dimensionData, int generatorType, Map<UUID, Long> sharedMigratedActorUniqueIdsByUuid) throws IOException {
        DB db = storage.getDb();
        int dimensionId = dimensionData.getDimensionId();
        List<LevelDBMigrationChunkSerializer.ChunkCoordinate> chunks = LevelDBMigrationChunkSerializer.collectChunks(db, dimensionId);

        if (chunks.isEmpty()) {
            log.info("[LevelDB Migration] No chunks require migration in dimension {}", dimensionId);
            return;
        }

        log.info("[LevelDB Migration] Migrating {} chunks in dimension {} for {}", chunks.size(), dimensionId,
                LevelDBMigrationVersionStore.getMigrationSignature());

        int migrated = 0;
        int alreadyMigrated = 0;
        int actorOnlyChunks = 0;

        Map<LevelDBActorV3_1_0Migration.CreakingHeartPosition, Long> migratedCreakingLinks = new HashMap<>();
        Map<UUID, Long> migratedActorUniqueIdsByUuid =
                sharedMigratedActorUniqueIdsByUuid != null ? sharedMigratedActorUniqueIdsByUuid : new HashMap<>();
        List<LevelDBActorV3_1_0Migration.LegacyRidingLink> migratedRidingLinks = new ArrayList<>();
        Set<LevelDBBlockEntityV3_1_0Migration.Position> migratedNetherPortalBlocks = new HashSet<>();

        for (int i = 0; i < chunks.size(); i++) {
            LevelDBMigrationChunkSerializer.ChunkCoordinate coordinate = chunks.get(i);
            MigrationResult result;

            try {
                result = migrateChunk(storage, migrationProvider, dimensionData, generatorType, coordinate.x(), coordinate.z(),
                        migratedCreakingLinks, migratedActorUniqueIdsByUuid, migratedRidingLinks, migratedNetherPortalBlocks);
            } catch (Exception e) {
                throw new IOException("Failed to migrate LevelDB chunk [" + coordinate.x() + "," + coordinate.z() + "] in dimension " + dimensionId, e);
            }

            switch (result) {
                case MIGRATED -> migrated++;
                case ALREADY_MIGRATED -> alreadyMigrated++;
                case ACTOR_ONLY -> actorOnlyChunks++;
            }

            int processed = i + 1;

            if (processed % 100 == 0 || processed == chunks.size()) {
                log.info("[LevelDB Migration] Dimension {}: {}/{} chunks processed", dimensionId, processed, chunks.size());
            }
        }

        ActorMigrationExecutor.reconcileRidingLinks(storage, migratedActorUniqueIdsByUuid, migratedRidingLinks);
        ActorMigrationExecutor.reconcileCreakingHeartLinks(storage, dimensionData, migratedCreakingLinks);
        BlockEntityMigrationExecutor.reconcileNetherPortals(storage, dimensionData, migratedNetherPortalBlocks);

        log.info(
                "[LevelDB Migration] Dimension {} completed: {} migrated, {} already migrated, {} actor-only",
                dimensionId, migrated, alreadyMigrated, actorOnlyChunks
        );
    }

    private MigrationResult migrateChunk(
            LevelDBStorage storage,
            LevelProvider migrationProvider,
            DimensionData dimensionData,
            int generatorType,
            int chunkX,
            int chunkZ,
            Map<LevelDBActorV3_1_0Migration.CreakingHeartPosition, Long> migratedCreakingLinks,
            Map<UUID, Long> migratedActorUniqueIdsByUuid,
            List<LevelDBActorV3_1_0Migration.LegacyRidingLink> migratedRidingLinks,
            Set<LevelDBBlockEntityV3_1_0Migration.Position> migratedNetherPortalBlocks
    ) throws IOException {
        DB db = storage.getDb();
        byte[] extraDataKey = LevelDBKeyUtil.PNX_EXTRA_DATA.getKey(chunkX, chunkZ, dimensionData);
        byte[] extraDataBytes = db.get(extraDataKey);
        boolean pnxManagedStorage = extraDataBytes != null;

        if (!pnxManagedStorage) {
            ActorMigrationExecutor.reconcileNativeBdsActorDigest(storage, db, chunkX, chunkZ, dimensionData);

            byte[] versionValue = db.get(LevelDBKeyUtil.VERSION.getKey(chunkX, chunkZ, dimensionData));
            if (versionValue == null) {
                versionValue = db.get(LevelDBKeyUtil.LEGACY_VERSION.getKey(chunkX, chunkZ, dimensionData));
            }

            if (versionValue == null) {
                return MigrationResult.ACTOR_ONLY;
            }
        }

        CompoundTag extraData = LevelDBMigrationChunkSerializer.readBigEndianCompound(extraDataBytes);

        if (extraData == null) {
            extraData = new CompoundTag();
        }

        MigrationVersion chunkVersion = LevelDBMigrationVersionStore.getCurrentVersion(extraData, MigrationFormat.CHUNK);
        MigrationVersion blockEntityVersion = LevelDBMigrationVersionStore.getCurrentVersion(extraData, MigrationFormat.BLOCK_ENTITY);
        MigrationVersion actorVersion = LevelDBMigrationVersionStore.getCurrentVersion(extraData, MigrationFormat.ACTOR);

        boolean chunkPending = getStepsAfter(MigrationFormat.CHUNK, chunkVersion).size() != 0;
        boolean blockEntityPending = getStepsAfter(MigrationFormat.BLOCK_ENTITY, blockEntityVersion).size() != 0;
        boolean actorPending = getStepsAfter(MigrationFormat.ACTOR, actorVersion).size() != 0;
        boolean legacyVersionMarker = LevelDBMigrationVersionStore.hasLegacyVersion(extraData);

        if (!chunkPending && !blockEntityPending && !actorPending && !legacyVersionMarker) {
            return MigrationResult.ALREADY_MIGRATED;
        }

        IChunk chunk = null;
        if (chunkPending || blockEntityPending) {
            /*
             * Deserialize terrain only when a pending CHUNK or BLOCK_ENTITY
             * migration actually requires it.
             */
            chunk = LevelDBMigrationChunkSerializer.readChunk(
                    db,
                    chunkX,
                    chunkZ,
                    migrationProvider,
                    extraData,
                    pnxManagedStorage
            );

            if (chunk == null) {
                throw new IOException("Chunk [" + chunkX + "," + chunkZ + "] exists in LevelDB but could not be deserialized");
            }
        }

        try (WriteBatch batch = storage.createBatch()) {
            if (chunkPending) {
                extraData = ChunkMigrationExecutor.migrate(
                        dimensionData,
                        db,
                        storage,
                        batch,
                        chunk,
                        generatorType,
                        extraData,
                        pnxManagedStorage,
                        this,
                        chunkVersion
                );
                chunkVersion = getLatestVersion(MigrationFormat.CHUNK);
            }

            if (blockEntityPending) {
                BlockEntityMigrationExecutor.migrate(
                        dimensionData,
                        db,
                        batch,
                        chunk,
                        extraData,
                        migratedNetherPortalBlocks,
                        this,
                        blockEntityVersion
                );
                blockEntityVersion = getLatestVersion(MigrationFormat.BLOCK_ENTITY);
            }

            if (actorPending) {
                ActorMigrationExecutor.migrate(
                        db,
                        batch,
                        chunkX,
                        chunkZ,
                        dimensionData,
                        migratedCreakingLinks,
                        migratedActorUniqueIdsByUuid,
                        migratedRidingLinks,
                        this,
                        actorVersion
                );
                actorVersion = getLatestVersion(MigrationFormat.ACTOR);
            }

            LevelDBMigrationVersionStore.putCurrentVersion(extraData, MigrationFormat.CHUNK, chunkVersion);
            LevelDBMigrationVersionStore.putCurrentVersion(extraData, MigrationFormat.BLOCK_ENTITY, blockEntityVersion);
            LevelDBMigrationVersionStore.putCurrentVersion(extraData, MigrationFormat.ACTOR, actorVersion);
            LevelDBMigrationVersionStore.removeLegacyVersion(extraData);

            batch.put(extraDataKey, LevelDBMigrationChunkSerializer.writeBigEndianCompound(extraData));
            storage.writeBatch(batch);
        }

        return MigrationResult.MIGRATED;
    }

    private static LevelProvider createMigrationProvider(Path worldPath, LevelConfig.GeneratorConfig generatorConfig) {
        var dimensionData = generatorConfig.dimensionData();

        return (LevelProvider) Proxy.newProxyInstance(
                LevelProvider.class.getClassLoader(),
                new Class<?>[]{LevelProvider.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getDimensionData" -> dimensionData;
                    case "getPath" -> worldPath.toString();
                    case "getName" -> worldPath.getFileName().toString();
                    case "getSeed" -> generatorConfig.seed();
                    case "getCurrentTick", "getTime" -> 0L;
                    case "getLevel" -> null;
                    case "toString" -> "LevelDBMigrationProvider[" + worldPath.getFileName() + ":" + dimensionData.getDimensionId() + "]";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException("Migration-only LevelProvider does not support " + method.getName());
                }
        );
    }

    private static boolean areAllDimensionsMigrated(
            CompoundTag globalMarker,
            Set<Integer> expectedDimensions,
            String migrationSignature
    ) {
        for (int dimensionId : expectedDimensions) {
            if (!migrationSignature.equals(globalMarker.getString(getDimensionMigrationSignatureKey(dimensionId)))) {
                return false;
            }
        }

        return true;
    }

    private static String getDimensionMigrationSignatureKey(int dimensionId) {
        return DIMENSION_MIGRATION_SIGNATURE_KEY_PREFIX + dimensionId;
    }

    /**
     * Applies every registered migration step newer than the supplied current version.
     */
    @SuppressWarnings("unchecked")
    public <T> T apply(MigrationFormat format, MigrationVersion currentVersion, T value) throws IOException {
        T migrated = value;
        for (MigrationStep<?> step : getStepsAfter(format, currentVersion)) {
            migrated = ((MigrationStep<T>) step).migrate(this.context, migrated);
        }
        return migrated;
    }

    /**
     * Returns the registered migration steps for the specified format.
     */
    public List<MigrationStep<?>> getSteps(MigrationFormat format) {
        return MigrationSteps.getSteps(format);
    }

    /**
     * Returns the registered migration versions for the specified format.
     */
    public List<MigrationVersion> getVersions(MigrationFormat format) {
        return getSteps(format).stream()
                .map(MigrationStep::targetVersion)
                .toList();
    }

    /**
     * Returns the migration steps newer than the supplied current version.
     */
    public List<MigrationStep<?>> getStepsAfter(MigrationFormat format, MigrationVersion currentVersion) {
        return getSteps(format).stream()
                .filter(step -> currentVersion == null || step.targetVersion().compareTo(currentVersion) > 0)
                .toList();
    }

    /**
     * Returns the first registered migration version for the specified format.
     */
    public MigrationVersion getFirstVersion(MigrationFormat format) {
        return MigrationSteps.getFirstVersion(format);
    }

    /**
     * Returns the latest registered migration version for the specified format.
     */
    public MigrationVersion getLatestVersion(MigrationFormat format) {
        return MigrationSteps.getLatestVersion(format);
    }

    /**
     * Parses and validates a persisted migration version for the specified format.
     */
    public MigrationVersion parseStoredVersion(MigrationFormat format, String value) throws IOException {
        return MigrationSteps.parseStoredVersion(format, value);
    }

    /**
     * Identifies the result of migrating one stored LevelDB chunk.
     *
     * @author Curse
     */
    private enum MigrationResult {
        MIGRATED,
        ALREADY_MIGRATED,
        ACTOR_ONLY
    }
}
