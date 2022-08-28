package cn.powernukkitx.tools;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class NBTDumper {
    public static void main(String[] args) {
        try (InputStream stream = new FileInputStream("src/main/resources/canonical_block_states.nbt")) {
            //noinspection ConstantConditions
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            try (BufferedInputStream bis = new BufferedInputStream(stream)) {
                while (bis.available() > 0) {
                    CompoundTag tag = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true);
                    Files.writeString(Path.of("target/canonical_block_states.snbt"), tag + "\n\n", StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
