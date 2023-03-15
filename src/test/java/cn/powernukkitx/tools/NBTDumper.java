package cn.powernukkitx.tools;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class NBTDumper {
    public static void main(String[] args) {
        var file = new File("src/main/resources/entity_identifiers.dat");
        try (InputStream stream = new FileInputStream(file)) {
            //noinspection ConstantConditions
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            try (BufferedInputStream bis = new BufferedInputStream(stream)) {
                while (bis.available() > 0) {
                    CompoundTag tag = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true);
                    Files.writeString(Path.of("target/" + file.getName() + ".txt"), tag + "\n\n", StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
