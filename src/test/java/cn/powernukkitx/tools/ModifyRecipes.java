package cn.powernukkitx.tools;

import cn.nukkit.utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.powernukkit.updater.AllResourceUpdater.GSON;

public class ModifyRecipes {
    public static void main(String[] args) {
        modifyRecipes();
    }

    public static void modifyRecipes() {
        HashMap<String, Integer> modify = new HashMap<>();
        modify.put("minecraft:acacia_planks", 4);
        modify.put("minecraft:acacia_planks_from_stripped", 4);
        modify.put("minecraft:acacia_planks_from_stripped_wood", 4);
        modify.put("minecraft:acacia_planks_from_wood", 4);
        modify.put("minecraft:acacia_wooden_slab", 4);
        modify.put("minecraft:birch_planks", 2);
        modify.put("minecraft:birch_planks_from_stripped", 2);
        modify.put("minecraft:birch_planks_from_stripped_wood", 2);
        modify.put("minecraft:birch_planks_from_wood", 2);
        modify.put("minecraft:birch_wooden_slab", 2);
        modify.put("minecraft:dark_oak_planks", 5);
        modify.put("minecraft:dark_oak_planks_from_stripped", 5);
        modify.put("minecraft:dark_oak_planks_from_stripped_wood", 5);
        modify.put("minecraft:dark_oak_planks_from_wood", 5);
        modify.put("minecraft:dark_oak_wooden_slab", 5);
        modify.put("minecraft:jungle_planks", 3);
        modify.put("minecraft:jungle_planks_from_stripped", 3);
        modify.put("minecraft:jungle_planks_from_stripped_wood", 3);
        modify.put("minecraft:jungle_planks_from_wood", 3);
        modify.put("minecraft:jungle_wooden_slab", 3);
        modify.put("minecraft:spruce_planks", 1);
        modify.put("minecraft:spruce_planks_from_stripped", 1);
        modify.put("minecraft:spruce_planks_from_stripped_wood", 1);
        modify.put("minecraft:spruce_planks_from_wood", 1);
        modify.put("minecraft:spruce_wooden_slab", 1);
        Config config = new Config(Config.JSON);
        try (InputStream recipesStream = new FileInputStream("src/main/resources/recipes.json")) {
            if (recipesStream == null) {
                throw new AssertionError("Unable to find recipes.json");
            }
            config.loadAsJson(recipesStream, GSON);
            var newRecipes = config.getMapList("recipes").stream().peek(map -> {
                for (var id : modify.keySet()) {
                    if (map.get("id") != null && map.get("id").equals(id)) {
                        var output = castList(map.get("output"), Object.class);
                        var target = castMap(output.get(0), String.class, Object.class);
                        output.remove(0);
                        target.put("damage", modify.get(id));
                        output.add(target);
                        map.put("output", output);
                    }
                }
            }).toList();
            config.set("recipes", newRecipes);
            config.saveAsJson(new File("src/main/resources/recipes.json"), false, GSON);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?> list) {
            for (Object o : list) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    public static <K, V> Map<K, V> castMap(Object obj, Class<K> kClass, Class<V> vClass) {
        Map<K, V> result = new HashMap<>();
        if (obj instanceof Map<?, ?> map) {
            for (Object o : map.keySet()) {
                result.put(kClass.cast(o), vClass.cast(map.get(o)));
            }
            return result;
        }
        return null;
    }
}


