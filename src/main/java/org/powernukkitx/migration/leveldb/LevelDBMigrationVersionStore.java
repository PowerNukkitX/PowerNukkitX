package org.powernukkitx.migration.leveldb;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.powernukkitx.ServerDBStorageFormat;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationSteps;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Reads and writes migration versions and completion signatures stored in LevelDB chunk and world metadata.
 *
 * @author Curse
 */
public final class LevelDBMigrationVersionStore {
    private static final String ROOT = "migrationVersions";
    private static final String MIGRATION_SIGNATURE_KEY = "migrationSignature";
    private static final String DIMENSION_MIGRATION_SIGNATURE_KEY_PREFIX = "migrationSignatureDimension_";
    private static final byte[] GLOBAL_STORAGE_VERSION_KEY = LevelDBKeyUtil.PNX_EXTRA_DATA.getGlobalKey();
    private static final byte[] WORLD_STORAGE_VERSION_KEY = "\0pnx_migration:world_storage:storage_version".getBytes(StandardCharsets.UTF_8);
    private static final List<MigrationFormat> CHUNK_FORMATS = List.of(
            MigrationFormat.CHUNK,
            MigrationFormat.BLOCK_ENTITY,
            MigrationFormat.ACTOR
    );
    private static final List<MigrationFormat> WORLD_FORMATS = List.of(
            MigrationFormat.WORLD_STORAGE,
            MigrationFormat.CHUNK,
            MigrationFormat.BLOCK_ENTITY,
            MigrationFormat.ACTOR,
            MigrationFormat.POSITION_TRACKING,
            MigrationFormat.STRUCTURE
    );

    private LevelDBMigrationVersionStore() {
    }

    /**
     * Reads the world migration completion marker.
     */
    public static CompoundTag readGlobalMarker(DB db) {
        return LevelDBMigrationChunkSerializer.readBigEndianCompound(db.get(GLOBAL_STORAGE_VERSION_KEY));
    }

    /**
     * Writes the world migration completion marker.
     */
    public static void writeGlobalMarker(LevelDBStorage storage, CompoundTag globalMarker) throws IOException {
        try (WriteBatch batch = storage.createBatch()) {
            batch.put(GLOBAL_STORAGE_VERSION_KEY, LevelDBMigrationChunkSerializer.writeBigEndianCompound(globalMarker));
            storage.writeBatch(batch);
        }
    }

    /**
     * Writes current migration metadata for a newly generated world.
     */
    public static void writeGeneratedWorldVersions(LevelDBStorage storage, Set<Integer> dimensionIds) throws IOException {
        synchronized (storage) {
            CompoundTag globalMarker = readGlobalMarker(storage.getDb());
            if (globalMarker == null) {
                globalMarker = new CompoundTag();
            }

            String dimensionSignature = getMigrationSignature();
            for (int dimensionId : dimensionIds.stream().sorted().toList()) {
                globalMarker.putString(DIMENSION_MIGRATION_SIGNATURE_KEY_PREFIX + dimensionId, dimensionSignature);
            }
            globalMarker.putString(MIGRATION_SIGNATURE_KEY, getWorldMigrationSignature());

            MigrationVersion worldStorageVersion = MigrationSteps.getLatestVersion(MigrationFormat.WORLD_STORAGE);
            try (WriteBatch batch = storage.createBatch()) {
                batch.put(WORLD_STORAGE_VERSION_KEY, worldStorageVersion.toString().getBytes(StandardCharsets.UTF_8));
                batch.put(GLOBAL_STORAGE_VERSION_KEY, LevelDBMigrationChunkSerializer.writeBigEndianCompound(globalMarker));
                storage.writeBatch(batch);
            }
        }
    }

    /**
     * Returns the persisted migration version for a chunk format.
     */
    public static MigrationVersion getCurrentVersion(CompoundTag extraData, MigrationFormat format) throws IOException {
        requireChunkFormat(format);

        MigrationVersion version = null;
        if (extraData.contains(ROOT)) {
            if (!extraData.containsCompound(ROOT)) {
                throw new IOException("Invalid LevelDB migrationVersions tag");
            }

            CompoundTag versions = extraData.getCompound(ROOT);
            String key = format.serializedName();

            if (versions.contains(key)) {
                if (!versions.containsString(key)) {
                    throw new IOException("Invalid " + format + " migration version tag");
                }

                version = MigrationSteps.parseStoredVersion(format, versions.getString(key));
            }
        }

        if (version == null && extraData.contains(ServerDBStorageFormat.LEVELDB_VERSION_KEY)) {
            if (!extraData.containsString(ServerDBStorageFormat.LEVELDB_VERSION_KEY)) {
                throw new IOException("Invalid legacy LevelDB storage version tag");
            }

            String legacyVersion = extraData.getString(ServerDBStorageFormat.LEVELDB_VERSION_KEY);
            if (!legacyVersion.isEmpty()) {
                version = MigrationSteps.parseStoredVersion(format, legacyVersion);
            }
        }

        return version;
    }

    /**
     * Stores the migrated version for a chunk format.
     */
    public static void putCurrentVersion(CompoundTag extraData, MigrationFormat format, MigrationVersion version) throws IOException {
        requireChunkFormat(format);
        if (version == null) return;

        CompoundTag versions;
        if (extraData.contains(ROOT)) {
            if (!extraData.containsCompound(ROOT)) {
                throw new IOException("Invalid LevelDB migrationVersions tag");
            }
            versions = extraData.getCompound(ROOT);
        } else {
            versions = new CompoundTag();
        }

        versions.putString(format.serializedName(), version.toString());
        extraData.putCompound(ROOT, versions);
    }

    /**
     * Writes the current runtime chunk migration versions.
     */
    public static void writeRuntimeVersions(CompoundTag extraData) {
        CompoundTag versions;
        if (extraData.contains(ROOT)) {
            if (!extraData.containsCompound(ROOT)) {
                throw new IllegalStateException("Invalid LevelDB migrationVersions tag");
            }
            versions = extraData.getCompound(ROOT);
        } else {
            versions = new CompoundTag();
        }

        for (MigrationFormat format : CHUNK_FORMATS) {
            MigrationVersion version = MigrationSteps.getLatestVersion(format);
            versions.putString(format.serializedName(), version.toString());
        }

        extraData.putCompound(ROOT, versions);
        removeLegacyVersion(extraData);
    }

    /**
     * Returns whether all runtime chunk migration formats are current.
     */
    public static boolean isRuntimeCurrent(CompoundTag extraData) throws IOException {
        for (MigrationFormat format : CHUNK_FORMATS) {
            if (!MigrationSteps.getLatestVersion(format).equals(getCurrentVersion(extraData, format))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether chunk extra data still contains the legacy migration version marker.
     */
    public static boolean hasLegacyVersion(CompoundTag extraData) {
        return extraData.contains(ServerDBStorageFormat.LEVELDB_VERSION_KEY);
    }

    /**
     * Removes the legacy chunk migration version marker.
     */
    public static void removeLegacyVersion(CompoundTag extraData) {
        extraData.remove(ServerDBStorageFormat.LEVELDB_VERSION_KEY);
    }

    /**
     * Returns the migration signature for chunk-scoped formats.
     */
    public static String getMigrationSignature() {
        return createMigrationSignature(CHUNK_FORMATS);
    }

    /**
     * Returns the migration signature for world-scoped formats.
     */
    public static String getWorldMigrationSignature() {
        return createMigrationSignature(WORLD_FORMATS);
    }

    private static String createMigrationSignature(List<MigrationFormat> formats) {
        StringJoiner signature = new StringJoiner(";");

        for (MigrationFormat format : formats) {
            signature.add(format.serializedName() + "=" + MigrationSteps.getLatestVersion(format));
        }

        return signature.toString();
    }

    private static void requireChunkFormat(MigrationFormat format) {
        if (!CHUNK_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Unsupported chunk migration format: " + format);
        }
    }
}
