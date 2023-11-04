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
 * PowerNukkitX Project 2023/7/12
 *
 * @author daoge_cmd
 */
public class BlockPropertyTypeFileCutter {

    static final String BLOCK_PROPERTY_TYPES_FILE_PATH = "cn/powernukkitx/block/block_property_types_allay.json";
    static final Path OUTPUT = Path.of("./src/main/resources/block_property_types.json");

    @SneakyThrows
    public static void main(String[] args) {
        var reader = new InputStreamReader(new BufferedInputStream(Objects.requireNonNull(BlockAttributesTest.class.getClassLoader().getResourceAsStream(BLOCK_PROPERTY_TYPES_FILE_PATH))));
        var parser = JsonParser.parseReader(reader);
        var output = new HashMap<String, String>();
        for (var entry : parser.getAsJsonObject().getAsJsonObject("propertyTypes").entrySet()) {
            var obj = entry.getValue().getAsJsonObject();
            output.put(
                    obj.get("serializationName").getAsString(),
                    obj.get("valueType").getAsString()
            );
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!Files.exists(OUTPUT))
            Files.createFile(OUTPUT);
        Files.writeString(OUTPUT, gson.toJson(output));
    }
}
