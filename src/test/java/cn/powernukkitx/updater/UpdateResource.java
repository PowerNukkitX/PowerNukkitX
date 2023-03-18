package cn.powernukkitx.updater;

import cn.nukkit.inventory.ItemTag;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class UpdateResource {
    /**
     * Pre-requisites:<br>
     * - Make sure that ProxyPass is updated and working with the last Minecraft Bedrock Edition client<br>
     * - Make sure PocketMine has released their exports: https://github.com/pmmp/BedrockData<br>
     * - Run ProxyPass with export-data in config.yml set to true, the proxy pass must be<br>
     * pointing to a vanilla BDS server from https://www.minecraft.net/en-us/download/server/bedrock<br>
     * - Connect to the ProxyPass server with the last Minecraft Bedrock Edition client at least once<br>
     * - Adjust the path bellow if necessary for your machine<br>
     */
    public static void main(String[] args) {
        new UpdateResource().execute("../Bedrock-ProxyPass/run/data");
        System.out.println("OK");
    }

    private void execute(@SuppressWarnings("SameParameterValue") String pathProxyPassData) {
        downloadResources();
        copyProxyPassResources(pathProxyPassData);
        update();
    }

    private void downloadResources() {
        download("https://github.com/pmmp/BedrockData/raw/master/canonical_block_states.nbt",
                "src/main/resources/canonical_block_states.nbt");
        download("https://github.com/pmmp/BedrockData/raw/master/vanilla_recipes/potion_type.json",
                "src/main/resources/vanilla_recipes/potion_type.json");
        download("https://github.com/pmmp/BedrockData/raw/master/vanilla_recipes/shaped_crafting.json",
                "src/main/resources/vanilla_recipes/shaped_crafting.json");
        download("https://github.com/pmmp/BedrockData/raw/master/vanilla_recipes/shapeless_crafting.json",
                "src/main/resources/vanilla_recipes/shapeless_crafting.json");
        download("https://github.com/pmmp/BedrockData/raw/master/vanilla_recipes/smithing.json",
                "src/main/resources/vanilla_recipes/smithing.json");
        download("https://github.com/pmmp/BedrockData/raw/master/vanilla_recipes/shapeless_shulker_box.json",
                "src/main/resources/vanilla_recipes/shapeless_shulker_box.json");
        download("https://github.com/pmmp/BedrockData/raw/master/vanilla_recipes/smelting.json",
                "src/main/resources/vanilla_recipes/smelting.json");
        download("https://github.com/pmmp/BedrockData/raw/master/vanilla_recipes/special_hardcoded.json",
                "src/main/resources/vanilla_recipes/special_hardcoded.json");
        download("https://github.com/pmmp/BedrockData/raw/master/biome_definitions_full.nbt",
                "src/main/resources/biome_definitions_full.nbt");
        download("https://github.com/pmmp/BedrockData/raw/master/entity_identifiers.nbt",
                "src/main/resources/entity_identifiers.nbt");
        download("https://github.com/pmmp/BedrockData/raw/master/creativeitems.json",
                "src/main/resources/creativeitems.json");
        download("https://github.com/pmmp/BedrockData/raw/master/item_tags.json",
                "src/main/resources/tag_2_items.json");
        download("https://github.com/pmmp/BedrockData/raw/master/required_item_list.json",
                "src/test/resources/org/powernukkit/updater/dumps/pmmp/required_item_list.json");
    }

    private void copyProxyPassResources(String pathProxyPassData) {
        copy(pathProxyPassData, "runtime_item_states.json", "src/main/resources/runtime_item_states.json");
    }

    @SneakyThrows
    private void copy(String path, String file, String into) {
        Files.copy(Paths.get(path).resolve(file), Paths.get(into), StandardCopyOption.REPLACE_EXISTING);
    }

    @SneakyThrows
    private void download(String url, String into) {
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");
        try (InputStream input = connection.getInputStream();
             OutputStream fos = new FileOutputStream(into);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
        ) {
            byte[] buffer = new byte[8 * 1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SneakyThrows
    private void update() {
        //update item_2_tags.json
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
