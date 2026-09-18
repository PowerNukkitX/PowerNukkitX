package org.powernukkitx.level.structure;

import org.powernukkitx.Server;
import org.powernukkitx.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StructureAPI {
    private static final Map<String, Structure> structureCache = new HashMap<>();

    private static File resolvePathNamespaced(String name) {
        return resolveInsideStructureDir(name.replace(":", File.separator) + ".mcstructure");
    }

    private static File resolvePathRoot(String name) {
        return resolveInsideStructureDir(name + ".mcstructure");
    }

    private static File resolvePathWithFallback(String name) {
        File file = resolvePathNamespaced(name);
        if (file != null && file.exists()) {
            return file;
        }
        return resolvePathRoot(name);
    }

    @Nullable
    private static File resolveInsideStructureDir(String relativePath) {
        File root = new File(Server.getInstance().structurePath);
        File file = new File(root, relativePath);
        try {
            Path rootPath = root.getCanonicalFile().toPath();
            Path filePath = file.getCanonicalFile().toPath();
            if (!filePath.startsWith(rootPath) || filePath.equals(rootPath)) {
                log.warn("Rejected structure path outside of the structure directory: {}", relativePath);
                return null;
            }
            return file;
        } catch (IOException exception) {
            log.debug("Cannot resolve structure path {}", relativePath, exception);
            return null;
        }
    }

    public static Structure load(String name) {
        if (structureCache.containsKey(name)) {
            return structureCache.get(name);
        }

        File file = resolvePathWithFallback(name);
        if (file == null){
            return null;
        }

        try (var stream = new FileInputStream(file);
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
        try {
            File file = resolvePathNamespaced(name); // always save in namespace path
            if (file == null){
                return;
            }
            file.getParentFile().mkdirs();

            try (var stream = new FileOutputStream(file);
                 var nbtOutputStream = NbtUtils.createWriterLE(stream)) {
                nbtOutputStream.writeTag(structure.toNBT().toNetwork());
            }

            if (Server.getInstance().getSettings().gameplaySettings().cacheStructures()) {
                structureCache.put(name, structure);
            }
        } catch (Exception e) {
            log.error("Cannot save structure {}", name, e);
        }
    }

    public static boolean exists(String name) {
        File file = resolvePathWithFallback(name);
        return file != null && file.exists();
    }

    public static boolean delete(String name) {
        structureCache.remove(name);

        File file = resolvePathWithFallback(name);
        if (file == null){
            return false;
        }

        if (file.exists()) {
            return file.delete();
        }

        return true;
    }
}
