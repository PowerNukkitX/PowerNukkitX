package cn.powernukkitx.updater;

import cn.nukkit.inventory.ItemTag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@SuppressWarnings("unchecked")
public class UpdateResource {
    private static final String proxyHost = null;
    private static final int proxyPort = 0;

    Gson gson = new GsonBuilder().setPrettyPrinting()
            .setObjectToNumberStrategy(jsonReader -> Double.valueOf(jsonReader.nextString()).intValue())
            .disableHtmlEscaping()
            .create();

    /**
     * Pre-requisites:<br>
     * - Make sure PocketMine has released their exports: <a href="https://github.com/pmmp/BedrockData">BedrockData</a><br>
     */
    public static void main(String[] args) {
        new UpdateResource().execute();
    }

    @SneakyThrows
    private void execute() {
        downloadResources();
        updateItem2Tags(Path.of("src/main/resources/item_2_tags.json"));
        convertRequiredItemList(Path.of("src/main/resources/runtime_item_states.json"));
        updateRecipes();
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

    private void copyProxyPassResources(String pathProxyPassData) {
        copy(pathProxyPassData, "runtime_item_states.json", "src/main/resources/runtime_item_states.json");
    }

    @SneakyThrows
    private void copy(String path, String file, String into) {
        Files.copy(Paths.get(path).resolve(file), Paths.get(into), StandardCopyOption.REPLACE_EXISTING);
    }

    @SneakyThrows
    private void download(String url, String into) {
        var builder = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS);
        if (proxyHost != null) {
            builder.proxy(ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)));
        }
        var client = builder.build();
        var resp = client.send(HttpRequest.newBuilder().uri(URI.create(url)).
                header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")
                .GET().build(), HttpResponse.BodyHandlers.ofFile(Paths.get(into)));
        System.out.println(resp.statusCode());
    }

    private void updateRecipes() throws IOException {
        Path p = Path.of("src/main/resources/vanilla_recipes/shaped_crafting.json");
        //todo These are wrong recipes, their `result` should not have `data`, if this bug is fixed, please remove it
        Map<String, String> errorRecipes = Map.of(
                "minecraft:dispenser", "CgAAAxAAZmFjaW5nX2RpcmVjdGlvbgAAAAABDQB0cmlnZ2VyZWRfYml0AAA=",
                "minecraft:dropper", "CgAAAxAAZmFjaW5nX2RpcmVjdGlvbgAAAAABDQB0cmlnZ2VyZWRfYml0AAA=",
                "minecraft:piston", "CgAAAxAAZmFjaW5nX2RpcmVjdGlvbgAAAAAA",
                "minecraft:sticky_piston", "CgAAAxAAZmFjaW5nX2RpcmVjdGlvbgAAAAAA"
        );
        List<Map<String, Object>> data = gson.fromJson(new FileReader(p.toFile()), List.class);
        for (var fix : data) {
            List<Map<String, Object>> output = (List<Map<String, Object>>) fix.get("output");
            Map<String, Object> o = output.get(0);
            var name = o.get("name").toString();
            if (errorRecipes.containsKey(name)) {
                o.put("block_states", errorRecipes.get(name));
            }
        }
        Files.writeString(p, gson.toJson(data), StandardCharsets.UTF_8);
    }

    private record RuntimeEntry(String name, int id) {

    }
}
