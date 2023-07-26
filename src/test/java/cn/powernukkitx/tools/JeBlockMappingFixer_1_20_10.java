package cn.powernukkitx.tools;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.SneakyThrows;

/**
 * PowerNukkitX Project 2023/7/14
 *
 * @author daoge_cmd
 *
 * 修复潜影盒和混凝土的mapping问题
 */
public class JeBlockMappingFixer_1_20_10 {

    static Path MAPPING = Path.of("src/main/resources/jeMappings/jeBlocksMapping.json");

    @SneakyThrows
    public static void main(String[] args) {
        var global = JsonParser.parseReader(Files.newBufferedReader(MAPPING)).getAsJsonObject();
        var copy = global.getAsJsonObject().deepCopy();
        global.entrySet().forEach(entry -> {
            var javaName = entry.getKey();
            if (javaName.contains("concrete") && !javaName.contains("_powder")) {
                var obj = entry.getValue().getAsJsonObject();
                obj.remove("bedrock_states");
                obj.addProperty("bedrock_identifier", javaName);
                copy.add(javaName, obj);
                return;
            }
            if (javaName.contains("shulker_box") && !javaName.startsWith("minecraft:shulker_box")) {
                var obj = entry.getValue().getAsJsonObject();
                var split = javaName.split("\\[")[0];
                obj.remove("bedrock_states");
                obj.addProperty("bedrock_identifier", split);
                copy.add(javaName, obj);
            }
        });
        var writer = Files.newBufferedWriter(MAPPING);
        new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(copy, writer);
        writer.flush();
        writer.close();
    }
}
