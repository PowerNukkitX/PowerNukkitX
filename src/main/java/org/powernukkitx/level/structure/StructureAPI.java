package org.powernukkitx.level.structure;

import org.powernukkitx.Server;
import org.powernukkitx.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StructureAPI {
    private static final Map<String, Structure> structureCache = new ConcurrentHashMap<>();

    private static boolean isNameSafe(String name) {
        if (name == null || name.isEmpty() || name.length() > 256) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            boolean allowed = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || c == '_' || c == '-' || c == '.' || c == ':';
            if (!allowed) {
                return false;
            }
        }
        return !name.contains("..");
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

    public static Structure load(String name) {
        Structure cached = structureCache.get(name);
        if (cached != null) {
            return cached;
        }

        File source = resolvePathWithFallback(name);
        if (source == null) {
            log.debug("Rejected structure name {}", name);
            return null;
        }

        try (var stream = new FileInputStream(source);
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
            if (file == null) {
                log.warn("Rejected structure name {}", name);
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
        File namespaced = resolvePathNamespaced(name);
        if (namespaced != null && namespaced.exists()) {
            return true;
        }
        File root = resolvePathRoot(name);
        return root != null && root.exists();
    }

    public static boolean delete(String name) {
        structureCache.remove(name);

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
