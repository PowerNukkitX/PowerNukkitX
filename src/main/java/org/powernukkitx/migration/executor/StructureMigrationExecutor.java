package org.powernukkitx.migration.executor;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationService;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.StructureMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Orchestrates structure migrations between LevelDB, legacy structure files and the canonical global structure storage.
 *
 * @author Curse
 */
@Slf4j
public final class StructureMigrationExecutor {
    private static final Path STRUCTURE_PATH = Path.of("structures");
    private static final Path WORLDS_PATH = Path.of("worlds");
    private static final String VERSION_FILE = ".pnx_structure_storage";
    private static final String LEVELDB_STRUCTURE_KEY_PREFIX = "structuretemplate_";

    private StructureMigrationExecutor() {}

    private static void migrateLevelDBStructures(Path worldsPath, Path structurePath) throws IOException {
        if (!Files.isDirectory(worldsPath)) return;

        List<Path> worlds;
        try (Stream<Path> paths = Files.list(worldsPath)) {
            worlds = paths.filter(Files::isDirectory).sorted().toList();
        }

        for (Path worldPath : worlds) {
            if (!LevelDBProvider.isValid(worldPath.toString())) continue;

            LevelDBStorage storage = new LevelDBStorage(1, worldPath.toString());
            try {
                migrateLevelDBStructures(structurePath, storage);
            } finally {
                storage.close();
            }
        }
    }

    private static void migrateLevelDBStructures(Path structurePath, LevelDBStorage storage) throws IOException {
        List<LevelDBStructureTemplate> structures = readLevelDBStructures(storage);
        if (structures.size() == 0) return;

        int migrated = 0;
        for (LevelDBStructureTemplate structure : structures) {
            Path path = structurePath.resolve(structure.identifier().replace(":", File.separator) + ".mcstructure");
            if (Files.isRegularFile(path)) continue;
            if (Files.exists(path)) throw new IOException("Structure target is not a regular file: " + path);
            writeRawStructure(path, structure.data());
            migrated++;
        }

        try (var batch = storage.createBatch()) {
            for (LevelDBStructureTemplate structure : structures) {
                batch.delete(structure.key());
            }
            storage.writeBatch(batch);
        }

        log.info("[Structure Migration] Migrated {} BDS LevelDB structure templates; preserved {} existing global structures",
                migrated, structures.size() - migrated);
    }

    private static List<LevelDBStructureTemplate> readLevelDBStructures(LevelDBStorage storage) throws IOException {
        List<LevelDBStructureTemplate> structures = new ArrayList<>();
        byte[] prefix = LEVELDB_STRUCTURE_KEY_PREFIX.getBytes(StandardCharsets.UTF_8);
        try (var iterator = storage.getDb().iterator()) {
            for (iterator.seek(prefix); iterator.hasNext(); iterator.next()) {
                var entry = iterator.peekNext();
                String key = new String(entry.getKey(), StandardCharsets.UTF_8);
                if (!key.startsWith(LEVELDB_STRUCTURE_KEY_PREFIX)) break;

                String identifier = key.substring(LEVELDB_STRUCTURE_KEY_PREFIX.length());
                structures.add(new LevelDBStructureTemplate(identifier, entry.getKey().clone(), entry.getValue().clone()));
            }
        }
        return structures;
    }

    private static void writeRawStructure(Path path, byte[] data) throws IOException {
        Files.createDirectories(path.getParent());
        Path temporary = path.resolveSibling(path.getFileName() + ".migrating");
        try {
            Files.write(temporary, data);
            replaceFile(temporary, path);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * Migrates global structure storage and imports native BDS structure templates.
     */
    public static void migrateIfNeeded(Path dataPath, MigrationService migrationService) throws IOException {
        Path structurePath = dataPath.resolve(STRUCTURE_PATH);
        migrateStructureFilesIfNeeded(structurePath, migrationService);
        migrateLevelDBStructures(dataPath.resolve(WORLDS_PATH), structurePath);
    }

    private static void migrateStructureFilesIfNeeded(Path structurePath, MigrationService migrationService) throws IOException {
        Files.createDirectories(structurePath);
        Path versionFile = structurePath.resolve(VERSION_FILE);
        MigrationVersion targetVersion = migrationService.getLatestVersion(MigrationFormat.STRUCTURE);
        MigrationVersion currentVersion = readMigrationVersion(versionFile, migrationService);

        if (currentVersion != null && currentVersion.compareTo(targetVersion) >= 0) {
            return;
        }

        List<Path> structureFiles;
        try (Stream<Path> paths = Files.walk(structurePath)) {
            structureFiles =
                    paths.filter(Files::isRegularFile)
                            .filter(path -> path.getFileName().toString().endsWith(".mcstructure"))
                            .sorted()
                            .toList();
        }

        int migrated = 0;
        for (Path structureFile : structureFiles) {
            CompoundTag root = readStructure(structureFile);
            StructureMigrationData result = migrationService.apply(
                    MigrationFormat.STRUCTURE,
                    currentVersion,
                    new StructureMigrationData(root, structureFile)
            );
            writeStructure(structureFile, result.root());
            migrated++;
        }

        writeVersion(versionFile, targetVersion);
        log.info(
                "[Structure Migration] Structure storage migration from {} to {} completed: {} files processed",
                currentVersion == null ? "legacy" : currentVersion.toString(),
                targetVersion,
                migrated);
    }

    private static CompoundTag readStructure(Path path) throws IOException {
        try (var stream = Files.newInputStream(path);
                var input = NbtUtils.createReaderLE(stream)) {
            Object tag = input.readTag();
            if (!(tag instanceof NbtMap map)) {
                throw new IOException("Invalid structure root NBT in " + path);
            }

            return CompoundTag.fromNetwork(map);
        }
    }

    private static void writeStructure(Path path, CompoundTag root) throws IOException {
        Path temporary = path.resolveSibling(path.getFileName() + ".migrating");
        try {
            try (var stream = Files.newOutputStream(temporary);
                    var output = NbtUtils.createWriterLE(stream)) {
                output.writeTag(root.toNetwork());
            }

            replaceFile(temporary, path);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static MigrationVersion readMigrationVersion(Path versionFile, MigrationService migrationService) throws IOException {
        if (!Files.isRegularFile(versionFile)) {
            return null;
        }

        String value = Files.readString(versionFile, StandardCharsets.UTF_8).trim();
        if ("1".equals(value)) {
            return migrationService.getFirstVersion(MigrationFormat.STRUCTURE);
        }

        return migrationService.parseStoredVersion(MigrationFormat.STRUCTURE, value);
    }

    private static void writeVersion(Path versionFile, MigrationVersion version) throws IOException {
        Path temporary = versionFile.resolveSibling(versionFile.getFileName() + ".tmp");
        try {
            Files.writeString(
                    temporary, version + System.lineSeparator(), StandardCharsets.UTF_8);
            replaceFile(temporary, versionFile);
        } finally {
            Files.deleteIfExists(temporary);
        }

        try {
            Files.setAttribute(versionFile, "dos:hidden", true);
        } catch (Exception ignored) {
        }
    }

    private static void replaceFile(Path source, Path target) throws IOException {
        try {
            Files.move(
                    source,
                    target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Represents one native BDS structure template loaded from LevelDB.
     */
    private record LevelDBStructureTemplate(String identifier, byte[] key, byte[] data) {}
}
