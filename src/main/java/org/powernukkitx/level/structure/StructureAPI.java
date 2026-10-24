package org.powernukkitx.level.structure;

import org.powernukkitx.Server;
import org.powernukkitx.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StructureAPI {
    private static final Map<String, Structure> structureCache = new ConcurrentHashMap<>();
    private static final Map<String, Structure> memoryStructures = new ConcurrentHashMap<>();
    private static final Map<String, Long> templateRevisions = new ConcurrentHashMap<>();

    /** Returns the current revision of a named template. */
    public static long getTemplateRevision(String name) {
        return templateRevisions.getOrDefault(name, 0L);
    }

    private static void templateChanged(String name) {
        templateRevisions.merge(name, 1L, Long::sum);
    }


    private static boolean isNameSafe(String name) {
        if (name == null || name.isEmpty() || name.length() > 256) return false;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            boolean allowed = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || c == '_' || c == '-' || c == '.' || c == ':' || c == '/';
            if (!allowed) return false;
        }

        if (name.contains("..") || name.contains("//") || name.startsWith("/") || name.endsWith("/")) return false;

        int separator = name.indexOf(':');
        return separator < 0 || separator > 0 && separator < name.length() - 1
                && name.lastIndexOf(':') == separator && name.charAt(separator + 1) != '/';
    }

    /**
     * Validates a global structure identifier.
     *
     * @param name structure identifier
     * @return whether the identifier is valid
     */
    public static boolean isValidName(String name) {
        return isNameSafe(name);
    }

    private static File confine(File candidate) {
        try {
            Path root = new File(Server.getInstance().structurePath).getCanonicalFile().toPath();
            Path resolved = candidate.getCanonicalFile().toPath();
            if (!resolved.startsWith(root)) {
                return null;
            }
            return resolved.toFile();
        } catch (IOException e) {
            return null;
        }
    }

    private static File resolvePathNamespaced(String name) {
        if (!isNameSafe(name)) {
            return null;
        }
        String relativePath = name.replace(":", File.separator) + ".mcstructure";
        return confine(new File(Server.getInstance().structurePath, relativePath));
    }

    private static File resolvePathRoot(String name) {
        if (!isNameSafe(name)) {
            return null;
        }
        return confine(new File(Server.getInstance().structurePath, name + ".mcstructure"));
    }

    private static File resolvePathWithFallback(String name) {
        File file = resolvePathNamespaced(name);
        if (file != null && file.exists()) {
            return file;
        }
        return resolvePathRoot(name);
    }

    /**
     * Saves a temporary structure accessible from every server world.
     *
     * @param structure structure to save
     * @param name global structure name
     */
    public static void saveMemory(Structure structure, String name) {
        if (!isNameSafe(name)) {
            log.warn("Rejected structure name {}", name);
            return;
        }
        memoryStructures.put(name, structure);
        templateChanged(name);
    }


    /**
     * Same as {@link #load(String)}, but reads the file on the compute thread pool.
     * Completes immediately when the structure is already cached.
     *
     * @param name structure name, either namespaced ({@code ns:name}) or relative to the structure directory
     * @return future completing with the structure, or with {@code null} if it is missing, unreadable
     * or resolves outside the structure directory
     */
    public static CompletableFuture<Structure> loadAsync(String name) {
        Structure memory = memoryStructures.get(name);
        if (memory != null) return CompletableFuture.completedFuture(memory);

        Structure cached = structureCache.get(name);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        return CompletableFuture.supplyAsync(() -> load(name), Server.getInstance().getComputeThreadPool());
    }

    public static Structure load(String name) {
        Structure memory = memoryStructures.get(name);
        if (memory != null) return memory;

        Structure cached = structureCache.get(name);
        if (cached != null) {
            return cached;
        }

        File source = resolvePathWithFallback(name);
        if (source == null) {
            log.debug("Rejected structure name {}", name);
            return null;
        }

        try (var stream = Files.newInputStream(source.toPath());
             var nbtInputStream = NbtUtils.createReaderLE(stream)) {
            NbtMap root = (NbtMap) nbtInputStream.readTag();

            Structure structure = Structure.fromNbtAsync(CompoundTag.fromNetwork(root)).join();

            if (Server.getInstance().getSettings().gameplaySettings().cacheStructures()) {
                structureCache.put(name, structure);
            }

            return structure;
        } catch (Exception e) {
            log.debug("Cannot load structure {}", name, e);
            return null;
        }
    }

    public static void save(Structure structure, String name) {
        saveDisk(structure, name);
    }

    /**
     * Saves a structure to persistent global storage.
     *
     * @param structure structure to save
     * @param name global structure identifier
     * @return whether the file was written successfully
     */
    public static boolean saveDisk(Structure structure, String name) {
        try {
            File file = resolvePathNamespaced(name);
            if (file == null) {
                log.warn("Rejected structure name {}", name);
                return false;
            }
            Files.createDirectories(file.getParentFile().toPath());

            Path target = file.toPath();
            Path temporary = Files.createTempFile(target.getParent(), file.getName(), ".saving");
            try {
                try (var stream = Files.newOutputStream(temporary);
                     var nbtOutputStream = NbtUtils.createWriterLE(stream)) {
                    nbtOutputStream.writeTag(structure.toNBT().toNetwork());
                }

                try {
                    Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporary);
            }

            memoryStructures.remove(name);
            structureCache.remove(name);
            templateChanged(name);
            return true;
        } catch (Exception e) {
            log.error("Cannot save structure {}", name, e);
            return false;
        }
    }

    public static boolean exists(String name) {
        if (memoryStructures.containsKey(name)) return true;

        File namespaced = resolvePathNamespaced(name);
        if (namespaced != null && namespaced.exists()) {
            return true;
        }
        File root = resolvePathRoot(name);
        return root != null && root.exists();
    }

    public static boolean delete(String name) {
        structureCache.remove(name);
        memoryStructures.remove(name);
        templateChanged(name);

        File file = resolvePathNamespaced(name);
        if (file == null || !file.exists()) {
            file = resolvePathRoot(name);
        }

        if (file == null) {
            return false;
        }

        if (file.exists()) {
            return file.delete();
        }

        return true;
    }
}
