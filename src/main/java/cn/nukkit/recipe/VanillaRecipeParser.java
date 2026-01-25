package cn.nukkit.recipe;

import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.recipe.descriptor.ItemTagDescriptor;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.JSONUtils;
import cn.nukkit.utils.MapParsingUtils;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class VanillaRecipeParser {
    private static final String SHAPED_KEY = "minecraft:recipe_shaped";
    private static final String SHAPELESS_KEY = "minecraft:recipe_shapeless";
    private static final String FURNACE_KEY = "minecraft:recipe_furnace";
    private static final String BREW_KEY = "minecraft:recipe_brewing_mix";
    private static final String CONTAINER_KEY = "minecraft:recipe_brewing_container";

    private static final String STONE_CUTTER_TAG = "stonecutter";
    private static final String CRAFTING_TABLE_TAG = "crafting_table";
    private static final String SHULKER_BOX_TAG = "shulker_box";
    private static final String CARTOGRAPHY_TABLE_TAG = "cartography_table";
    private static final String FURNACE_TAG = "furnace";
    private static final String SMOKER_TAG = "smoker";
    private static final String BLAST_FURNACE_TAG = "blast_furnace";
    private static final String CAMPFIRE_TAG = "campfire";
    private static final String SOUL_CAMPFIRE_TAG = "soul_campfire";
    private static final String BREW_STAND_TAG = "brewing_stand";
    private static final Function<String, RuntimeException> RECIPE_ERROR =
            field -> new IllegalArgumentException("Invalid recipe data: " + field);

    public void parseAndRegisterRecipe(@NotNull File file) {
        try (var reader = new FileReader(file)) {
            matchAndParse(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void parseAndRegisterRecipe(@NotNull InputStream file) {
        try (var reader = new InputStreamReader(file)) {
            matchAndParse(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void matchAndParse(InputStreamReader reader) {
        Map<String, Object> map = JSONUtils.from(reader, new TypeToken<Map<String, Object>>() {
        });
        if (map.containsKey(SHAPED_KEY)) {
            parseAndRegisterShapedRecipe(MapParsingUtils.stringObjectMap(map.get(SHAPED_KEY), SHAPED_KEY, RECIPE_ERROR));
        } else if (map.containsKey(SHAPELESS_KEY)) {
            parseAndRegisterShapeLessRecipe(MapParsingUtils.stringObjectMap(map.get(SHAPELESS_KEY), SHAPELESS_KEY, RECIPE_ERROR));
        } else if (map.containsKey(FURNACE_KEY)) {
            parseAndRegisterFurnaceRecipe(MapParsingUtils.stringObjectMap(map.get(FURNACE_KEY), FURNACE_KEY, RECIPE_ERROR));
        } else if (map.containsKey(BREW_KEY)) {
            parseAndRegisterBrewRecipe(MapParsingUtils.stringObjectMap(map.get(BREW_KEY), BREW_KEY, RECIPE_ERROR));
        } else if (map.containsKey(CONTAINER_KEY)) {
            parseAndRegisterContainerRecipe(MapParsingUtils.stringObjectMap(map.get(CONTAINER_KEY), CONTAINER_KEY, RECIPE_ERROR));
        }
    }

    private void parseAndRegisterShapedRecipe(Map<String, Object> recipeData) {
        List<String> tags = tags(recipeData);
        if (tags.size() == 1 && tags.get(0).equals("crafting_table")) {
            int prior = (int) recipeData.getOrDefault("priority", 0);
            List<String> pattern = MapParsingUtils.stringList(recipeData.get("pattern"), "pattern", RECIPE_ERROR);
            String[] shapes;
            if (pattern.size() > 1) {
                int maxWidth = pattern.stream().map(s -> s.toCharArray().length).max(Integer::compare).get().intValue();
                shapes = pattern.stream().map(shape -> {
                    StringBuilder builder = new StringBuilder();
                    char[] charArray = shape.toCharArray();
                    for (char c : charArray) {
                        builder.append(c);
                    }
                    return builder.append(" ".repeat(Math.max(0, maxWidth - charArray.length))).toString();
                }).toArray(String[]::new);
            } else {
                shapes = pattern.toArray(String[]::new);
            }
            Map<String, Object> key = MapParsingUtils.stringObjectMap(recipeData.get("key"), "key", RECIPE_ERROR);
            final Map<Character, ItemDescriptor> ingredients = new LinkedHashMap<>();
            try {
                key.forEach((k, v) -> {
                    Map<String, Object> entry = MapParsingUtils.stringObjectMap(v, "key", RECIPE_ERROR);
                    if (entry.containsKey("tag")) {
                        var tag = entry.get("tag").toString();
                        int count = (int) entry.getOrDefault("count", 1);
                        ingredients.put(k.charAt(0), new ItemTagDescriptor(tag, count));
                    } else {
                        ingredients.put(k.charAt(0), new DefaultDescriptor(parseItem(entry)));
                    }
                });
                Object o = recipeData.get("result");
                Map<String, Object> result = Map.of();
                if (o instanceof Map<?, ?> map) {
                    result = MapParsingUtils.stringObjectMap(map, "result", RECIPE_ERROR);
                } else if (o instanceof List<?> list) {
                    if (!list.isEmpty()) {
                        result = MapParsingUtils.stringObjectMap(list.get(0), "result", RECIPE_ERROR);
                    }
                }
                Registries.RECIPE.register(new ShapedRecipe(description(recipeData), prior, parseItem(result), shapes, ingredients, List.of()));
            } catch (AssertionError ignore) {
            }
        }
    }

    private void parseAndRegisterShapeLessRecipe(Map<String, Object> recipeData) {
        int prior = (int) recipeData.getOrDefault("priority", 0);
        List<Map<String, Object>> ingredients = MapParsingUtils.stringObjectMapList(recipeData.get("ingredients"), "ingredients", RECIPE_ERROR);
        final List<ItemDescriptor> itemDescriptors = new ArrayList<>();
        try {
            ingredients.forEach(v -> {
                if (v.containsKey("tag")) {
                    var tag = v.get("tag").toString();
                    int count = (int) v.getOrDefault("count", 1);
                    itemDescriptors.add(new ItemTagDescriptor(tag, count));
                } else {
                    itemDescriptors.add(new DefaultDescriptor(parseItem(v)));
                }
            });
            Map<String, Object> result = MapParsingUtils.stringObjectMap(recipeData.get("result"), "result", RECIPE_ERROR);
            Item re = parseItem(result);
            List<String> tags = tags(recipeData);
            for (var tag : tags) {
                Recipe recipe = switch (tag) {
                    case CRAFTING_TABLE_TAG -> new ShapelessRecipe(description(recipeData), prior, re, itemDescriptors);
                    case SHULKER_BOX_TAG -> new UserDataShapelessRecipe(description(recipeData), prior, re, itemDescriptors);
                    case STONE_CUTTER_TAG ->
                            new StonecutterRecipe(description(recipeData), prior, re, itemDescriptors.get(0).toItem());
                    case CARTOGRAPHY_TABLE_TAG ->
                            new CartographyRecipe(description(recipeData), prior, re, itemDescriptors);
                    default -> throw new IllegalArgumentException(tag);
                };
                Registries.RECIPE.register(recipe);
            }
        } catch (AssertionError ignore) {
        }
    }

    private void parseAndRegisterFurnaceRecipe(Map<String, Object> recipeData) {
        Item input = Item.get(recipeData.get("input").toString());
        Item output = Item.get(recipeData.get("output").toString());
        if (input.isNull() || output.isNull()) {
            return;
        }
        List<String> tags = tags(recipeData);
        for (var tag : tags) {
            Recipe recipe = switch (tag) {
                case FURNACE_TAG -> new FurnaceRecipe(description(recipeData), output, input);
                case SMOKER_TAG -> new SmokerRecipe(description(recipeData), output, input);
                case BLAST_FURNACE_TAG -> new BlastFurnaceRecipe(description(recipeData), output, input);
                case CAMPFIRE_TAG, SOUL_CAMPFIRE_TAG -> new CampfireRecipe(description(recipeData), output, input);
                default -> throw new IllegalArgumentException(tag);
            };
            Registries.RECIPE.register(recipe);
        }
    }

    private void parseAndRegisterBrewRecipe(Map<String, Object> recipeData) {
        String inputID = "minecraft:" + recipeData.get("input").toString().split(":")[2].toLowerCase(Locale.ENGLISH);
        Item input = ItemPotion.fromPotion(PotionType.get(inputID));
        String outputID = "minecraft:" + recipeData.get("output").toString().split(":")[2].toLowerCase(Locale.ENGLISH);
        Item output = ItemPotion.fromPotion(PotionType.get(outputID));
        Item reagent = Item.get(recipeData.get("reagent").toString());
        registerBrewingRecipe(recipeData, input, output, reagent, false);
    }

    private void parseAndRegisterContainerRecipe(Map<String, Object> recipeData) {
        Item input = Item.get(recipeData.get("input").toString());
        Item output = Item.get(recipeData.get("output").toString());
        Item reagent = Item.get(recipeData.get("reagent").toString());
        registerBrewingRecipe(recipeData, input, output, reagent, true);
    }

    private void registerBrewingRecipe(Map<String, Object> recipeData, Item input, Item output, Item reagent, boolean isContainer) {
        if (input.isNull() || output.isNull() || reagent.isNull()) {
            return;
        }
        List<String> tags = tags(recipeData);
        if (!tags.isEmpty() && tags.get(0).equals(BREW_STAND_TAG)) {
            Recipe recipe = isContainer
                    ? new ContainerRecipe(description(recipeData), input, reagent, output)
                    : new BrewingRecipe(description(recipeData), input, reagent, output);
            Registries.RECIPE.register(recipe);
        }
    }

    private Item parseItem(Map<String, Object> v) throws AssertionError {
        String item = (String) v.get("item");
        int count = (int) v.getOrDefault("count", 1);
        int data = (int) v.getOrDefault("data", 32767);
        Item i = Item.get(item);
        if (i.isNull()) {
            throw new AssertionError();
        }
        if (data != 0) {
            if (data == 32767) {
                i = i.clone();
                i.disableMeta();
            } else {
                i.setDamage(data);
            }
        }
        i.setCount(count);
        return i;
    }

    private String description(Map<String, Object> recipeData) {
        return MapParsingUtils.stringStringMap(recipeData.get("description"), "description", RECIPE_ERROR).get("identifier");
    }

    private List<String> tags(Map<String, Object> recipeData) {
        return MapParsingUtils.stringList(recipeData.get("tags"), "tags", RECIPE_ERROR);
    }
}
