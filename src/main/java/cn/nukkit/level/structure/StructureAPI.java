package cn.nukkit.level.structure;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteOrder;

@Slf4j
public class StructureAPI {
    public static Structure load(String name) {
        try (var stream = new FileInputStream(new File(Server.getInstance().structurePath + name + ".mcstructure"))) {
            CompoundTag root = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);

            return Structure.fromNbtAsync(root).join();
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
        } catch (Exception e) {
            log.error("Cannot save structure " + name);
            log.debug("Cannot save structure " + name, e);
        }
    }

    public static boolean exists(String name) {
        return new File(Server.getInstance().structurePath + name + ".mcstructure").exists();
    }

    public static boolean delete(String name) {
        File file = new File(Server.getInstance().structurePath + name + ".mcstructure");
        if (file.exists()) {
            return file.delete();
        }

        return true;
    }
}
