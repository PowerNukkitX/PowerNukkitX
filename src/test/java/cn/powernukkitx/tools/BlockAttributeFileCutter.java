package cn.powernukkitx.tools;

import cn.nukkit.block.BlockAttributesTest;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

/**
 * Author: daoge_cmd <br>
 * Date: 2023/5/27 <br>
 * PowerNukkitX Project <br>
 */
public class BlockAttributeFileCutter {

    static final String BLOCK_ATTRIBUTES_REFERENCE_FILE_PATH = "cn/powernukkitx/block/block_attributes_allay.nbt";
    static final Path OUTPUT = Path.of("./src/main/resources/block_color.json");

    @SneakyThrows
    public static void main(String[] args) {
        var nbt = NBTIO.readCompressed(new BufferedInputStream(
                Objects.requireNonNull(
                        BlockAttributesTest.class.getClassLoader().getResourceAsStream(BLOCK_ATTRIBUTES_REFERENCE_FILE_PATH),
                        "block_attributes.nbt is missing!"
                )
        ));
        var ext = new HashMap<Integer, Color>();
        for (var block : nbt.getList("block", CompoundTag.class).getAll()) {
            var hash = block.getInt("blockStateHash");
            var color = block.getCompound("color");
            var r = color.getInt("r");
            var g = color.getInt("g");
            var b = color.getInt("b");
            var a = block.getFloat("translucency") >= 1 ? 0 : 255;
            ext.put(hash, new Color(r, g, b, a));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!Files.exists(OUTPUT))
            Files.createFile(OUTPUT);
        Files.writeString(OUTPUT, gson.toJson(ext));
    }

    record Color(int r, int g, int b, int a) {
    }
}
