package cn.nukkit.level.structure;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StructureAPI {
    private static Map<String, Structure> structureCache = new HashMap<>();

    public static Structure load(String name) {
        if (structureCache.containsKey(name)) {
            return structureCache.get(name);
        }

        try (var stream = new FileInputStream(new File(Server.getInstance().structurePath + name + ".mcstructure"))) {
            CompoundTag root = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);

            Structure structure = Structure.fromNbtAsync(root).join();

            if(Server.getInstance().getSettings().gameplaySettings().cacheStructures())
                structureCache.put(name, structure);

            return structure;
        } catch (Exception e) {
            log.debug("Cannot load structure " + name);
            return null;
        }
    }

    public static void save(Structure structure, String name) {
        try {
            File file = new File(Server.getInstance().structurePath + name + ".mcstructure");
            file.getParentFile().mkdirs();
            NBTIO.write(structure.toNBT(), file, ByteOrder.LITTLE_ENDIAN);

            if(Server.getInstance().getSettings().gameplaySettings().cacheStructures())
                structureCache.put(name, structure);
        } catch (Exception e) {
            log.error("Cannot save structure " + name);
            log.debug("Cannot save structure " + name, e);
        }
    }

    public static boolean exists(String name) {
        return new File(Server.getInstance().structurePath + name + ".mcstructure").exists();
    }

    public static boolean delete(String name) {
        structureCache.remove(name);

        File file = new File(Server.getInstance().structurePath + name + ".mcstructure");
        if (file.exists()) {
            return file.delete();
        }

        return true;
    }
}
