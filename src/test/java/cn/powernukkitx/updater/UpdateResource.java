package cn.powernukkitx.updater;

import cn.nukkit.inventory.ItemTag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@SuppressWarnings("unchecked")
public class UpdateResource {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Pre-requisites:<br>
     * - Make sure PocketMine has released their exports: <a href="https://github.com/pmmp/BedrockData">BedrockData</a><br>
     * - Make sure Mojang has released their addon samples: <a href="https://github.com/Mojang/bedrock-samples">bedrock-samples</a><br>
     */
    public static void main(String[] args) {
        new UpdateResource().execute();
        System.out.println("OK");
    }

    @SneakyThrows
    private void execute() {
//        downloadResources();
//        updateItem2Tags(Path.of("src/main/resources/item_2_tags.json"));
//        convertRequiredItemList(Path.of("src/main/resources/runtime_item_states.json"));
        updateRecipes(Path.of("target/bedrock-samples"), Path.of("src/main/resources/vanilla_recipes"));
        System.exit(0);
    }

    private void downloadResources() {
        download("https://github.com/pmmp/BedrockData/raw/master/canonical_block_states.nbt",
                "src/main/resources/canonical_block_states.nbt");
        download("https://github.com/pmmp/BedrockData/raw/master/recipes/potion_type.json",
                "src/main/resources/vanilla_recipes/potion_type.json");
        download("https://github.com/pmmp/BedrockData/raw/master/recipes/shaped_crafting.json",
                "src/main/resources/vanilla_recipes/shaped_crafting.json");
        download("https://github.com/pmmp/BedrockData/raw/master/recipes/shapeless_crafting.json",
                "src/main/resources/vanilla_recipes/shapeless_crafting.json");
        download("https://github.com/pmmp/BedrockData/raw/master/recipes/smithing.json",
                "src/main/resources/vanilla_recipes/smithing.json");
        download("https://github.com/pmmp/BedrockData/raw/master/recipes/shapeless_shulker_box.json",
                "src/main/resources/vanilla_recipes/shapeless_shulker_box.json");
        download("https://github.com/pmmp/BedrockData/raw/master/recipes/smelting.json",
                "src/main/resources/vanilla_recipes/smelting.json");
        download("https://github.com/pmmp/BedrockData/raw/master/recipes/special_hardcoded.json",
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

    //update item_2_tags.json
    private void updateItem2Tags(Path target) throws IOException {
        Map<String, Set<String>> test = new LinkedHashMap<>();
        for (var entry : ItemTag.getTag2Items().entrySet()) {
            for (var v : entry.getValue()) {
                test.computeIfAbsent(v, k -> new LinkedHashSet<>());
                test.get(v).add(entry.getKey());
            }
        }
        Files.writeString(target, gson.toJson(test), StandardCharsets.UTF_8);
        System.out.println("update item_2_tags.json success!");
    }

    //convert required_item_list.json to runtime_item_states.json
    private void convertRequiredItemList(Path target) throws IOException {
        var runtime_item_states = new ArrayList<RuntimeEntry>();
        Map<String, ?> map = gson.fromJson(new FileReader("./src/test/resources/org/powernukkit/updater/dumps/pmmp/required_item_list.json"), Map.class);
        map.forEach((k, v) -> {
            String runtime_id = ((Map) v).get("runtime_id").toString();
            int id = Double.valueOf(runtime_id).intValue();
            runtime_item_states.add(new RuntimeEntry(k, id));
        });
        Files.writeString(target, gson.toJson(runtime_item_states), StandardCharsets.UTF_8);
        System.out.println("convert required_item_list.json to runtime_item_states.json success!");
    }

    private void updateRecipes(Path source, Path target) throws IOException {
        //todo These are two wrong recipes, their output should not have `data`, if this bug is fixed, please remove it
        List<String> errorRecipes = List.of("dispenser.json", "dropper.json");
        Path recipes = source.resolve("behavior_pack/recipes");
        FileUtils.copyDirectory(recipes.toFile(), target.toFile());
        for (var fix : errorRecipes) {
            Path p = target.resolve(fix);
            Map<String, Object> data = gson.fromJson(new FileReader(p.toFile()), Map.class);
            Map<String, Object> recipe = (Map<String, Object>) data.get("minecraft:recipe_shaped");
            Map<String, Object> result = (Map<String, Object>) recipe.get("result");
            result.remove("data");
            recipe.put("result", result);
            data.put("minecraft:recipe_shaped", recipe);
            Files.writeString(p, gson.toJson(data), StandardCharsets.UTF_8);
        }
        System.out.println("update recipe success!");
    }

    private record RuntimeEntry(String name, int id) {
    }
}
