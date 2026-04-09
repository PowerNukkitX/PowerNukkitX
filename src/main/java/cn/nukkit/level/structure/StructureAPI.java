package cn.nukkit.level.structure;

import cn.nukkit.Server;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StructureAPI {
    private static final Map<String, Structure> structureCache = new HashMap<>();

    private static File resolvePathNamespaced(String name) {
        String relativePath = name.replace(":", File.separator) + ".mcstructure";
        return new File(Server.getInstance().structurePath, relativePath);
    }

    private static File resolvePathRoot(String name) {
        return new File(Server.getInstance().structurePath, name + ".mcstructure");
    }

    private static File resolvePathWithFallback(String name) {
        File file = resolvePathNamespaced(name);
        if (file.exists()) {
            return file;
        }
        return resolvePathRoot(name);
    }

    public static Structure load(String name) {
        if (structureCache.containsKey(name)) {
            return structureCache.get(name);
        }

        try (var stream = new FileInputStream(resolvePathWithFallback(name));
             var nbtInputStream = NbtUtils.createReaderLE(stream)) {
            NbtMap root = (NbtMap) nbtInputStream.readTag();

            Structure structure = Structure.fromNbtAsync(root).join();

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
            file.getParentFile().mkdirs();

            try (var stream = new FileOutputStream(file);
                 var nbtOutputStream = NbtUtils.createWriterLE(stream)) {
                nbtOutputStream.writeTag(structure.toNBT());
            }

            if (Server.getInstance().getSettings().gameplaySettings().cacheStructures()) {
                structureCache.put(name, structure);
            }
        } catch (Exception e) {
            log.error("Cannot save structure {}", name, e);
        }
    }

    public static boolean exists(String name) {
        return resolvePathNamespaced(name).exists() || resolvePathRoot(name).exists();
    }

    public static boolean delete(String name) {
        structureCache.remove(name);

        File file = resolvePathNamespaced(name);
        if (!file.exists()) {
            file = resolvePathRoot(name);
        }

        if (file.exists()) {
            return file.delete();
        }

        return true;
    }
}
