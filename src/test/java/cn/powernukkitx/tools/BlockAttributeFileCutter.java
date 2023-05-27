package cn.powernukkitx.tools;

import cn.nukkit.block.BlockAttributesTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
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

    static final String BLOCK_ATTRIBUTES_REFERENCE_FILE_PATH = "cn/powernukkitx/block/block_attributes.json";
    static final Path OUTPUT = Path.of("./src/main/resources/block_color.json");

    @SneakyThrows
    public static void main(String[] args) {
        var reader = new InputStreamReader(new BufferedInputStream(Objects.requireNonNull(BlockAttributesTest.class.getClassLoader().getResourceAsStream(BLOCK_ATTRIBUTES_REFERENCE_FILE_PATH))));
        var parser = JsonParser.parseReader(reader);
        var ext = new HashMap<Long, Color>();
        for (var jsonElement : parser.getAsJsonArray()) {
            var obj = jsonElement.getAsJsonObject();
            var hash = obj.get("blockStateHash").getAsLong();
            var colorObj = obj.get("color").getAsJsonObject();
            var r = colorObj.get("r").getAsInt();
            var g = colorObj.get("g").getAsInt();
            var b = colorObj.get("b").getAsInt();
            var a = colorObj.get("a").getAsInt();
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
