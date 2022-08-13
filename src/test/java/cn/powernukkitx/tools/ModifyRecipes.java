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
                //fix recipes for a variety of wool to recolor
                var input1 = castList(map.get("input"), Object.class);
                if (input1 != null) {
                    var listmap = castListMap(input1, String.class, Object.class);
                    if (listmap != null) {
                        if (listmap.size() == 2) {
                            var p1 = listmap.get(0).get("id");
                            var p2 = listmap.get(1).get("id");
                            int deyMeta1 = dyeDamage((String) listmap.get(0).get("id"));
                            int deyMeta2 = dyeDamage((String) listmap.get(1).get("id"));
                            if (deyMeta1 != -1 && (p2.equals("minecraft:wool"))) {
                                var output = castList(map.get("output"), Object.class);
                                if (output != null) {
                                    var target = castMap(output.get(0), String.class, Object.class);
                                    output.remove(0);
                                    target.put("damage", deyMeta1);
                                    output.add(target);
                                    map.put("output", output);
                                }
                            } else if ((p1.equals("minecraft:shulker_box") || p1.equals("minecraft:undyed_shulker_box")) && deyMeta2 != -1) {
                                var output = castList(map.get("output"), Object.class);
                                if (output != null) {
                                    var target = castMap(output.get(0), String.class, Object.class);
                                    output.remove(0);
                                    target.put("damage", deyMeta2);
                                    output.add(target);
                                    map.put("output", output);
                                }
                            }
                        } else if (listmap.size() == 9) {
                            int deyMeta = dyeDamage((String) listmap.get(0).get("id"));
                            var name = listmap.get(1).get("id");
                            if (deyMeta != -1 && name.equals("minecraft:sand")) {
                                var output = castList(map.get("output"), Object.class);
                                if (output != null) {
                                    var target = castMap(output.get(0), String.class, Object.class);
                                    output.remove(0);
                                    target.put("damage", deyMeta);
                                    output.add(target);
                                    map.put("output", output);
                                }
                            }
                        }
                    }
                }
                var input2 = castMap(map.get("input"), String.class, Object.class);
                if (input2 != null) {
                    if (input2.keySet().size() == 2 && input2.containsKey("A") && input2.containsKey("B")) {
                        var AA = castMap(input2.get("A"), String.class, Object.class);
                        var BB = castMap(input2.get("B"), String.class, Object.class);
                        if (AA != null && BB != null) {
                            var p1 = AA.get("id");
                            int p2 = dyeDamage((String) BB.get("id"));
                            if ((p1.equals("minecraft:glass") || p1.equals("minecraft:glass_pane")
                                    || p1.equals("minecraft:carpet") || p1.equals("minecraft:hardened_clay")) && p2 != -1) {
                                var output = castList(map.get("output"), Object.class);
                                if (output != null) {
                                    var target = castMap(output.get(0), String.class, Object.class);
                                    output.remove(0);
                                    target.put("damage", p2);
                                    output.add(target);
                                    map.put("output", output);
                                }
                            }
                        }
                    }
                }
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

    private static <K, V> List<Map<K, V>> castListMap(List<?> list, Class<K> kClass, Class<V> vClass) {
        List<Map<K, V>> result = new ArrayList<>();
        for (Object o : list) {
            var map = castMap(o, kClass, vClass);
            if (map != null) result.add(map);
        }
        if (result.isEmpty()) return null;
        return result;
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

    private static int dyeDamage(String name) {
        return switch (name) {
            case "minecraft:orange_dye" -> 1;
            case "minecraft:magenta_dye" -> 2;
            case "minecraft:light_blue_dye" -> 3;
            case "minecraft:yellow_dye" -> 4;
            case "minecraft:lime_dye" -> 5;
            case "minecraft:pink_dye" -> 6;
            case "minecraft:gray_dye" -> 7;
            case "minecraft:light_gray_dye" -> 8;//silver_dye
            case "minecraft:cyan_dye" -> 9;
            case "minecraft:purple_dye" -> 10;
            case "minecraft:blue_dye" -> 11;
            case "minecraft:cocoa_beans", "minecraft:brown_dye" -> 12;
            case "minecraft:green_dye" -> 13;
            case "minecraft:red_dye" -> 14;
            case "minecraft:ink_sac", "minecraft:black_dye" -> 15;
            default -> -1;
        };
    }
}


