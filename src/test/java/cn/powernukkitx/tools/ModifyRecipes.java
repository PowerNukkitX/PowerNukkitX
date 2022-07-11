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
        //fix recipes for a variety of logs and wood
        modify.put("minecraft:spruce_planks", 1);
        modify.put("minecraft:spruce_wooden_slab", 1);
        modify.put("minecraft:spruce_planks_from_wood", 1);
        modify.put("minecraft:spruce_planks_from_stripped", 1);
        modify.put("minecraft:spruce_planks_from_stripped_wood", 1);
        modify.put("minecraft:birch_planks", 2);
        modify.put("minecraft:birch_wooden_slab", 2);
        modify.put("minecraft:birch_planks_from_wood", 2);
        modify.put("minecraft:birch_planks_from_stripped", 2);
        modify.put("minecraft:birch_planks_from_stripped_wood", 2);
        modify.put("minecraft:jungle_planks", 3);
        modify.put("minecraft:jungle_wooden_slab", 3);
        modify.put("minecraft:jungle_planks_from_wood", 3);
        modify.put("minecraft:jungle_planks_from_stripped", 3);
        modify.put("minecraft:jungle_planks_from_stripped_wood", 3);
        modify.put("minecraft:acacia_planks", 4);
        modify.put("minecraft:acacia_wooden_slab", 4);
        modify.put("minecraft:acacia_planks_from_wood", 4);
        modify.put("minecraft:acacia_planks_from_stripped", 4);
        modify.put("minecraft:acacia_planks_from_stripped_wood", 4);
        modify.put("minecraft:dark_oak_planks", 5);
        modify.put("minecraft:dark_oak_wooden_slab", 5);
        modify.put("minecraft:dark_oak_planks_from_wood", 5);
        modify.put("minecraft:dark_oak_planks_from_stripped", 5);
        modify.put("minecraft:dark_oak_planks_from_stripped_wood", 5);
        //fix recipes for a variety of stone to wall
        modify.put("minecraft:mossy_cobblestone_wall", 1);
        modify.put("minecraft:stonecutter_mossy_cobblestone_wall", 1);
        modify.put("minecraft:granite_wall", 2);
        modify.put("minecraft:stonecutter_granite_wall", 2);
        modify.put("minecraft:diorite_wall", 3);
        modify.put("minecraft:stonecutter_diorite_wall", 3);
        modify.put("minecraft:andesite_wall", 4);
        modify.put("minecraft:stonecutter_andesite_wall", 4);
        modify.put("minecraft:sandstone_wall", 5);
        modify.put("minecraft:stonecutter_sandstone_wall", 5);
        modify.put("minecraft:brick_wall", 6);
        modify.put("minecraft:stonecutter_brick_wall", 6);
        modify.put("minecraft:stone_brick_wall", 7);
        modify.put("minecraft:stonecutter_stonebrick_wall", 7);
        modify.put("minecraft:stonecutter_stonebrick_wall2", 7);
        modify.put("minecraft:mossy_stone_brick_wall", 8);
        modify.put("minecraft:stonecutter_mossy_stonebrick_wall", 8);
        modify.put("minecraft:end_brick_wall", 9);
        modify.put("minecraft:stonecutter_endbrick_wall", 9);
        modify.put("minecraft:stonecutter_endbrick_wall2", 9);
        modify.put("minecraft:nether_brick_wall", 10);
        modify.put("minecraft:stonecutter_nether_brick_wall", 10);
        modify.put("minecraft:prismarine_wall", 11);
        modify.put("minecraft:stonecutter_prismarine_wall", 11);
        modify.put("minecraft:red_sandstone_wall", 12);
        modify.put("minecraft:stonecutter_red_sandstone_wall", 12);
        modify.put("minecraft:red_nether_brick_wall", 13);
        modify.put("minecraft:stonecutter_red_nether_brick_wall", 13);
        //fix recipes for a variety of stone to slab1
        modify.put("stoneslab_sandstone_heiroglyphs_recipeId", 1);
        modify.put("minecraft:stonecutter_sanddouble_stone_slab", 1);
        modify.put("Painting_VanillaBlocks_recipeId", 1);
        modify.put("Painting_Cobblestone_recipeId", 3);
        modify.put("minecraft:stonecutter_cobbledouble_stone_slab", 3);
        modify.put("StoneSlab_Brick_recipeId", 4);
        modify.put("minecraft:stonecutter_brick_slab", 4);
        modify.put("StoneSlab_StoneBrick_recipeId", 5);
        modify.put("minecraft:stonecutter_stonebrick_slab", 5);
        modify.put("minecraft:stonecutter_stonebrick_slab2", 5);
        modify.put("stoneslab_quartz_recipeId", 6);
        modify.put("minecraft:stonecutter_quartz_slab", 6);
        modify.put("Painting_NetherBrick_recipeId", 7);
        modify.put("minecraft:stonecutter_nether_brick_slab", 7);
        //fix recipes for a variety of stone to slab2
        modify.put("stoneslab2_purpur_recipeId", 1);
        modify.put("minecraft:stonecutter_purpur_slab", 1);
        modify.put("stoneslab_recipeId", 2);
        modify.put("minecraft:stonecutter_prismarine_slab", 2);
        modify.put("stoneslab2_prismarine_recipeId", 3);
        modify.put("minecraft:stonecutter_dark_prismarine_slab", 3);
        modify.put("stoneslab2_prismarine_bricks_recipeId", 4);
        modify.put("minecraft:stonecutter_prismarine_brick_slab", 4);
        modify.put("stoneslab2_recipeId", 5);
        modify.put("minecraft:stonecutter_mossy_cobbledouble_stone_slab", 5);
        modify.put("stoneslab2_smoothsandstone_smooth_recipeId", 6);
        modify.put("minecraft:stonecutter_smooth_sanddouble_stone_slab", 6);
        modify.put("stoneslab2_rednetherbrick_recipeId", 7);
        modify.put("minecraft:stonecutter_red_nether_brick_slab", 7);
        //fix recipes for a variety of stone to slab3
        modify.put("stoneslab3_smooth_recipeId", 1);
        modify.put("minecraft:stonecutter_smooth_red_sanddouble_stone_slab", 1);
        modify.put("stoneslab3_polished_andesite_andesitesmooth_recipeId", 2);
        modify.put("minecraft:stonecutter_polished_andesite_slab", 2);
        modify.put("minecraft:stonecutter_polished_andesite_slab2", 2);
        modify.put("stoneslab3_andesite_recipeId", 3);
        modify.put("minecraft:stonecutter_andesite_slab", 3);
        modify.put("stoneslab3_diorite_recipeId", 4);
        modify.put("minecraft:stonecutter_diorite_slab", 4);
        modify.put("stoneslab3_polished_diorite_dioritesmooth_recipeId", 5);
        modify.put("minecraft:stonecutter_polished_diorite_slab", 5);
        modify.put("minecraft:stonecutter_polished_diorite_slab2", 5);
        modify.put("stoneslab3_granite", 6);
        modify.put("minecraft:stonecutter_granite_slab", 6);
        modify.put("stoneslab3_polishedGranite_GraniteSmooth_recipeId", 7);
        modify.put("minecraft:stonecutter_polished_granite_slab", 7);
        modify.put("minecraft:stonecutter_polished_granite_slab2", 7);
        //fix recipes for a variety of stone to slab4
        modify.put("stoneslab4_smoothquartz_smooth_recipeId", 1);
        modify.put("minecraft:stonecutter_smooth_quartz_slab", 1);
        modify.put("StoneSlab4_recipeId", 2);
        modify.put("minecraft:stonecutter_double_stone_slab", 2);
        modify.put("stoneslab4_cut_sandstone_cut_recipeId", 3);
        modify.put("stoneslab4_cut_redsandstone_cut_recipeId", 4);
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
            System.out.println("OK!");
        } catch (IOException e) {
            System.out.println("ERROR!");
            throw new UncheckedIOException(e);
        }
    }

    private static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?> list) {
            for (Object o : list) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    private static <K, V> Map<K, V> castMap(Object obj, Class<K> kClass, Class<V> vClass) {
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


