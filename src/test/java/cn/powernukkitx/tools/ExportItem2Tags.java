package cn.powernukkitx.tools;

import cn.nukkit.inventory.ItemTag;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * item_2_tags from https://github.com/pmmp/BedrockData
 */
public class ExportItem2Tags {
    public static void main(String[] args) throws IOException {
        var gson = new Gson();
        Map<String, Set<String>> test = new LinkedHashMap<>();
        for (var entry : ItemTag.getTag2Items().entrySet()) {
            for (var v : entry.getValue()) {
                test.computeIfAbsent(v, k -> new LinkedHashSet<>());
                test.get(v).add(entry.getKey());
            }
        }
        Files.writeString(Path.of("./src/main/resources/item_2_tags.json"), gson.toJson(test), StandardCharsets.UTF_8);
    }
}
